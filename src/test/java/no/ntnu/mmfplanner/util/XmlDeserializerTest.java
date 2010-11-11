/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import no.ntnu.mmfplanner.ProjectTestFixture;
import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.model.ProjectRoi;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement.PanelInfo;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link XmlDeserializer}
 */
public class XmlDeserializerTest extends ProjectTestFixture {

    TabPanePanelPlacement placement;

    @Before
    public void setUp() {
        placement = new TabPanePanelPlacement(new JMenuItem());
        placement.add("projectPropPanel", "Project Properties",
                TabPanePanelPlacement.TYPE_INPUT,
                TabPanePanelPlacement.PLACEMENT_UPPER, true, new JPanel());
        placement.add("mmfTablePanel", "MMF Table",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_LOWER, false, new JPanel());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlDeserializer#readProject(java.io.InputStream)}
     * .
     * 
     * @throws IOException
     * @throws MmfException
     */
    @Test
    public void testReadProject() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XmlSerializer.writeWorkspace(placement, project, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Project project2 = XmlDeserializer.readProject(is);
        assertEquals(project.getCategorySize(), project2.getCategorySize());
        assertEquals(project.getInterestRate(), project2.getInterestRate(),
                0.0001);
        assertEquals(project.getName(), project2.getName());
        assertEquals(project.getNextId(), project2.getNextId());
        assertEquals(project.getPeriods(), project2.getPeriods());
        assertEquals(project.getMaxMmfsPerPeriod(), project2
                .getMaxMmfsPerPeriod());
        assertArrayEquals(project.getSaNpvTable(), project2.getSaNpvTable());

        ProjectRoi roi = ProjectRoi.getRoiTable(project, project
                .getInterestRate(), false);
        ProjectRoi roi2 = ProjectRoi.getRoiTable(project2, project2
                .getInterestRate(), false);

        assertEquals(roi.mmfs.length, roi2.mmfs.length);
        for (int i = 0; i < roi.mmfs.length; i++) {
            assertEquals(roi.mmfs[i].getId(), roi2.mmfs[i].getId());
        }
        assertArrayEquals(roi.values, roi2.values);
        assertArrayEquals(roi.cash, roi2.cash);
        assertArrayEquals(roi.investment, roi2.investment);
        assertArrayEquals(roi.presentValue, roi2.presentValue);
        assertArrayEquals(roi.rollingNpv, roi2.rollingNpv);

        assertEquals(roi.roi, roi2.roi, 0.0001);
        assertEquals(roi.selfFundingPeriod, roi2.selfFundingPeriod);
        assertEquals(roi.breakevenPeriod, roi2.breakevenPeriod);
        assertEquals(roi.breakevenRegression, roi2.breakevenRegression, 0.0001);
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlDeserializer#elementToProject(nu.xom.Element)}
     * .
     * 
     * @throws MmfException
     */
    @Test
    public void testElementToProject() throws MmfException {
        Element e = XmlSerializer.projectToElement(project);
        Project project2 = XmlDeserializer.elementToProject(e);
        assertEquals(project.getCategorySize(), project2.getCategorySize());
        assertEquals(project.getInterestRate(), project2.getInterestRate(),
                0.0001);
        assertEquals(project.getName(), project2.getName());
        assertEquals(project.getNextId(), project2.getNextId());
        assertEquals(project.getMaxMmfsPerPeriod(), project2
                .getMaxMmfsPerPeriod());

        assertArrayEquals(project.getSaNpvTable(), project2.getSaNpvTable());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlDeserializer#elementToCategory(nu.xom.Element)}
     * .
     * 
     * @throws MmfException
     */
    @Test
    public void testElementToCategory() throws MmfException {
        Element e = XmlSerializer.categoryToElement(project, category1);
        Category category12 = XmlDeserializer.elementToCategory(project, e);
        assertEquals(category1.getColor(), category12.getColor());
        assertEquals(category1.getName(), category12.getName());
        assertEquals(category1.getParent(), category12.getParent());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlDeserializer#elementToMmf(nu.xom.Element)}
     * .
     * 
     * @throws MmfException
     */
    @Test
    public void testElementToMmf() throws MmfException {
        mmfA.setLocked(true);
        Element e = XmlSerializer.mmfToElement(project, mmfA);
        Mmf mmfA2 = XmlDeserializer.elementToMmf(project, e);
        assertEquals(mmfA.getId(), mmfA2.getId());
        assertEquals(mmfA.getName(), mmfA2.getName());
        assertEquals(mmfA.getPeriod(), mmfA2.getPeriod());
        assertEquals(mmfA.isLocked(), mmfA2.isLocked());
        assertEquals(mmfA.getPeriodCount(), mmfA2.getPeriodCount());
        assertEquals(mmfA.getSwimlane(), mmfA2.getSwimlane());
        assertEquals(mmfA.getCategory(), mmfA2.getCategory());
        assertEquals(mmfA.getPrecursors(), mmfA2.getPrecursors());
        assertEquals(mmfA.getRevenueLength(), mmfA2.getRevenueLength());
        for (int i = 1; i <= mmfA.getRevenueLength(); i++) {
            assertEquals(mmfA.getRevenue(i), mmfA2.getRevenue(i));
        }
        assertEquals(mmfA.getProject(), mmfA2.getProject());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlDeserializer#elementToTabPanePanelPlacement(nu.xom.Element)}
     * .
     * 
     * @throws MmfException
     */
    @Test
    public void testElementToTabPanePanelPlacement() throws MmfException {
        Element e = XmlSerializer.tabPanePanelPlacementToElement(placement);
        placement.movePanel("projectPropPanel",
                TabPanePanelPlacement.PLACEMENT_LOWER);
        placement.setVisible("projectPropPanel", false);
        placement.movePanel("mmfTablePanel",
                TabPanePanelPlacement.PLACEMENT_UPPER);
        placement.setVisible("mmfTablePanel", true);

        XmlDeserializer.elementToTabPanePanelPlacement(placement, e);

        PanelInfo p1 = placement.getPanelInfo("projectPropPanel");
        PanelInfo p2 = placement.getPanelInfo("mmfTablePanel");

        assertEquals(p1.placement, TabPanePanelPlacement.PLACEMENT_UPPER);
        assertEquals(p1.visible, true);

        assertEquals(p2.placement, TabPanePanelPlacement.PLACEMENT_LOWER);
        assertEquals(p2.visible, false);
    }
}
