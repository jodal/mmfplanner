/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;

/**
 * This class is an adapter between project properties GUI and project
 * properties model. It listens to changes in both the model and the GUI
 */
public class ProjectPropertiesAdapter implements PropertyChangeListener,
        FocusListener, ActionListener {
    private Project project;
    private JFormattedTextField periodsTextField;
    private JTextField interestRateTextField;
    private JTextField projectNameTextField;
    private JFormattedTextField maxMmfsPerPeriodTextField;

    /**
     * Constructor for this class
     *
     * Sets the model, and registrers to the listeners
     *
     * @param periodsTextField
     * @param interestRateTextField
     * @param projectNameTextField
     * @param project The project we are working on
     */
    public ProjectPropertiesAdapter(JFormattedTextField periodsTextField,
            JTextField interestRateTextField, JTextField projectNameTextField,
            JFormattedTextField maxMmfsPerPeriodTextField, Project project) {

        // Set local properties
        this.periodsTextField = periodsTextField;
        this.interestRateTextField = interestRateTextField;
        this.projectNameTextField = projectNameTextField;
        this.maxMmfsPerPeriodTextField = maxMmfsPerPeriodTextField;

        // set model
        setModel(project);

        // Add itself as a listener to textfields
        periodsTextField.addFocusListener(this);
        periodsTextField.addActionListener(this);
        interestRateTextField.addFocusListener(this);
        interestRateTextField.addActionListener(this);
        projectNameTextField.addFocusListener(this);
        projectNameTextField.addActionListener(this);
        maxMmfsPerPeriodTextField.addFocusListener(this);
        maxMmfsPerPeriodTextField.addActionListener(this);

    }

    /**
     * Method for setting the model
     *
     * It also sets the textfields to the right state (with values)
     *
     * @param project
     */
    public void setModel(Project project) {
        if (null != this.project) {
            this.project.removePropertyChangeListener(this);
        }
        this.project = project;

        if (null != project) {
            project.addPropertyChangeListener(this);

            periodsTextField.setValue(project.getPeriods());
            interestRateTextField.setText(interestFormat(project
                    .getInterestRate()));
            projectNameTextField.setText(project.getName());
            maxMmfsPerPeriodTextField.setValue(project.getMaxMmfsPerPeriod());

        }
        updateComponent(null);
    }

    /**
     * @param interestRate
     * @return
     */
    private String interestFormat(double interestRate) {
        return Math.round(interestRate * 100000) / 1000.0 + "%";
    }

    public void focusGained(FocusEvent e) {

    }

    /**
     * Method for updating the model with values from GUI
     *
     * @param source The field that has changed
     */
    private void updateModel(Object source) {
        if (source == periodsTextField) {
            try {
                periodsTextField.commitEdit();
                int value = ((Number) periodsTextField.getValue()).intValue();
                project.setPeriods(value);
            } catch (MmfException e) {
                updateComponent(Project.EVENT_PERIODS);
            } catch (ParseException e) {
                updateComponent(Project.EVENT_PERIODS);
            }
        } else if (source == interestRateTextField) {
            String s = interestRateTextField.getText();
            Pattern p = Pattern.compile("([0-9]*)(?:[\\.,]([0-9]+))?.*");
            Matcher m = p.matcher(s);

            if (m.matches()) {
                s = m.group(1);
                if (m.group(2) != null) {
                    s += "." + m.group(2);
                }
                double value = Double.parseDouble(s) / 100.0;
                project.setInterestRate(value);
            }

            updateComponent(Project.EVENT_INTEREST_RATE);
        } else if (source == projectNameTextField) {
            project.setName(projectNameTextField.getText());
        } else if (source == maxMmfsPerPeriodTextField) {
            try {
                maxMmfsPerPeriodTextField.commitEdit();
                int value = ((Number) maxMmfsPerPeriodTextField.getValue())
                        .intValue();
                project.setMaxMmfsPerPeriod(value);
            } catch (MmfException e) {
                updateComponent(Project.EVENT_MAX_MMFS);
            } catch (ParseException e) {
                updateComponent(Project.EVENT_MAX_MMFS);
            }
        }
    }

    private void updateComponent(String event) {
        if (null != project) {
            if ((null == event) || Project.EVENT_PERIODS.equals(event)) {
                periodsTextField.setValue(project.getPeriods());
            } else if ((null == event)
                    || Project.EVENT_INTEREST_RATE.equals(event)) {
                interestRateTextField.setText(interestFormat(project
                        .getInterestRate()));
            } else if ((null == event) || Project.EVENT_NAME.equals(event)) {
                projectNameTextField.setText(project.getName());
            } else if ((null == event) || Project.EVENT_MAX_MMFS.equals(event)) {
                maxMmfsPerPeriodTextField.setValue(project
                        .getMaxMmfsPerPeriod());
            }
        }
    }

    public void focusLost(FocusEvent e) {
        updateModel(e.getSource());
    }

    public void actionPerformed(ActionEvent e) {
        updateModel(e.getSource());
    }

    /**
     * Method for setting the GUI right
     *
     * @param evt The event that has occured
     */
    public void propertyChange(PropertyChangeEvent evt) {
        updateComponent(evt.getPropertyName());
    }

}
