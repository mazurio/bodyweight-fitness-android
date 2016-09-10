package com.bodyweight.fitness.model

import com.bodyweight.fitness.persistence.Glacier
import java.io.Serializable
import java.util.*

abstract class LinkedRoutine : Serializable {
    abstract val title: String
    abstract val type: RoutineType
}

class Routine(JSONRoutine: JSONRoutine) : Serializable {
    var routineId: String = "routine0"
    var title: String = ""
    var subtitle: String = ""

    val categories = ArrayList<Category>()
    val sections = ArrayList<Section>()
    val exercises = ArrayList<Exercise>()
    val linkedExercises = ArrayList<Exercise>()
    val linkedRoutine = ArrayList<LinkedRoutine>()

    init {
        routineId = JSONRoutine.routineId
        title = JSONRoutine.title
        subtitle = JSONRoutine.subtitle

        var currentCategory: Category? = null
        var currentSection: Section? = null
        var currentExercise: Exercise? = null

        for (JSONLinkedRoutine in JSONRoutine.routine) {
            if (JSONLinkedRoutine.routineType === RoutineType.Category) {
                currentCategory = Category(JSONLinkedRoutine.categoryId, JSONLinkedRoutine.title)

                categories.add(currentCategory)
                linkedRoutine.add(currentCategory)
            } else if (JSONLinkedRoutine.routineType === RoutineType.Section) {
                currentSection = Section(
                        JSONLinkedRoutine.sectionId,
                        JSONLinkedRoutine.title,
                        JSONLinkedRoutine.description,
                        JSONLinkedRoutine.sectionMode)

                currentCategory!!.insertSection(currentSection)

                sections.add(currentSection)
                linkedRoutine.add(currentSection)
            } else if (JSONLinkedRoutine.routineType === RoutineType.Exercise) {
                val exercise = Exercise(
                        JSONLinkedRoutine.exerciseId,
                        JSONLinkedRoutine.level,
                        JSONLinkedRoutine.title,
                        JSONLinkedRoutine.description,
                        JSONLinkedRoutine.youTubeId,
                        JSONLinkedRoutine.videoId,
                        JSONLinkedRoutine.defaultSet)

                currentSection!!.insertExercise(exercise)

                exercises.add(exercise)

                if (currentSection.sectionMode === SectionMode.Levels || currentSection.sectionMode === SectionMode.Pick) {
                    val currentExerciseId = Glacier.get(currentSection.sectionId, String::class.java)

                    if (currentExerciseId != null) {
                        if (exercise.exerciseId.matches(currentExerciseId.toRegex())) {
                            linkedExercises.add(exercise)
                            linkedRoutine.add(exercise)

                            exercise.previous = currentExercise

                            if (currentExercise != null) {
                                currentExercise.next = exercise
                            }

                            currentExercise = exercise
                            currentSection.setCurrentLevel(exercise)
                        }
                    } else {
                        if (currentSection.exercises.size == 1) {
                            linkedExercises.add(exercise)
                            linkedRoutine.add(exercise)

                            exercise.previous = currentExercise

                            if (currentExercise != null) {
                                currentExercise.next = exercise
                            }

                            currentExercise = exercise
                        }
                    }
                } else {
                    exercise.previous = currentExercise

                    if (currentExercise != null) {
                        currentExercise.next = exercise
                    }

                    currentExercise = exercise

                    linkedExercises.add(exercise)
                    linkedRoutine.add(exercise)
                }
            }
        }
    }

    fun setLevel(exercise: Exercise, level: Int) {
        val currentSectionExercise = exercise.section!!.currentExercise

        if (currentSectionExercise != exercise) {
            if (currentSectionExercise.previous != null) {
                currentSectionExercise.previous!!.next = exercise
            }

            if (currentSectionExercise.next != null) {
                currentSectionExercise.next!!.previous = exercise
            }

            exercise.previous = currentSectionExercise.previous
            exercise.next = currentSectionExercise.next

            exercise.section!!.currentLevel = level

            currentSectionExercise.previous = null
            currentSectionExercise.next = null

            val indexOfCurrentExercise = linkedRoutine.indexOf(currentSectionExercise)
            if (indexOfCurrentExercise > -1) {
                linkedRoutine[indexOfCurrentExercise] = exercise
            }

            val indexOfLinkedExercise = linkedExercises.indexOf(currentSectionExercise)
            if (indexOfLinkedExercise > -1) {
                linkedExercises[indexOfLinkedExercise] = exercise
            }
        }
    }
}

class Category(
        val categoryId: String,
        val categoryTitle: String
) : LinkedRoutine(), Serializable {
    val sections = ArrayList<Section>()

    override val title: String = categoryTitle
    override val type: RoutineType = RoutineType.Category

    fun insertSection(section: Section) {
        section.category = this

        sections.add(section)
    }
}

class Section(
        val sectionId: String,
        val sectionTitle: String,
        val description: String,
        val sectionMode: SectionMode) : LinkedRoutine(), Serializable {

    var category: Category? = null
    var currentLevel = 0
        set(level) {
            if (sectionMode == SectionMode.Levels || sectionMode == SectionMode.Pick) {
                if (level < 0 || level >= exercises.size) {
                    return
                }

                field = level
            }
        }

    val exercises = ArrayList<Exercise>()

    override val title: String
        get() = sectionTitle

    override val type: RoutineType
        get() = RoutineType.Section

    fun setCurrentLevel(exercise: Exercise) {
        var current = 0
        for (ex in exercises) {
            if (ex == exercise) {
                currentLevel = current
                return
            }

            current++
        }
    }

    val availableLevels: Int
        get() {
            if (sectionMode == SectionMode.Levels || sectionMode == SectionMode.Pick) {
                return exercises.size
            } else {
                return 0
            }
        }

    fun insertExercise(exercise: Exercise) {
        exercise.category = category
        exercise.section = this

        exercises.add(exercise)
    }

    val currentExercise: Exercise
        get() = exercises[currentLevel]
}

class Exercise(
        val exerciseId: String,
        val level: String,
        val exerciseTitle: String,
        val description: String,
        val youTubeId: String,
        val videoId: String,
        val defaultSet: String) : LinkedRoutine(), Serializable {

    var category: Category? = null
    var section: Section? = null

    var previous: Exercise? = null
    var next: Exercise? = null

    override val title: String
        get() = exerciseTitle

    override val type: RoutineType
        get() = RoutineType.Exercise

    val isTimedSet: Boolean
        get() = defaultSet.equals("timed", ignoreCase = true)

    val hasProgressions: Boolean
        get() = section?.sectionMode != SectionMode.All

    val isPrevious: Boolean
        get() = previous != null

    val isNext: Boolean
        get() = next != null
}