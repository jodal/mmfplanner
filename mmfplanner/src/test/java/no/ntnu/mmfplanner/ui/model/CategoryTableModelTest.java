/*
 * $Id: CategoryTableModelTest.java 1025 2007-10-30 11:00:05Z jodal $
 * 
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui.model;

import static org.junit.Assert.*;

import java.awt.Color;

import no.ntnu.mmfplanner.ProjectTestFixture;

import org.junit.Before;
import org.junit.Test;

public class CategoryTableModelTest extends ProjectTestFixture {
    CategoryTableModel testModel;

    @Before
    public void setUp() throws Exception {
        testModel = new CategoryTableModel(project);
    }

    @Test
    public void testIsCellEditable() {
        for (int i = 0; i < testModel.getRowCount() - 1; i++) {
            assertEquals(true, testModel.isCellEditable(i, 0));
            assertEquals(true, testModel.isCellEditable(i, 1));
            assertEquals(true, testModel.isCellEditable(i, 2));
        }
        assertEquals(true, testModel.isCellEditable(
                testModel.getRowCount() - 1, 0));
        assertEquals(false, testModel.isCellEditable(
                testModel.getRowCount() - 1, 1));
        assertEquals(false, testModel.isCellEditable(
                testModel.getRowCount() - 1, 2));
    }

    @Test
    public void testGetColumnNameInt() {
        assertEquals(CategoryTableModel.COLUMN_NAME, testModel.getColumnName(0));
        assertEquals(CategoryTableModel.COLUMN_COLOR, testModel
                .getColumnName(1));
        assertEquals(CategoryTableModel.COLUMN_PARENT, testModel
                .getColumnName(2));
    }

    @Test
    public void testGetColumnCount() {
        assertEquals(3, testModel.getColumnCount());
    }

    @Test
    public void testGetRowCount() {
        assertEquals(project.getCategorySize() + 1, testModel.getRowCount());
    }

    /**
     * Checks that the last row is empty Other tests of getValueAt() is done
     * through tests of setValueAt()
     */
    @Test
    public void testGetValueAt() {
        assertEquals(null, testModel.getValueAt(testModel.getRowCount() - 1, 0));
        assertEquals(null, testModel.getValueAt(testModel.getRowCount() - 1, 1));
        assertEquals(null, testModel.getValueAt(testModel.getRowCount() - 1, 2));
    }

    @Test
    public void testGetColumnClassInt() {
        assertEquals(project.getCategory(1).getName().getClass(), testModel
                .getColumnClass(0));
        assertEquals(project.getCategory(1).getColor().getClass(), testModel
                .getColumnClass(1));
        assertEquals(project.getCategory(1).getParent().getClass(), testModel
                .getColumnClass(2));
    }

    @Test
    public void testSetValueAtObjectIntInt() {
        // Tests defaults values
        assertEquals("Online Travel Agency", testModel.getValueAt(0, 0));
        assertEquals(Color.RED, testModel.getValueAt(0, 1));
        assertEquals(null, testModel.getValueAt(0, 2));

        /*
         * Changes parent of the project in the second row to null, to avoid
         * circular parents
         */
        testModel.setValueAt(null, 1, 2);

        // Sets new values in the first project
        testModel.setValueAt(project.getCategory(1), 0, 2);
        testModel.setValueAt("Test", 0, 0);
        testModel.setValueAt(Color.BLUE, 0, 1);

        assertEquals(project.getCategory(1), project.getCategory(0).getParent());
        assertEquals("Test", project.getCategory(0).getName());
        assertEquals(Color.BLUE, project.getCategory(0).getColor());

        // Check that rowCount increases when a value is added to the last row
        assertEquals(3, testModel.getRowCount());
        assertEquals(2, project.getCategorySize());
        testModel.setValueAt("New category test", 2, 0);
        assertEquals(4, testModel.getRowCount());
        assertEquals(3, project.getCategorySize());
    }

}
