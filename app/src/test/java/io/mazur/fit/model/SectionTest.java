package io.mazur.fitness.model;

import org.junit.*;

import static org.junit.Assert.*;

public class SectionTest {
    @Test
    public void sectionIsCreatedTest() {
        Section section = new Section("Dynamic Stretches", SectionMode.ALL);

        assertEquals(section.getTitle(), "Dynamic Stretches");
        assertEquals(section.getSectionMode(), SectionMode.ALL);
    }

    @Test
    public void insertExercisesTest() {
        Section section = new Section("Dynamic Stretches", SectionMode.ALL);

        section.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        section.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        assertEquals(section.getExercises().size(), 2);

        assertEquals(section.getExercises().get(0).getId(), "exercise0");
        assertEquals(section.getExercises().get(0).getSection(), section);

        assertEquals(section.getExercises().get(1).getId(), "exercise1");
        assertEquals(section.getExercises().get(1).getSection(), section);
    }

    @Test
    public void setCategoryTest() {
        Section section = new Section("Dynamic Stretches", SectionMode.ALL);
        Category category = new Category("Category");

        section.setCategory(category);

        assertEquals(section.getCategory(), category);
    }

    @Test
    public void getDefaultLevelForAllTest() {
        Section section = new Section("Dynamic Stretches", SectionMode.ALL);

        section.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        section.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        assertEquals(section.getCurrentLevel(), 0);
    }

    @Test
    public void getDefaultLevelForLevelsTest() {
        Section section = new Section("Dynamic Stretches", SectionMode.LEVELS);

        section.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        section.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        assertEquals(section.getCurrentLevel(), 0);
    }

    @Test
    public void getDefaultLevelForPickOneTest() {
        Section section = new Section("Dynamic Stretches", SectionMode.PICK);

        section.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        section.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        assertEquals(section.getCurrentLevel(), 0);
    }

    @Test
    public void setLevelTest() {
        Section pickSection = new Section("Dynamic Stretches", SectionMode.PICK);

        pickSection.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        pickSection.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        pickSection.setCurrentLevel(1);

        assertEquals(pickSection.getCurrentLevel(), 0);
        assertEquals(pickSection.getCurrentExercise().getId(), "exercise0");

        Section levelsSection = new Section("Dynamic Stretches", SectionMode.LEVELS);

        levelsSection.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        levelsSection.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        assertEquals(levelsSection.getCurrentLevel(), 0);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise0");

        levelsSection.setCurrentLevel(1);

        assertEquals(levelsSection.getCurrentLevel(), 1);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise1");

        levelsSection.setCurrentLevel(2);

        assertEquals(levelsSection.getCurrentLevel(), 1);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise1");
    }

    @Test
    public void levelUpTest() {
        Section levelsSection = new Section("Dynamic Stretches", SectionMode.LEVELS);

        levelsSection.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        levelsSection.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));
        levelsSection.insertExercise(new Exercise("exercise2", "Exercise 2", "Description 2"));
        levelsSection.insertExercise(new Exercise("exercise3", "Exercise 3", "Description 3"));
        levelsSection.insertExercise(new Exercise("exercise4", "Exercise 4", "Description 4"));

        assertEquals(levelsSection.getCurrentLevel(), 0);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise0");

        levelsSection.levelUp();

        assertEquals(levelsSection.getCurrentLevel(), 1);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise1");

        levelsSection.levelUp();

        assertEquals(levelsSection.getCurrentLevel(), 2);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise2");

        levelsSection.levelUp();

        assertEquals(levelsSection.getCurrentLevel(), 3);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise3");

        levelsSection.levelUp();

        assertEquals(levelsSection.getCurrentLevel(), 4);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise4");

        levelsSection.levelUp();

        assertEquals(levelsSection.getCurrentLevel(), 4);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise4");

        levelsSection.levelDown();

        assertEquals(levelsSection.getCurrentLevel(), 3);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise3");
    }

    @Test
    public void levelDownTest() {
        Section levelsSection = new Section("Dynamic Stretches", SectionMode.LEVELS);

        levelsSection.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        levelsSection.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));
        levelsSection.insertExercise(new Exercise("exercise2", "Exercise 2", "Description 2"));
        levelsSection.insertExercise(new Exercise("exercise3", "Exercise 3", "Description 3"));
        levelsSection.insertExercise(new Exercise("exercise4", "Exercise 4", "Description 4"));

        assertEquals(levelsSection.getCurrentLevel(), 0);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise0");

        levelsSection.levelDown();

        assertEquals(levelsSection.getCurrentLevel(), 0);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise0");

        levelsSection.setCurrentLevel(2);

        assertEquals(levelsSection.getCurrentLevel(), 2);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise2");

        levelsSection.levelDown();

        assertEquals(levelsSection.getCurrentLevel(), 1);
        assertEquals(levelsSection.getCurrentExercise().getId(), "exercise1");
    }

    @Test
    public void availableLevelsTest() {
        Section levelsSection = new Section("Dynamic Stretches", SectionMode.LEVELS);

        levelsSection.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        levelsSection.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        assertEquals(levelsSection.getAvailableLevels(), 2);

        levelsSection.insertExercise(new Exercise("exercise2", "Exercise 2", "Description 2"));

        assertEquals(levelsSection.getAvailableLevels(), 3);

        Section pickSection = new Section("Dynamic Stretches", SectionMode.PICK);

        pickSection.insertExercise(new Exercise("exercise0", "Exercise 0", "Description 0"));
        pickSection.insertExercise(new Exercise("exercise1", "Exercise 1", "Description 1"));

        assertEquals(pickSection.getAvailableLevels(), 0);
    }
}
