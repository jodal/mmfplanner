/*
 * $Id: RevenueTableModelTest.java 1397 2007-11-17 13:55:32Z erikbagg $
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

import javax.swing.table.TableModel;

import no.ntnu.mmfplanner.ProjectTestFixture;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.MmfException;

import org.junit.Before;
import org.junit.Test;

/**
 * @version $Revision: 1397 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class RevenueTableModelTest extends ProjectTestFixture {

    TableModel model;

    @Before
    public void setUp() throws Exception {
        model = new RevenueTableModel(project);
    }

    @Test
    public void testIsCellEditable() {
        assertFalse(model.isCellEditable(0, 0));
        for (int i = 1; i < 13; i++) {
            assertTrue(model.isCellEditable(i, i));
        }
    }

    @Test
    public void testGetColumnCount() throws MmfException {
        assertEquals(13, model.getColumnCount());
        project.setPeriods(63);
        assertEquals(64, model.getColumnCount());
        project.setPeriods(1);
        assertEquals(2, model.getColumnCount());
    }

    @Test
    public void testGetColumnNameInt() {
        assertEquals(RevenueTableModel.COLUMN_MMF, model.getColumnName(0));
        for (int i = 1; i < 13; i++) {
            assertEquals("+" + i, model.getColumnName(i));
        }
    }

    @Test
    public void testGetRowCount() {
        assertEquals(2, model.getRowCount());
        project.add(new Mmf("C", "Test C"));
        assertEquals(3, model.getRowCount());
        project.remove(mmfA);
        project.remove(project.get(0));
        project.remove(project.get(0));
        assertEquals(0, model.getRowCount());
    }

    @Test
    public void testGetValueAt() {
        assertEquals("A: Test A", model.getValueAt(0, 0));
        for (int i = 1; i < 13; i++) {
            assertEquals(mmfA.getRevenue(i), model.getValueAt(0, i));
            int rev = (int) (i * Math.PI);
            mmfA.setRevenue(i, rev);
            assertEquals(rev, model.getValueAt(0, i));
        }
    }

    @Test
    public void testSetValueAtObjectIntInt() {
        model.setValueAt("T: T", 0, 0);
        assertEquals("A: Test A", model.getValueAt(0, 0));

        for (int i = 1; i < 13; i++) {
            int rev = (int) (i * Math.E);
            model.setValueAt(rev, 0, i);
            assertEquals(rev, mmfA.getRevenue(i));
        }
    }

    @Test
    public void testGetColumnClassInt() {
        assertEquals(String.class, model.getColumnClass(0));
        for (int i = 1; i < 13; i++) {
            assertEquals(Integer.class, model.getColumnClass(i));
        }
    }

}
