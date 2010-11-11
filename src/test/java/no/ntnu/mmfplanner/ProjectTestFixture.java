/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;

/**
 * Fixture for all tests that depend on a Project. Project is set up with two
 * MMFs, two categories, and other default values.
 */
public abstract class ProjectTestFixture {

    protected Project project;

    protected Mmf mmfA, mmfB;

    protected Category category1, category2;

    protected PropertyChangeEvent propEvt;

    protected PropertyChangeListener propChg = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            propCount++;
            propEvt = evt;
        }
    };

    protected int propCount = 0;

    @Before
    public void setUpFixture() throws Exception {
        project = new Project();
        project.setName("Test project");
        project.setPeriods(12);
        project.setInterestRate(0.008);

        category1 = new Category("Online Travel Agency", Color.RED, null);
        project.addCategory(category1);

        category2 = new Category("Trip Planner", Color.BLUE, category1);
        project.addCategory(category2);

        mmfA = new Mmf("A", "Test A");
        int revenues[] = new int[] { -200, -200, 100, 120, 140, 160, 200, 220,
                240, 300, 320, 340, 1000, 2000, 3000 };
        for (int i = 1; i <= revenues.length; i++) {
            mmfA.setRevenue(i, revenues[i - 1]);
        }
        mmfA.setPeriod(2);
        mmfA.setLocked(true);
        project.add(mmfA);

        mmfB = new Mmf("B", "Test B");
        mmfB.setPeriod(1);
        project.add(mmfB);
        mmfB.setCategory(category1);
        mmfB.addPrecursor(mmfA);

    }
}
