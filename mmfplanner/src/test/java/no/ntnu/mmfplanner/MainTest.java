/*
 * $Id: MainTest.java 1406 2007-11-17 14:44:28Z erikbagg $
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

import java.awt.Frame;

import javax.swing.UIManager;

import no.ntnu.mmfplanner.ui.MainFrame;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for {@link Main}
 * 
 * @version $Revision: 1406 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
//FIXME: The test is disabled to get correct code coverage, also does not work without X -bagge
@Ignore
public class MainTest {

    @Test
    public void testMain() throws Exception {
        Frame[] frames = Frame.getFrames();
        int frameCount = frames.length;
        Main.main(null);
        java.awt.EventQueue.invokeAndWait(new Runnable() {
            public void run() {
            }
        });
        assertEquals(UIManager.getSystemLookAndFeelClassName(), UIManager
                .getLookAndFeel().getClass().getName());

        frames = Frame.getFrames();
        assertEquals(frameCount + 1, frames.length);
        assertEquals(MainFrame.class, frames[frameCount].getClass());
        MainFrame mainFrame = (MainFrame) frames[frameCount];
        assertTrue(mainFrame.isVisible());
        assertNotNull(mainFrame.getProject());
    }

}
