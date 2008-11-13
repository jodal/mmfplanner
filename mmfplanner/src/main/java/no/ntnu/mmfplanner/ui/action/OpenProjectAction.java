/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.util.XmlDeserializer;

/**
 * Action for opening a project from file.
 */
public class OpenProjectAction extends MainAbstractAction {

    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Open Project...";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_O;

    public static final String ACTION_ACCELERATOR = "ctrl O";

    public static final String ACTION_DESCRIPTION = "Open a project";

    public OpenProjectAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_MNEMONIC, ACTION_ACCELERATOR,
                ACTION_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent evt) {
        boolean cancel = mainFrame.queryProjectCloseSave();
        if (cancel) {
            return;
        }

        // show a file chooser
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setMultiSelectionEnabled(false);
        fc.setAcceptAllFileFilterUsed(true);
        FileFilter filter = new MmfprojectFileFilter();
        fc.addChoosableFileFilter(filter);
        if (JFileChooser.APPROVE_OPTION != fc.showOpenDialog(mainFrame)) {
            return;
        }

        try {
            // serialize and write to file
            File file = fc.getSelectedFile();
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            Project project = XmlDeserializer.readProject(mainFrame.getTabPanePanelPlacement(), is);
            mainFrame.setModel(project);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame,
                    "An error occured while opening project:\n" + e, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
