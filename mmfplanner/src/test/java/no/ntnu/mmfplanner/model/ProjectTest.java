/*
 * $Id: ProjectTest.java 1403 2007-11-17 14:19:58Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.model;

import java.awt.Color;

import no.ntnu.mmfplanner.ProjectTestFixture;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * Test suite for model.Project.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class ProjectTest extends ProjectTestFixture {

    @Before
    public void setUp() throws Exception {
        project.addPropertyChangeListener(propChg);
    }

    @Test
    public void setName() {
        System.out.println("ProjectTest.setName()");

        String name = "MMF Planner";
        project.setName(name);
        assertEquals(name, project.getName());

        assertEquals(1, propCount);
        assertEquals(name, propEvt.getNewValue());
    }

    @Test
    public void setPeriods() throws MmfException {
        System.out.println("ProjectTest.setPeriods()");

        int periods = 6;
        project.setPeriods(periods);
        assertEquals(periods, project.getPeriods());

        assertEquals(1, propCount);
        assertEquals(periods, propEvt.getNewValue());
    }
    @Test
    public void setMaxMMFsPerPeriod() throws MmfException {
        System.out.println("ProjectTest.setMaxMMFsperPeriod()");

        int maxMMFs = 6;
        project.setMaxMmfsPerPeriod(maxMMFs);
        assertEquals(maxMMFs, project.getMaxMmfsPerPeriod());

        assertEquals(1, propCount);
        assertEquals(maxMMFs, propEvt.getNewValue());

    }

    @Test (expected=MmfException.class)
    public void setMaxMMFsPerPeriodFault() throws MmfException {
        System.out.println("ProjectTest.setMaxMMFsperPeriod()");

        int maxMMFs = -1;
        project.setMaxMmfsPerPeriod(maxMMFs);
        assertEquals(maxMMFs, project.getMaxMmfsPerPeriod());

    }

    @Test
    public void setInterestRate() {
        System.out.println("ProjectTest.setInterestRate()");

        double interestRate = 0.025;
        project.setInterestRate(interestRate);
        assertEquals(interestRate, project.getInterestRate(), 0.0001);

        assertEquals(1, propCount);
        assertEquals(interestRate, propEvt.getNewValue());
    }

    @Test
    public void addCategory() {
        System.out.println("ProjectTest.addCategory()");

        assertEquals(2, project.getCategorySize());
        Category category = new Category("Category 1", Color.RED, null);
        project.addCategory(category);
        assertEquals(category, project
                .getCategory(project.getCategorySize() - 1));
        assertEquals(3, project.getCategorySize());

        assertEquals(1, propCount);
        assertEquals(category, propEvt.getNewValue());
    }

    @Test
    public void removeCategory() {
        System.out.println("ProjectTest.removeCategory()");

        assertEquals(2, project.getCategorySize());
        Category category = project.getCategory(project.getCategorySize() - 1);
        project.removeCategory(category);
        assertEquals(1, project.getCategorySize());

        assertEquals(1, propCount);
        assertEquals(null, propEvt.getNewValue());
    }

    @Test
    public void add() {
        System.out.println("ProjectTest.add()");

        assertEquals(2, project.size());
        Mmf mmfC = new Mmf("C", "MMF C");
        project.add(mmfC);
        assertEquals(mmfC, project.get("C"));

        assertEquals(3, project.size());
        try {
            project.add(mmfC);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        assertEquals(3, project.size());

        assertEquals(1, propCount);
        assertEquals(mmfC, propEvt.getNewValue());
    }

    @Test
    public void getNextId() {
        System.out.println("ProjectTest.getNextId()");

        // we add D three times, first should be D, second C, third E
        testNewMmf("D", "D");
        testNewMmf("D", "C");
        testNewMmf("D", "E");

        // rest should use F-X
        for (char id = 'F'; id < 'Y'; id++) {
            testNewMmf(null, "" + id);
        }

        // remove B and U, then the next should use Y, B, U
        project.remove(project.get("B"));
        project.remove(project.get("U"));
        testNewMmf(null, "Y");
        testNewMmf("Y", "B");
        testNewMmf("B", "U");

        // remove L, next should use L
        project.remove(project.get("L"));
        testNewMmf(null, "L");

        // ZA-ZZY
        for (int i = 25; i < 25 * 3; i++) {
            String id = "ZZZ".substring(0, i / 25) + (char) ('A' + i % 25);
            testNewMmf(null, id);
        }

        // remove I and ZL, next should use I, ZL, ZZZA
        project.remove(project.get("ZL"));
        project.remove(project.get("I"));
        testNewMmf(null, "I");
        testNewMmf(null, "ZL");
        testNewMmf(null, "ZZZA");
    }

    private void testNewMmf(String id, String expected) {
        System.out.println("ProjectTest.testNewMmf()");

        Mmf mmf = new Mmf(id, "Test " + id + "=>" + expected);
        project.add(mmf);
        assertEquals(expected, mmf.getId());
        mmf = project.get(expected);
        assertEquals(expected, mmf.getId());
    }

    @Test
    public void remove() {
        System.out.println("ProjectTest.remove()");

        // Test get-by-id
        assertEquals(2, project.size());
        Mmf mmf1 = project.get("A");
        project.remove(mmf1);
        assertEquals(1, project.size());

        assertEquals(2, propCount); // both mmf and one precursor
        assertEquals(null, propEvt.getNewValue());

        project.add(mmf1);
        assertEquals(3, propCount);
        assertEquals(mmf1, propEvt.getNewValue());

        // Test get-by-index
        assertEquals(2, project.size());
        Mmf mmf2 = project.get(0);
        project.remove(mmf2);
        Mmf mmf3 = project.get(0);
        project.remove(mmf3);
        assertEquals(0, project.size());
        project.remove(mmf3);
        assertEquals(0, project.size());

        assertEquals(6, propCount);
        assertEquals(null, propEvt.getNewValue());
    }

    @Test
    public void testChangeListeners() {
        System.out.println("ProjectTest.testChangeListeners()");

        assertEquals(0, propCount);

        Category c = project.getCategory(0);
        c.setName("New name");
        assertEquals(1, propCount);
        project.removeCategory(c);
        assertEquals(4, propCount); // remove + 2*remove category
        c.setName("Newer name");
        assertEquals(4, propCount);
        project.addCategory(c);
        assertEquals(5, propCount);
        c.setName("Newest name");
        assertEquals(6, propCount);

        propCount = 0;
        Mmf mmf = project.get(0);
        mmf.setName("New name");
        assertEquals(1, propCount);
        project.remove(mmf);
        assertEquals(3, propCount); // remove + remove precursor
        mmf.setName("Newer name");
        assertEquals(3, propCount);
        project.add(mmf);
        assertEquals(4, propCount);
        mmf.setName("Newest name");
        assertEquals(5, propCount);

        propCount = 0;
        project.setName("new name");
        assertEquals(1, propCount);
        project.removePropertyChangeListener(propChg);
        project.setName("Newer name");
        assertEquals(1, propCount);
        project.addPropertyChangeListener(propChg);
        project.setName("Newest name");
        assertEquals(2, propCount);
    }

    @Test
    public void getSaNpvTable() {
        System.out.println("ProjectTest.getSaNpvTable()");

        int[][] table = project.getSaNpvTable();
        assertEquals(2, table.length);
        assertEquals(12, table[0].length);
    }
}