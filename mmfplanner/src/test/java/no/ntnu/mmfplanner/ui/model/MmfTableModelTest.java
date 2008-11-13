/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.model;

import static org.junit.Assert.*;

import javax.swing.table.TableModel;

import no.ntnu.mmfplanner.ProjectTestFixture;
import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link MmfTableModel}
 */
public class MmfTableModelTest extends ProjectTestFixture {

    TableModel model;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        model = new MmfTableModel(project);
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#isCellEditable(int, int)}.
     */
    @Test
    public void testIsCellEditable() {
        assertFalse(model.isCellEditable(0, 0));
        for (int i = 1; i < 5; i++) {
            assertTrue(model.isCellEditable(0, i));
        }
        assertFalse(model.isCellEditable(2, 0));
        assertTrue(model.isCellEditable(2, 1));
        assertFalse(model.isCellEditable(2, 2));
        assertFalse(model.isCellEditable(2, 5));
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#getColumnCount()}.
     */
    @Test
    public void testGetColumnCount() {
        assertEquals(6, model.getColumnCount());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#getColumnName(int)}.
     */
    @Test
    public void testGetColumnNameInt() {
        for (int i = 0; i < 6; i++) {
            assertEquals(MmfTableModel.COLUMNS[i], model.getColumnName(i));
        }
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#getRowCount()}.
     */
    @Test
    public void testGetRowCount() {
        assertEquals(3, model.getRowCount());
        project.add(new Mmf("C", "Test C"));
        assertEquals(4, model.getRowCount());
        project.remove(mmfA);
        project.remove(project.get(0));
        project.remove(project.get(0));
        assertEquals(1, model.getRowCount());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#getValueAt(int, int)}.
     */
    @Test
    public void testGetValueAt() {
        assertEquals("A", model.getValueAt(0, 0));
        assertEquals("Test A", model.getValueAt(0, 1));
        mmfA.setName("Test AA");
        assertEquals("Test AA", model.getValueAt(0, 1));

        assertEquals(2, model.getValueAt(0, 2));
        assertEquals(true, model.getValueAt(0, 3));

        assertEquals("A", model.getValueAt(1, 4));

        mmfB.removePrecursor(mmfA);
        assertEquals("", model.getValueAt(1, 4));

        assertEquals(null, model.getValueAt(0, 5));
        assertEquals(mmfB.getCategory(), model.getValueAt(1, 5));

        assertEquals(null, model.getValueAt(3, 0));
        assertEquals(null, model.getValueAt(3, 1));
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#setValueAt(java.lang.Object, int, int)}.
     */
    @Test
    public void testSetValueAtObjectIntInt() {
        Mmf mmfC = new Mmf("C", "Test C");
        project.add(mmfC);

        model.setValueAt("T", 0, 0);
        assertEquals("A", model.getValueAt(0, 0));

        model.setValueAt("Test AA", 0, 1);
        assertEquals("Test AA", mmfA.getName());

        model.setValueAt(1, 0, 2);
        assertEquals(1, mmfA.getPeriod());

        model.setValueAt("A,C", 1, 4);
        assertArrayEquals(new Mmf[] { mmfA, mmfC }, mmfB.getPrecursors()
                .toArray());
        mmfB.removePrecursor(mmfA);
        mmfB.removePrecursor(mmfC);
        model.setValueAt("A C", 1, 4);
        assertArrayEquals(new Mmf[] { mmfA, mmfC }, mmfB.getPrecursors()
                .toArray());
        mmfB.removePrecursor(mmfA);
        mmfB.removePrecursor(mmfC);
        model.setValueAt("AC", 1, 4);
        assertArrayEquals(new Mmf[] { mmfA, mmfC }, mmfB.getPrecursors()
                .toArray());

        model.setValueAt(category1, 0, 5);
        assertEquals(category1, mmfA.getCategory());

        // adding a new MMF
        assertEquals(4, model.getRowCount());
        assertEquals(3, project.size());
        model.setValueAt("Test New", 3, 1);
        assertEquals(4, project.size());
        assertEquals("Test New", project.get(3).getName());
        assertEquals(5, model.getRowCount());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#getColumnClass(int)}.
     */
    @Test
    public void testGetColumnClassInt() {
        assertEquals(String.class, model.getColumnClass(0));
        assertEquals(String.class, model.getColumnClass(1));
        assertEquals(Integer.class, model.getColumnClass(2));
        assertEquals(Boolean.class, model.getColumnClass(3));
        assertEquals(String.class, model.getColumnClass(4));
        assertEquals(Category.class, model.getColumnClass(5));
        project.remove(mmfA);
        project.remove(mmfB);
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.model.MmfTableModel#getColumnClass(int)}.
     */
    @Test
    public void testGetColumnClassWhenEmpty() {
        project.remove(mmfA);
        project.remove(mmfB);
        for (int i = 0; i < 5; i++) {
            assertEquals(Object.class, model.getColumnClass(0));
        }
        assertEquals(Category.class, model.getColumnClass(5));
    }
}
