/*
 * $Id: RoiTableModelTest.java 1403 2007-11-17 14:19:58Z erikbagg $
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
import no.ntnu.mmfplanner.model.ProjectRoi;

import org.junit.Before;
import org.junit.Test;

/**
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class RoiTableModelTest extends ProjectTestFixture {

    TableModel model;

    @Before
    public void setUp() throws Exception {
        model = new RoiTableModel(project, false);
    }

    @Test
    public void testIsCellEditable() {
        System.out.println("RoiTableModelTest.testIsCellEditable()");

        for (int i = 0; i < 40; i++) {
            assertFalse(model.isCellEditable(i, i));
        }
    }

    @Test
    public void testGetColumnCount() throws MmfException {
        System.out.println("RoiTableModelTest.testGetColumnCount()");

        assertEquals(14, model.getColumnCount());
        project.setPeriods(63);
        assertEquals(65, model.getColumnCount());
        project.setPeriods(1);
        assertEquals(3, model.getColumnCount());
    }

    @Test
    public void testGetColumnNameInt() {
        System.out.println("RoiTableModelTest.testGetColumnNameInt()");

        assertEquals(RoiTableModel.COLUMN_MMF, model.getColumnName(0));

        for (int i = 1; i < 13; i++) {
            assertEquals("" + (i), model.getColumnName(i));
        }
        assertEquals(RoiTableModel.COLUMN_NET, model.getColumnName(13));
    }

    @Test
    public void testGetRowCount() {
        System.out.println("RoiTableModelTest.testGetRowCount()");

        assertEquals(9, model.getRowCount());
        project.add(new Mmf("B", "Test B"));
        assertEquals(10, model.getRowCount());
        project.remove(mmfA);
        project.remove(mmfB);
        project.remove(project.get(0));
        assertEquals(7, model.getRowCount());
    }

    @Test
    public void testGetValueAt() {
        System.out.println("RoiTableModelTest.testGetValueAt()");

        // We only test values and rollingNpv. We already know ProjectRoi is
        // correct, so no need to test the actual values
        ProjectRoi roi = ProjectRoi.getRoiTable(project, project.getInterestRate(), false);
        for (int i = 0; i < roi.rollingNpv.length; i++) {
            assertEquals(roi.rollingNpv[i], model.getValueAt(7, i + 1));
        }

        for (int i = 0; i < roi.values.length; i++) {
            boolean isNull = true;
            for (int j = 0; j < roi.values[i].length; j++) {
                // System.out.println(isNull);
                // System.out.println(roi.values[i][j]);
                isNull = isNull && (0 == roi.values[i][j]);
                // System.out.println(isNull);
                if (isNull) {
                    assertNull(model.getValueAt(i, j + 1));
                } else {
                    assertEquals(roi.values[i][j], model.getValueAt(i, j + 1));
                }
            }
        }
    }
}
