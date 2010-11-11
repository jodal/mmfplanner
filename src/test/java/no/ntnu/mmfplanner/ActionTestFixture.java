/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;

import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.ui.action.MainAbstractAction;

/**
 * Fixture for *ActionTest.
 */
public abstract class ActionTestFixture extends ProjectTestFixture {

    protected MainFrame mainFrame;

    protected MainAbstractAction action;

    @Before
    public void setUpActionFixture() throws Exception {
        mainFrame = new MainFrame();
        mainFrame.setModel(project);
        assertEquals(project, mainFrame.getProject());
    }

    @After
    public void tearDownActionFixture() throws Exception {
        mainFrame.dispose();
    }
}
