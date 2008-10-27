/*
 * $Id: SaveProjectAction.java 1403 2007-11-17 14:19:58Z erikbagg $
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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.util.XmlSerializer;

/**
 * Serializes the current project to XML and saves it to disk.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class SaveProjectAction extends MainAbstractAction {

    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Save Project...";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_S;

    public static final String ACTION_ACCELERATOR = "ctrl S";

    public static final String ACTION_DESCRIPTION = "Save the current project";

    public SaveProjectAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_MNEMONIC, ACTION_ACCELERATOR,
                ACTION_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent evt) {
        save();
    }

    public boolean save() {
        // show a file chooser
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setMultiSelectionEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        FileFilter filter = new MmfprojectFileFilter();
        fc.addChoosableFileFilter(filter);
        if (JFileChooser.APPROVE_OPTION != fc.showSaveDialog(mainFrame)) {
            return false;
        }

        // check that the file ends with .mmfproject and doesn't exist
        File file = fc.getSelectedFile();
        if (file == null) {
            return false;
        } else if (!filter.accept(file)) {
            file = new File(file.getPath() + MmfprojectFileFilter.SUFFIX);
        }
        if (file.exists()) {
            int yesno = JOptionPane
                    .showConfirmDialog(
                            mainFrame,
                            "The file '"
                                    + file.getName()
                                    + "' already exists. Do you want to replace the existing file?",
                            "File exists", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION != yesno) {
                return false;
            }
        }

        try {
            // serialize and write to file
            OutputStream os = new BufferedOutputStream(new FileOutputStream(
                    file));
            XmlSerializer.writeWorkspace(mainFrame.getTabPanePanelPlacement(),
                    mainFrame.getProject(), os);
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame,
                    "An error occured while saving project:\n" + e, "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
