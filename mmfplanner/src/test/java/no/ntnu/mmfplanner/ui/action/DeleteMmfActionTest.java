/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.action;

import static org.junit.Assert.*;

import javax.swing.JTable;

import no.ntnu.mmfplanner.ActionTestFixture;
import no.ntnu.mmfplanner.model.Mmf;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for class {@link DeleteMmfAction}
 */
//FIXME: The test is disabled to get correct code coverage, also does not work without X -bagge
@Ignore
public class DeleteMmfActionTest extends ActionTestFixture {

    @Before
    public void setUp() throws Exception {
        action = new DeleteMmfAction(mainFrame);
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.action.DeleteMmfAction#DeleteMmfAction(no.ntnu.mmfplanner.ui.MainFrame)}.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructWithNull() {
        new DeleteMmfAction(null);
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.ui.action.DeleteMmfAction#actionPerformed(java.awt.event.ActionEvent)}.
     */
    @Test
    public void testActionPerformed() {
        JTable table = mainFrame.getMmfTable();
        assertEquals(2, project.size());

        // add a new MMF and remove first two
        Mmf mmfC = new Mmf("C", "Test C");
        project.add(mmfC);
        table.getSelectionModel().addSelectionInterval(0, 1);
        action.actionPerformed(null);
        assertEquals(1, project.size());
        assertEquals(mmfC, project.get(0));

        // add B and A and remove first and last row (C and A)
        project.add(mmfB);
        project.add(mmfA);
        table.getSelectionModel().addSelectionInterval(0, 0);
        table.getSelectionModel().addSelectionInterval(2, 3);
        action.actionPerformed(null);
        assertEquals(1, project.size());
        assertEquals(mmfB, project.get(0));

        // neither of these should actually remove anything
        action.actionPerformed(null);
        assertEquals(1, project.size());
        table.getSelectionModel().addSelectionInterval(0, 1);
        table.clearSelection();
        assertEquals(1, project.size());
        table.getSelectionModel().addSelectionInterval(1, 1);
        assertEquals(1, project.size());

        // remove all
        project.add(mmfC);
        table.selectAll();
        action.actionPerformed(null);
        assertEquals(0, project.size());
    }

}
