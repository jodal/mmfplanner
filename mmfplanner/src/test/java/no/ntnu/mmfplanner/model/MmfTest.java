/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

import no.ntnu.mmfplanner.ProjectTestFixture;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for {@link Mmf}
 */
public class MmfTest extends ProjectTestFixture {

    @Before
    public void setUp() {
        mmfA.addPropertyChangeListener(propChg);
    }

    @Test
    public void setId() throws MmfException {
        assertEquals("A", mmfA.getId());
        mmfA.setId("C");
        assertEquals("C", mmfA.getId());

        try {
            mmfA.setId("B");
            fail("Expected MmfException");
        } catch (MmfException e) {
        }
        assertEquals("C", mmfA.getId());

        assertEquals(1, propCount);
        assertEquals("C", propEvt.getNewValue());
    }

    @Test
    public void setName() {
        assertEquals("Test A", mmfA.getName());
        mmfA.setName("Test B");
        assertEquals("Test B", mmfA.getName());

        assertEquals(1, propCount);
        assertEquals("Test B", propEvt.getNewValue());
    }

    @Test
    public void setPeriod() throws MmfException {
        assertEquals(2, mmfA.getPeriod());
        mmfA.setPeriod(5);
        assertEquals(5, mmfA.getPeriod());
        try {
            mmfA.setPeriod(0);
            fail("MmfException expected");
        } catch (MmfException e) {
        }
        assertEquals(5, mmfA.getPeriod());

        assertEquals(1, propCount);
        assertEquals(5, propEvt.getNewValue());
    }

    @Test
    public void setLocked() throws MmfException {
        assertEquals(true, mmfA.isLocked());
        mmfA.setLocked(true);
        assertEquals(true, mmfA.isLocked());
        mmfA.setLocked(false);
        assertEquals(false, mmfA.isLocked());

        assertEquals(1, propCount);
        assertEquals(false, propEvt.getNewValue());
    }

    @Test
    public void setSwimlane() throws MmfException {
        assertEquals(1, mmfA.getSwimlane());
        mmfA.setSwimlane(5);
        assertEquals(5, mmfA.getSwimlane());
        try {
            mmfA.setSwimlane(0);
            fail("MmfException expected");
        } catch (MmfException e) {
        }
        assertEquals(5, mmfA.getSwimlane());

        assertEquals(1, propCount);
        assertEquals(5, propEvt.getNewValue());
    }

    @Test
    public void precursors() throws MmfException {
        assertEquals(0, mmfA.getPrecursors().size());
        assertEquals(1, mmfB.getPrecursors().size());
        mmfB.removePrecursor(mmfA);
        assertEquals(0, mmfB.getPrecursors().size());

        mmfA.addPrecursor(mmfB);
        assertEquals(1, mmfA.getPrecursors().size());

        try {
            mmfA.addPrecursor(mmfA);
            fail("MmfException expected");
        } catch (MmfException e) {
        }
        assertEquals(1, mmfA.getPrecursors().size());

        try {
            mmfB.addPrecursor(mmfA);
            fail("MmfException expected");
        } catch (MmfException e) {
        }

        assertEquals(1, mmfA.getPrecursors().size());
        assertEquals(0, mmfB.getPrecursors().size());
        assertTrue(mmfA.getPrecursors().contains(mmfB));

        assertEquals(1, propCount);
        assertEquals(null, propEvt.getOldValue());
        assertEquals(mmfB, propEvt.getNewValue());

        mmfA.removePrecursor(mmfA);
        mmfA.removePrecursor(mmfA);
        assertEquals(1, mmfA.getPrecursors().size());
        assertTrue(mmfA.getPrecursors().contains(mmfB));

        assertEquals(1, propCount);

        mmfA.removePrecursor(mmfB);
        assertEquals(0, mmfA.getPrecursors().size());

        mmfA.removePrecursor(mmfB);
        assertEquals(0, mmfA.getPrecursors().size());
        assertFalse(mmfA.getPrecursors().contains(mmfB));

        assertEquals(2, propCount);
        assertEquals(mmfB, propEvt.getOldValue());
        assertEquals(null, propEvt.getNewValue());
    }

    @Test
    public void testToString() {
        assertEquals("MMF A: Test A [2,1] > []", mmfA.toString());
    }

    @Test
    public void setCategory() throws MmfException {
        assertEquals(null, mmfA.getCategory());

        mmfA.setCategory(category1);
        assertEquals(category1, mmfA.getCategory());

        try {
            Category testCategory = new Category("test",
                    Category.CATEGORY_COLORS[0], null);
            mmfA.setCategory(testCategory);
            fail("Expected MmfException");
        } catch (MmfException e) {
        }
        assertEquals(category1, mmfA.getCategory());

        assertEquals(1, propCount);
        assertEquals(category1, propEvt.getNewValue());
    }

    @Test
    public void revenues() throws MmfException {
        assertEquals(15, mmfA.getRevenueLength());

        mmfA.setRevenue(1, 22);
        assertEquals(22, mmfA.getRevenue(1));

        mmfA.setRevenue(25, 33);
        assertEquals(33, mmfA.getRevenue(25));
        assertEquals(0, mmfA.getRevenue(16));
        assertEquals(25, mmfA.getRevenueLength());
        project.setPeriods(10);
        assertEquals(25, mmfA.getRevenueLength());
        project.setPeriods(1);
        assertEquals(25, mmfA.getRevenueLength());
        assertEquals(33, mmfA.getRevenue(25));

        assertEquals(2, propCount);
        assertEquals(33, propEvt.getNewValue());
        assertEquals(0, propEvt.getOldValue());

    }

    @Test
    public void getSaNpv() {
        int npvs[] = new int[] { 1604, 1285, 986, 708, 486, 283, 101, -44,
                -170, -277, -365, -182 };

        for (int i = 0; i < npvs.length; i++) {
            assertEquals(npvs[i], mmfA.getSaNpv(0.008, i));
        }
        assertEquals(0, mmfA.getSaNpv(0.008, npvs.length + 1));
        assertEquals(0, mmfA.getSaNpv(0.008, npvs.length + 10));

        assertArrayEquals(npvs, mmfA.getSaNpvList(0.008));

        try {
            mmfA.getSaNpv(14.3, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }
}
