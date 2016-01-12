package com.bodyweight.fitness.model;

import com.bodyweight.fitness.model.json.JSONRoutine;

import java.io.Serializable;
import java.util.ArrayList;

import io.mazur.glacier.Glacier;

public class Routine implements Serializable {
    private ArrayList<Category> mCategories = new ArrayList<>();
    private ArrayList<Section> mSections = new ArrayList<>();
    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private ArrayList<Exercise> mLinkedExercises = new ArrayList<>();
    private ArrayList<LinkedRoutine> mLinkedRoutine = new ArrayList<>();

    public Routine(JSONRoutine JSONRoutine) {
        Category currentCategory = null;
        Section currentSection = null;
        Exercise currentExercise = null;

        for(com.bodyweight.fitness.model.json.JSONLinkedRoutine JSONLinkedRoutine : JSONRoutine.getPartRoutines()) {
            if(JSONLinkedRoutine.getType() == RoutineType.CATEGORY) {
                currentCategory = new Category(
                        JSONLinkedRoutine.getCategoryId(),
                        JSONLinkedRoutine.getTitle()
                );

                mCategories.add(currentCategory);

                /**
                 * Whether to show categories in the linked routine (e.g. Drawer).
                 */
                mLinkedRoutine.add(currentCategory);
            }

            /**
             * Updated sections for each category and add into linked routine.
             */
            else if(JSONLinkedRoutine.getType() == RoutineType.SECTION) {
                currentSection = new Section(
                        JSONLinkedRoutine.getSectionId(),
                        JSONLinkedRoutine.getTitle(),
                        JSONLinkedRoutine.getDescription(),
                        JSONLinkedRoutine.getMode()
                );

                currentCategory.insertSection(currentSection);

                mSections.add(currentSection);
                mLinkedRoutine.add(currentSection);
            }

            /**
             * Update exercises for each section, link them together.
             */
            else if(JSONLinkedRoutine.getType() == RoutineType.EXERCISE) {
                Exercise exercise = new Exercise(
                        JSONLinkedRoutine.getExerciseId(),
                        JSONLinkedRoutine.getId(),
                        JSONLinkedRoutine.getLevel(),
                        JSONLinkedRoutine.getTitle(),
                        JSONLinkedRoutine.getDescription(),
                        JSONLinkedRoutine.getYouTubeId(),
                        JSONLinkedRoutine.getDefaultSet()
                );

                /**
                 * Add exercise into a section.
                 */
                currentSection.insertExercise(exercise);

                /**
                 * List contains all exercises, no matter what level.
                 */
                mExercises.add(exercise);

                /**
                 * Add only first level of exercise when linking.
                 */
                if(currentSection.getSectionMode() == SectionMode.LEVELS || currentSection.getSectionMode() == SectionMode.PICK) {
                    String currentExerciseId = Glacier.get(currentSection.getSectionId(), String.class);

                    if(currentExerciseId != null) {
                        if(exercise.getExerciseId().matches(currentExerciseId)) {
                            mLinkedExercises.add(exercise);
                            mLinkedRoutine.add(exercise);

                            exercise.setPrevious(currentExercise);

                            if(currentExercise != null) {
                                currentExercise.setNext(exercise);
                            }

                            currentExercise = exercise;
                            currentSection.setCurrentLevel(exercise);
                        }
                    } else {
                        if(currentSection.getExercises().size() == 1) {
                            mLinkedExercises.add(exercise);
                            mLinkedRoutine.add(exercise);

                            exercise.setPrevious(currentExercise);

                            if(currentExercise != null) {
                                currentExercise.setNext(exercise);
                            }

                            currentExercise = exercise;
                        }
                    }
                } else {
                    exercise.setPrevious(currentExercise);

                    if(currentExercise != null) {
                        currentExercise.setNext(exercise);
                    }

                    currentExercise = exercise;

                    mLinkedExercises.add(exercise);
                    mLinkedRoutine.add(exercise);
                }
            }
        }
    }

    public ArrayList<Category> getCategories() {
        return mCategories;
    }

    public ArrayList<Section> getSections() {
        return mSections;
    }

    public ArrayList<Exercise> getExercises() {
        return mExercises;
    }

    public ArrayList<Exercise> getLinkedExercises() {
        return mLinkedExercises;
    }

    public void setLevel(Exercise exercise, int level) {
        Exercise currentSectionExercise = exercise.getSection().getCurrentExercise();

        if(currentSectionExercise != exercise) {
            if(currentSectionExercise.getPrevious() != null) {
                currentSectionExercise.getPrevious().setNext(exercise);
            }

            if(currentSectionExercise.getNext() != null) {
                currentSectionExercise.getNext().setPrevious(exercise);
            }

            exercise.setPrevious(currentSectionExercise.getPrevious());
            exercise.setNext(currentSectionExercise.getNext());

            exercise.getSection().setCurrentLevel(level);

            currentSectionExercise.setPrevious(null);
            currentSectionExercise.setNext(null);

            int indexOfCurrentExercise = mLinkedRoutine.indexOf(currentSectionExercise);
            if(indexOfCurrentExercise == -1) {
                return;
            }

            mLinkedRoutine.set(indexOfCurrentExercise, exercise);
        }
    }

    public ArrayList<LinkedRoutine> getLinkedRoutine() {
        return mLinkedRoutine;
    }
}
