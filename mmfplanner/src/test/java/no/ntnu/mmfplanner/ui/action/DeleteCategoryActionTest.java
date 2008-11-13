/*
 * $Id: DeleteCategoryActionTest.java 1408 2007-11-17 14:47:11Z erikbagg $
 * 
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui.action;

import static org.junit.Assert.*;

import javax.swing.JTable;

import no.ntnu.mmfplanner.ActionTestFixture;
import no.ntnu.mmfplanner.model.Category;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for {@link DeleteCategoryAction}
 *
 * @version $Revision: 1408 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
//FIXME: The test is disabled to get correct code coverage, also does not work without X -bagge
@Ignore
public class DeleteCategoryActionTest extends ActionTestFixture {

    @Before
    public void setUp() throws Exception {
        action = new DeleteCategoryAction(mainFrame);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructWithNull() throws Exception {
        new DeleteCategoryAction(null);
    }

    @Test
    public void testActionPerformed() {
        JTable table = mainFrame.getCategoryTable();
        assertEquals(2, project.getCategorySize());

        // add a new category and remove first two
        Category category3 = new Category();
        project.addCategory(category3);
        table.getSelectionModel().addSelectionInterval(0, 1);
        action.actionPerformed(null);
        assertEquals(1, project.getCategorySize());
        assertEquals(category3, project.getCategory(0));

        // add 1 and 2 and remove first and last row
        project.addCategory(category2);
        project.addCategory(category1);
        table.getSelectionModel().addSelectionInterval(0, 0);
        table.getSelectionModel().addSelectionInterval(2, 3);
        action.actionPerformed(null);
        assertEquals(1, project.getCategorySize());
        assertEquals(category2, project.getCategory(0));

        // neither of these should actually remove anything
        action.actionPerformed(null);
        assertEquals(1, project.getCategorySize());
        table.getSelectionModel().addSelectionInterval(0, 1);
        table.clearSelection();
        assertEquals(1, project.getCategorySize());
        table.getSelectionModel().addSelectionInterval(1, 1);
        assertEquals(1, project.getCategorySize());

        // remove all
        project.addCategory(category1);
        table.selectAll();
        action.actionPerformed(null);
        assertEquals(0, project.getCategorySize());
    }

}
