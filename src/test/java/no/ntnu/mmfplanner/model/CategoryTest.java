/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

import java.awt.Color;

import no.ntnu.mmfplanner.ProjectTestFixture;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for {@link Category}
 */
public class CategoryTest extends ProjectTestFixture {

    @Before
    public void setUp() throws Exception {
        category2.addPropertyChangeListener(propChg);
    }

    @Test
    public void setName() {
        assertEquals("Online Travel Agency", category1.getName());
        assertEquals("Trip Planner", category2.getName());

        String name = "Flight Booker";
        category2.setName(name);
        assertEquals(name, category2.getName());

        assertEquals(1, propCount);
        assertEquals(name, propEvt.getNewValue());
    }

    @Test
    public void setColor() {
        assertEquals(Color.RED, category1.getColor());
        assertEquals(Color.BLUE, category2.getColor());

        Color color = Color.PINK;
        category2.setColor(color);
        assertEquals(color, category2.getColor());

        assertEquals(1, propCount);
        assertEquals(color, propEvt.getNewValue());
    }

    @Test
    public void setParent() throws MmfException {
        assertEquals(null, category1.getParent());
        assertEquals(category1, category2.getParent());

        category2.setParent(null);
        assertEquals(null, category2.getParent());
        category2.setParent(category1);
        assertEquals(category1, category2.getParent());

        assertEquals(2, propCount);
        assertEquals(category1, propEvt.getNewValue());
    }

    @Test
    public void setParentToSelf() throws MmfException {
        try {
            category1.setParent(category1);
            fail("MmfException expected");
        } catch (MmfException e) {
        }
        assertEquals(null, category1.getParent());
    }

    @Test
    public void setParentToChild() throws MmfException {
        try {
            category1.setParent(category2);
            fail("MmfException expected");
        } catch (MmfException e) {
        }
        assertEquals(null, category1.getParent());
    }
}