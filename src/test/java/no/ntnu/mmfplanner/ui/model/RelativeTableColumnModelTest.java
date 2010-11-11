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

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link RelativeTableColumnModel}
 */
public class RelativeTableColumnModelTest {

    TableColumnModel model;

    @Before
    public void setUp() {
        model = new RelativeTableColumnModel(new int[] { 0, -2, -100,
                -Integer.MAX_VALUE, 1, 4, 99 }, new int[] { 0, 200, 50, 10,
                400, 500, 900 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRelativeTableColumnModel() {
        model = new RelativeTableColumnModel(new int[] { 0, -2,
                -Integer.MAX_VALUE, 1, 4, 99 }, new int[] { 0, 200, 50, 10,
                400, 500, 900 });
    }

    @Test
    public void testResizeColumns() {
        assertEquals(0, model.getColumnCount());
        TableColumn c1 = new TableColumn(0);
        TableColumn c2 = new TableColumn(1);
        TableColumn c3 = new TableColumn(2);

        model.addColumn(c1);
        assertEquals(0, c1.getPreferredWidth());

        model.addColumn(c2);
        assertEquals(400, c2.getPreferredWidth());
        assertEquals(200, c1.getPreferredWidth());

        model.addColumn(c3);
        assertEquals(100, c3.getPreferredWidth());
        assertEquals(400, c2.getPreferredWidth());
        assertEquals(0, c1.getPreferredWidth());

        model.removeColumn(c3);
        assertEquals(400, c2.getPreferredWidth());
        assertEquals(200, c1.getPreferredWidth());
        assertEquals(2, model.getColumnCount());
    }

}
