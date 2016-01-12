package io.mazur.fitness.model;

import org.junit.*;

import static org.junit.Assert.*;

public class CategoryTest {
    @Test
    public void categoryIsCreatedTest() {
        Category category = new Category("Warmup");

        assertEquals(category.getTitle(), "Warmup");
    }

    @Test
    public void insertSectionsTest() {
        Category category = new Category("Warmup");

        Section section0 = new Section("Dynamic Stretches", SectionMode.ALL);
        Section section1 = new Section("Dynamic Warmup", SectionMode.PICK);
        Section section2 = new Section("Android", SectionMode.LEVELS);

        category.insertSection(section0);
        category.insertSection(section1);
        category.insertSection(section2);

        assertEquals(category.getSections().size(), 3);
        assertEquals(category.getSections().get(0), section0);
        assertEquals(category.getSections().get(1), section1);
        assertEquals(category.getSections().get(2), section2);
    }
}
