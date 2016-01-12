package io.mazur.fitness.model;

import org.junit.*;

import static org.junit.Assert.*;

public class ExerciseTest {
    @Test
    public void exerciseIsCreatedTest() {
        Exercise exercise = new Exercise("wall_extensions", "Wall Extensions", "5 - 10 reps");

        assertEquals(exercise.getId(), "wall_extensions");
        assertEquals(exercise.getTitle(), "Wall Extensions");
        assertEquals(exercise.getDescription(), "5 - 10 reps");
    }

    @Test
    public void setCategoryTest() {
        Exercise exercise = new Exercise("wall_extensions", "Wall Extensions", "5 - 10 reps");
        Category category = new Category("Category");

        exercise.setCategory(category);

        assertEquals(exercise.getCategory(), category);
    }

    @Test
    public void setSectionTest() {
        Exercise exercise = new Exercise("wall_extensions", "Wall Extensions", "5 - 10 reps");
        Section section = new Section("Section", SectionMode.ALL);

        exercise.setSection(section);

        assertEquals(exercise.getSection(), section);
    }
}
