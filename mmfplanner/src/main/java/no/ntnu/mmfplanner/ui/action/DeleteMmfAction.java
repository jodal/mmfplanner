/*
 * $Id: DeleteMmfAction.java 1403 2007-11-17 14:19:58Z erikbagg $
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.MainFrame;

/**
 *
 * Deletes the MMF(s) currently selected in mainFrame.mmfTable.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class DeleteMmfAction extends MainAbstractAction implements
        ListSelectionListener {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Delete MMF";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_D;

    public static final String ACTION_DESCRIPTION = "Delete the selected MMF";

    public DeleteMmfAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_MNEMONIC, null, ACTION_DESCRIPTION);
        mainFrame.getMmfTable().getSelectionModel().addListSelectionListener(
                this);
        valueChanged(null);
    }

    public void actionPerformed(ActionEvent e) {
        // Get mmfTable from mainFrame and find the selected rows,
        // then delete the corresponding mmfs from the current project
        JTable table = mainFrame.getMmfTable();
        Project project = mainFrame.getProject();
        int[] selectedRows = table.getSelectedRows();

        List<Mmf> mmfsToDelete = new ArrayList<Mmf>();
        for (int selectedRow : selectedRows) {
            // Not supported in JDK5
            // selectedRow = table.convertRowIndexToModel(selectedRow);
            if (selectedRow >= project.size()) {
                continue;
            }
            mmfsToDelete.add(project.get(selectedRow));
        }

        for (Mmf mmf : mmfsToDelete) {
            project.remove(mmf);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        JTable table = mainFrame.getMmfTable();
        boolean enabled = (table.getSelectedRowCount() > 0)
                && (table.getSelectedRow() < table.getRowCount() - 1);
        setEnabled(enabled);
    }
}
