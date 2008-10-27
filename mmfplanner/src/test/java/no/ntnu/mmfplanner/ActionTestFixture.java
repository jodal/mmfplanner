/*
 * $Id: ActionTestFixture.java 1397 2007-11-17 13:55:32Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;

import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.ui.action.MainAbstractAction;

/**
 * Fixture for *ActionTest.
 *
 * @version $Revision: 1397 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
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
