/*
 * $Id: GuiUtilTest.java 1397 2007-11-17 13:55:32Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.util;

import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * Test suite for util.GuiUtil.
 *
 * @version $Revision: 1397 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class GuiUtilTest {
    @Test
    public void testGetColorName() {
        System.out.println("GuiUtilTest.testGetColorName()");

        Color colors[] = new Color[] { Color.BLACK, Color.WHITE,
                new Color(1, 240, 15), TangoColor.BUTTER_1,
                TangoColor.ALUMINIUM_6 };
        String names[] = new String[] { "000000", "FFFFFF", "01F00F",
                "Butter 1", "Aluminium 6" };
        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], GuiUtil.getColorName(colors[i]));
        }
    }

    @Test(expected = NullPointerException.class)
    public void testGetColorNameWithNull() {
        System.out.println("GuiUtilTest.testGetColorNameWithNull()");

        GuiUtil.getColorName(null);
    }

    @Test
    public void getBrightness() {
        System.out.println("GuiUtilTest.getBrightness()");

        assertEquals((255 * 299) / 1000, GuiUtil.getGrayscale(Color.RED));
        assertEquals((255 * 587) / 1000, GuiUtil.getGrayscale(Color.GREEN));
        assertEquals((255 * 114) / 1000, GuiUtil.getGrayscale(Color.BLUE));
    }

    @Test
    public void getBlackWhiteColor() {
        System.out.println("GuiUtilTest.getBlackWhiteColor()");

        assertEquals(Color.BLACK, GuiUtil.getBlackWhiteColor(Color.WHITE));
        assertEquals(Color.BLACK, GuiUtil.getBlackWhiteColor(Color.YELLOW));
        assertEquals(Color.BLACK, GuiUtil.getBlackWhiteColor(Color.LIGHT_GRAY));
        assertEquals(Color.WHITE, GuiUtil.getBlackWhiteColor(Color.BLACK));
        assertEquals(Color.WHITE, GuiUtil.getBlackWhiteColor(Color.BLUE));
        assertEquals(Color.WHITE, GuiUtil.getBlackWhiteColor(Color.RED));
        assertEquals(Color.BLACK, GuiUtil.getBlackWhiteColor(Color.GREEN));
        assertEquals(Color.WHITE, GuiUtil.getBlackWhiteColor(new Color(0x00,
                0x80, 0x00))); // dark green
    }
}