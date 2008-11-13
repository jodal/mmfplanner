/*
 * $Id: ProjectPropertiesAdapterTest.java 1397 2007-11-17 13:55:32Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui.model;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import no.ntnu.mmfplanner.ProjectTestFixture;
import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;

import org.junit.Before;
import org.junit.Test;

/**
 * @version $Revision: 1397 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class ProjectPropertiesAdapterTest extends ProjectTestFixture {

    ProjectPropertiesAdapter propAdapter;
    private JFormattedTextField periodsTextField;
    private JTextField interestRateTextField;
    private JTextField projectNameTextField;
    private JFormattedTextField maxMmfsPerPeriodTextField;

    @Before
    public void setUp() throws Exception {
        // Create and set up JTextFields
        periodsTextField = new JFormattedTextField(project.getPeriods());
        interestRateTextField = new JTextField("" + project.getInterestRate());
        projectNameTextField = new JTextField(project.getName());
        maxMmfsPerPeriodTextField = new JFormattedTextField(project
                .getMaxMmfsPerPeriod());

        // Initialize the adapter
        propAdapter = new ProjectPropertiesAdapter(periodsTextField,
                interestRateTextField, projectNameTextField,
                maxMmfsPerPeriodTextField, project);
    }

    @Test
    public void testSetModel() throws MmfException {
        assertEquals(12, periodsTextField.getValue());
        assertEquals("0.8%", interestRateTextField.getText());
        assertEquals("Test project", projectNameTextField.getText());
        assertEquals(1, maxMmfsPerPeriodTextField.getValue());

        Project expectedNewProject = new Project();

        expectedNewProject.setPeriods(14);
        expectedNewProject.setInterestRate(0.0112345);
        expectedNewProject.setName("New Project to Test");
        expectedNewProject.setMaxMmfsPerPeriod(2);

        propAdapter.setModel(expectedNewProject);

        assertEquals(14, periodsTextField.getValue());
        assertEquals("1.123%", interestRateTextField.getText());
        assertEquals("New Project to Test", projectNameTextField.getText());
        assertEquals(2, maxMmfsPerPeriodTextField.getValue());

        periodsTextField.setValue(13);
        propAdapter.focusLost(new FocusEvent(periodsTextField,
                FocusEvent.FOCUS_LOST));
        assertEquals(12, project.getPeriods());
        assertEquals(13, expectedNewProject.getPeriods());

    }

    @Test
    public void testFocusLost() {
        assertEquals(12, project.getPeriods());
        periodsTextField.setValue(13);
        propAdapter.focusLost(new FocusEvent(periodsTextField,
                FocusEvent.FOCUS_LOST));
        assertEquals(13, project.getPeriods());

        assertEquals(0.008, project.getInterestRate(), 0.00001);
        interestRateTextField.setText("12");
        propAdapter.focusLost(new FocusEvent(interestRateTextField,
                FocusEvent.FOCUS_LOST));
        assertEquals(0.12, project.getInterestRate(), 0.00001);
        interestRateTextField.setText("0,01234¤/&#/263265");
        propAdapter.focusLost(new FocusEvent(interestRateTextField,
                FocusEvent.FOCUS_LOST));
        assertEquals(0.0001234, project.getInterestRate(), 0.00000001);

        assertEquals("Test project", project.getName());
        projectNameTextField.setText("MMF Test Project");
        propAdapter.focusLost(new FocusEvent(projectNameTextField,
                FocusEvent.FOCUS_LOST));
        assertEquals("MMF Test Project", project.getName());

        assertEquals(1, project.getMaxMmfsPerPeriod());
        maxMmfsPerPeriodTextField.setValue(2);
        propAdapter.focusLost(new FocusEvent(maxMmfsPerPeriodTextField,
                FocusEvent.FOCUS_LOST));
        assertEquals(2, project.getMaxMmfsPerPeriod());

    }

    @Test
    public void testActionPerformed() {
        assertEquals(12, project.getPeriods());
        periodsTextField.setValue(13);
        propAdapter.actionPerformed(new ActionEvent(periodsTextField,
                ActionEvent.ACTION_PERFORMED, "update"));
        assertEquals(13, project.getPeriods());

        assertEquals(0.008, project.getInterestRate(), 0.00001);
        interestRateTextField.setText("1.4%");
        propAdapter.actionPerformed(new ActionEvent(interestRateTextField,
                ActionEvent.ACTION_PERFORMED, "update"));
        assertEquals(0.014, project.getInterestRate(), 0.00001);

        assertEquals("Test project", project.getName());
        projectNameTextField.setText("MMF Test Project");
        propAdapter.actionPerformed(new ActionEvent(projectNameTextField,
                ActionEvent.ACTION_PERFORMED, "update"));
        assertEquals("MMF Test Project", project.getName());

        assertEquals(1, project.getMaxMmfsPerPeriod());
        maxMmfsPerPeriodTextField.setValue(2);
        propAdapter.actionPerformed(new ActionEvent(maxMmfsPerPeriodTextField,
                ActionEvent.ACTION_PERFORMED, "update"));
        assertEquals(2, project.getMaxMmfsPerPeriod());

    }

    @Test
    public void testPropertyChange() throws MmfException {
        assertEquals(0.008, project.getInterestRate(), 0.00001);
        project.setInterestRate(0.01);
        assertEquals("1.0%", interestRateTextField.getText());

        assertEquals(12, project.getPeriods());
        int expectedNewPeriod = 10;
        project.setPeriods(expectedNewPeriod);
        assertEquals(expectedNewPeriod, ((Number) periodsTextField.getValue())
                .intValue());

        assertEquals("Test project", project.getName());
        String expectedNewProjectName = "MMF test project";
        project.setName(expectedNewProjectName);
        assertEquals(expectedNewProjectName, projectNameTextField.getText());

        assertEquals(1, project.getMaxMmfsPerPeriod());
        int expectedNewMaxMMFs = 2;
        project.setMaxMmfsPerPeriod(expectedNewMaxMMFs);
        assertEquals(expectedNewMaxMMFs, ((Number) maxMmfsPerPeriodTextField
                .getValue()).intValue());

    }

}
