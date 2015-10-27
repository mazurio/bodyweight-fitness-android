package io.mazur.fit.model;

import java.io.Serializable;
import java.util.ArrayList;

import io.mazur.glacier.Glacier;

public class Routine implements Serializable {
    /**
     * Categories are treated as top level parent for the sections and exercises.
     */
    private ArrayList<Category> mCategories = new ArrayList<>();

    /**
     * All sections put together inside a list.
     */
    private ArrayList<Section> mSections = new ArrayList<>();

    /**
     * All exercises put together inside a list, used to link exercises after changes.
     */
    private ArrayList<Exercise> mExercises = new ArrayList<>();

    /**
     * Linked exercises for switching between them. This could and should be implemented as an
     * independent screen manager later which allows linking different views together
     * (e.g. rest view or summary).
     */
    private ArrayList<Exercise> mLinkedExercises = new ArrayList<>();

    /**
     * Linked routine for displaying views in the recycler view as we can just request
     * type of the object.
     */
    private ArrayList<LinkedRoutine> mLinkedRoutine = new ArrayList<>();

    public Routine(JSONRoutine JSONRoutine) {
        Category currentCategory = null;
        Section currentSection = null;
        Exercise currentExercise = null;

        for(io.mazur.fit.model.JSONRoutine.JSONLinkedRoutine JSONLinkedRoutine : JSONRoutine.getPartRoutines()) {
            /**
             * Update categories in both lists.
             */
            if(JSONLinkedRoutine.getType() == RoutineType.CATEGORY) {
                currentCategory = new Category(JSONLinkedRoutine.getTitle());

                mCategories.add(currentCategory);

                /**
                 * TODO: We avoid categories from Linked Routine for now.
                 */
//                mLinkedRoutine.add(currentCategory);
            }

            /**
             * Updated sections for each category and add into linked routine.
             */
            else if(JSONLinkedRoutine.getType() == RoutineType.SECTION) {
                currentSection = new Section(
                        JSONLinkedRoutine.getTitle(),
                        JSONLinkedRoutine.getDescription(),
                        JSONLinkedRoutine.getMode());

                currentCategory.insertSection(currentSection);

                mSections.add(currentSection);
                mLinkedRoutine.add(currentSection);
            }

            /**
             * Update exercises for each section, link them together.
             */
            else if(JSONLinkedRoutine.getType() == RoutineType.EXERCISE) {
                Exercise exercise = new Exercise(
                        JSONLinkedRoutine.getId(),
                        JSONLinkedRoutine.getLevel(),
                        JSONLinkedRoutine.getTitle(),
                        JSONLinkedRoutine.getDescription());

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
                    String currentExerciseId = Glacier.get(currentSection.getTitle(), String.class);

                    if(currentExerciseId != null) {
                        if(exercise.getId().matches(currentExerciseId)) {
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

    /**
     * Set level of the exercise in given section.
     */
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
