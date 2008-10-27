/*
 * $Id: Main.java 1397 2007-11-17 13:55:32Z erikbagg $
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

import no.ntnu.mmfplanner.ui.MainFrame;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * The Main class sets the GUI look-and-feel like the system you are using
 * And starts the program with threads and test data
 *
 * @version $Revision: 1397 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class Main {

    /**
     * Set system look and feel. This must be called before any frames are
     * created.
     */
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setLookAndFeel();
                MainFrame m = new MainFrame();
                //m.initTestData1();
                m.initTestData2();
                m.setVisible(true);
            }
        });
    }
}
