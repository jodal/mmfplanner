/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.model.ProjectRoi;

/**
 * This is a class for displaying the Return On Investment for our project in a
 * own table
 *
 * If the project changes the roiTable is set to null, it is only calculated if
 * the user points to its tab and wants to see it
 *
 * @see no.ntnu.mmfplanner.model.Project#getRoiTable() Here is the actual
 *      calculations done
 */
public class RoiTableModel extends AbstractTableModel implements
        PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    public final static String COLUMN_MMF = "MMF";
    public final static String COLUMN_NET = "Net";
    public final static String ROW_NAMES[] = new String[] { "Cash",
            "Investment", "ROI", "Self-funding", "Present value",
            "Rolling NPV", "Break-even" };

    private Project project;

    private ProjectRoi roi;

    private Object tableData[][];

    private boolean waterfall;

    /**
     * Constructor for class
     *
     * @param project
     */
    public RoiTableModel(Project project, boolean waterfall) {
        super();
        this.project = project;
        this.waterfall = waterfall;
        project.addPropertyChangeListener(this);
    }

    public int getColumnCount() {
        return project.getPeriods() + 2;
    }

    public int getRowCount() {
        return project.size() + 7;
    }

    /**
     * Method for determining the name of the column
     *
     * @param column The number of the column determines the name
     */
    @Override
    public String getColumnName(int column) {
        if (0 == column) {
            return COLUMN_MMF;
        } else if (getColumnCount() - 1 == column) {
            return COLUMN_NET;
        } else {
            return "" + (column);
        }
    }

    /**
     * Method for displaying the numbers. Calls the generateTableData
     *
     *
     * @see #generateTableData()
     * @param row
     * @param column
     *
     */
    public Object getValueAt(int row, int column) {
        generateTableData();
        return tableData[row][column];
    }

    /**
     * Method for getting values to the table. Calls the calculation method and
     * prints the values in the right formatting to display the right things in
     * this table
     *
     */
    private void generateTableData() {
        if ((null == project) || (null != roi)) {
            // we have no project yet, or the table is up to date
            return;
        }
        roi = ProjectRoi.getRoiTable(project, project.getInterestRate(), waterfall);
        int periods = project.getPeriods();
        int mmfCount = project.size();

        Object data[][] = new Object[mmfCount + 8][periods + 2];

        for (int i = 0; i < roi.mmfs.length; i++) {
            data[i][0] = roi.mmfs[i].getId() + ": " + roi.mmfs[i].getName();
        }
        for (int i = 0; i < ROW_NAMES.length; i++) {
            data[mmfCount + i][0] = ROW_NAMES[i];
        }
        // interestRate
        data[mmfCount + 4][0] = ROW_NAMES[4] + " "
                + Math.round(roi.interestRate * 10000) / 100.0 + "%";
        data[mmfCount + 5][0] = ROW_NAMES[5] + " "
                + Math.round(roi.interestRate * 10000) / 100.0 + "%";

        // values[][];
        for (int r = 0; r < roi.values.length; r++) {
            int p = 0;
            for (; p < roi.values[r].length; p++) {
                if (roi.values[r][p] != 0) {
                    break;
                }
            }
            for (; p < roi.values[r].length; p++) {
                data[r][p + 1] = roi.values[r][p];
            }
        }

        // cash[], investment[], presentValue[], rollingNpv[];
        for (int p = 0; p <= periods; p++) {
            data[mmfCount + 0][p + 1] = roi.cash[p];
            if (roi.investment[p] != 0) {
                data[mmfCount + 1][p + 1] = roi.investment[p];
            }
            data[mmfCount + 4][p + 1] = roi.presentValue[p];
            if (p < periods) {
                data[mmfCount + 5][p + 1] = roi.rollingNpv[p];
            }
        }

        // roi;
        data[mmfCount + 2][periods + 1] = Math.round(roi.roi * 100) + "%";
        // selfFundingPeriod;
        if (roi.selfFundingPeriod > 0) {
            data[mmfCount + 3][roi.selfFundingPeriod] = "X";
        }
        // breakevenPeriod;
        if (roi.breakevenPeriod > 0) {
            data[mmfCount + 6][roi.breakevenPeriod] = "X";
        }
        // breakevenRegression;
        data[mmfCount + 6][periods + 1] = Math
                .round(roi.breakevenRegression * 100) / 100.0;

        tableData = data;
    }

    /**
     * Method for changes. Sets ROI to null since we may need to calculate the
     * ROI once again
     */
    public void propertyChange(PropertyChangeEvent evt) {
        roi = null;
        if (Project.EVENT_PERIODS.equals(evt.getPropertyName())) {
            fireTableStructureChanged();
        } else {
            fireTableDataChanged();
        }
    }
}
