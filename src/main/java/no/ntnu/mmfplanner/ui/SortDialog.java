/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.model.ProjectSorter;
import no.ntnu.mmfplanner.model.ProjectSorter.Result;
import no.ntnu.mmfplanner.ui.model.SortTableModel;
import no.ntnu.mmfplanner.util.ProjectSorterUtil;

/**
 * Dialog that displays the progress of a {@link ProjectSorter} while running.
 * Also allows aborting a sort in progress, as well as selecting a result to be
 * used as the new project sequence.
 */
public class SortDialog extends javax.swing.JDialog implements ActionListener,
        ListSelectionListener {
    private static final long serialVersionUID = 1L;

    private static final NumberFormat NUMBER_FORMAT = NumberFormat
            .getInstance();

    private ProjectSorter sorter;
    private Timer progressTimer;
    private SortTableModel sortTableModel;

    int initialMmfPeriod[];
    int initialMmfSwimlane[];
    String initialSequence;

    /** Creates new form SortDialog */
    public SortDialog(java.awt.Frame parent, boolean modal, ProjectSorter sorter) {
        super(parent, modal);
        this.sorter = sorter;
        initialMmfPeriod = new int[sorter.getProject().size()];
        initialMmfSwimlane = new int[sorter.getProject().size()];
        for (int i = 0; i < sorter.getProject().size(); i++) {
            initialMmfPeriod[i] = sorter.getProject().get(i).getPeriod();
            initialMmfSwimlane[i] = sorter.getProject().get(i).getSwimlane();
        }

        Result r = new Result();
        r.periods = initialMmfPeriod;
        sorter.updateSequence(r);
        initialSequence = r.sequence;

        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        sortProgress.setStringPainted(true);

        sortTableModel = new SortTableModel(sorter, initialSequence);
        resultTable.setModel(sortTableModel);
        resultTable.getSelectionModel().addListSelectionListener(this);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setEnabled(false);

        progressTimer = new Timer(500, this);
        progressTimer.start();
        actionPerformed(null);
    }

    public void actionPerformed(ActionEvent e) {
        long divideBy = Math.max(1, sorter.getProgressMax() / 1000);
        long progress = sorter.getProgress();
        long progressMax = sorter.getProgressMax();
        sortProgress.setMaximum((int) (progressMax / divideBy));
        sortProgress.setValue((int) (progress / divideBy));

        String str = NUMBER_FORMAT.format(progress) + " of "
                + NUMBER_FORMAT.format(progressMax);
        if (sorter.isDone()) {
            if (progress < progressMax) {
                sortProgress.setString("Aborted (" + str + ")");
            } else {
                sortProgress.setString("Finished (" + str + ")");
            }
            progressTimer.stop();
            closeButton.setEnabled(true);
            abortButton.setEnabled(false);
            resultTable.setEnabled(true);
        } else {
            sortProgress.setString(str);
        }
        sortTableModel.update();
    }

    /**
     * Occurs when a row has been selected in the result table
     */
    public void valueChanged(ListSelectionEvent evt) {
        // only handle row selection if the sorter is done
        if (sorter.isDone()) {
            // find the selected row and associated result
            int selectedRow = resultTable.getSelectedRow();
            Project project = sorter.getProject();
            int unusedPeriod = project.getPeriods() + 1;

            if (selectedRow == 0) {
                for (int i = 0; i < sorter.getProject().size(); i++) {
                    try {
                        sorter.getProject().get(i).setPeriod(
                                initialMmfPeriod[i]);
                        sorter.getProject().get(i).setSwimlane(
                                initialMmfSwimlane[i]);
                    } catch (MmfException e) {
                        e.printStackTrace();
                    }

                }
                // result = sorter.getInitialResult();
            } else {
                ProjectSorter.Result result = sorter.getResults().get(
                        selectedRow - 1);
                // update the mmf periods
                for (int i = 0; i < result.periods.length; i++) {
                    int mmfPeriod = (result.periods[i] == 0 ? unusedPeriod
                            : result.periods[i]);
                    try {
                        project.get(i).setPeriod(mmfPeriod);
                    } catch (MmfException e) {
                        e.printStackTrace();
                    }
                }
                ProjectSorterUtil.sortSwimlanes(project);
            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {

        sortProgress = new javax.swing.JProgressBar();
        abortButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        resultTablePane = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sort result");

        abortButton.setMnemonic('A');
        abortButton.setText("Abort");
        abortButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abortButtonActionPerformed(evt);
            }
        });

        closeButton.setMnemonic('C');
        closeButton.setText("Close");
        closeButton.setEnabled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        resultTablePane.setViewportView(resultTable);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout
                .setHorizontalGroup(layout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                GroupLayout.Alignment.TRAILING,
                                layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.TRAILING)
                                                        .addComponent(
                                                                resultTablePane,
                                                                GroupLayout.Alignment.LEADING,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                379,
                                                                Short.MAX_VALUE)
                                                        .addGroup(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                sortProgress,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                285,
                                                                                Short.MAX_VALUE)
                                                                        .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                abortButton)
                                                                        .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                closeButton)))
                                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                        resultTablePane, GroupLayout.DEFAULT_SIZE, 284,
                        Short.MAX_VALUE).addPreferredGap(
                        LayoutStyle.ComponentPlacement.RELATED).addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.LEADING).addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE)
                                        .addComponent(closeButton)
                                        .addComponent(abortButton))
                                .addComponent(sortProgress,
                                        GroupLayout.PREFERRED_SIZE, 24,
                                        GroupLayout.PREFERRED_SIZE))
                        .addContainerGap()));

        pack();
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {
        sorter.setStopFlag();
        abortButton.setEnabled(false);
    }

    private javax.swing.JButton abortButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JTable resultTable;
    private javax.swing.JScrollPane resultTablePane;
    private javax.swing.JProgressBar sortProgress;
}
