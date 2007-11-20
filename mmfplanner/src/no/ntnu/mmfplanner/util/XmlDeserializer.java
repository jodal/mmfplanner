/*
 * $Id: XmlDeserializer.java 1526 2007-11-19 16:21:30Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.util;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTabbedPane;

import no.ntnu.mmfplanner.model.*;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement;
import nu.xom.*;

/**
 * Helper class for serializing a MMF Project into an XML file.
 * 
 * @version $Revision: 1526 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class XmlDeserializer {
    /**
     * Helper method for retrieving the value from a single text node
     */
    private static String getTextNode(Element e, String name) {
        Element child = e.getFirstChildElement(name);
        if (child == null) {
            throw new IllegalArgumentException("Element not found: " + name);
        }
        return child.getValue();
    }

    /**
     * Helper method for retrieving the value from a single integer text node
     */
    private static int getIntNode(Element e, String name) {
        return Integer.parseInt(getTextNode(e, name));
    }

    /**
     * Helper method for retrieving the value from a single boolean text node
     */
    private static boolean getBoolNode(Element e, String name) {
        return Boolean.parseBoolean(getTextNode(e, name));
    }

    public static Project readProject(InputStream is) throws MmfException,
            IOException, ValidityException, ParsingException {
        Builder b = new Builder(false);
        return documentToProject(b.build(is));
    }

    public static Project documentToProject(Document e) throws MmfException {
        Element eprojects = e.getRootElement();
        Element eproject = eprojects.getFirstChildElement("project");
        return elementToProject(eproject);
    }

    public static Project elementToProject(Element e) throws MmfException {
        // project and properties
        Project project = new Project();
        project.setName(getTextNode(e, "name"));
        project.setPeriods(getIntNode(e, "periods"));
        project.setInterestRate(Double.parseDouble(getTextNode(e,
                "interestrate")));
        project.setNextId(getTextNode(e, "nextid"));
        project.setMaxMmfsPerPeriod(getIntNode(e, "maxmmfs"));

        // categories
        Map<Category, Element> catElements = new HashMap<Category, Element>();
        Elements ecats = e.getFirstChildElement("categories").getChildElements(
                "category");
        for (int i = 0; i < ecats.size(); i++) {
            Element ec = ecats.get(i);
            Category cat = elementToCategory(project, ec);
            project.addCategory(cat);
            catElements.put(cat, ec);
        }
        for (Category cat : catElements.keySet()) {
            setCategoryParent(project, cat, catElements.get(cat));
        }

        // mmfs
        Map<Mmf, Element> mmfElements = new HashMap<Mmf, Element>();
        Elements emmfs = e.getFirstChildElement("mmfs").getChildElements("mmf");
        for (int i = 0; i < emmfs.size(); i++) {
            Element em = emmfs.get(i);
            Mmf mmf = elementToMmf(project, em);
            project.add(mmf);
            mmfElements.put(mmf, em);
        }
        for (Mmf mmf : mmfElements.keySet()) {
            addMmfPrecursors(mmf, mmfElements.get(mmf));
        }

        return project;
    }

    /**
     * Reads the settings element and set the placement according to it
     * 
     * @param placement tabPanePanelPlacement to update according to <settings>
     * @param e The <settings> element to use.
     * @throws MmfException
     */
    public static void elementToTabPanePanelPlacement(
            TabPanePanelPlacement placement, Element e) throws MmfException {

        // Set panels properties
        Elements epanels = e.getChildElements("panel");
        for (int i = 0; i < epanels.size(); i++) {
            String id = epanels.get(i).getAttributeValue("id");
            placement.movePanel(id, getTextNode(epanels.get(i), "placement"));
            placement.setVisible(id, getBoolNode(epanels.get(i), "visible"));
        }

        // Let all panes display first tab
        for (JTabbedPane pane : placement.getPanes()) {
            pane.setSelectedIndex(0);
        }

    }

    public static Category elementToCategory(Project project, Element e)
            throws MmfException {
        Category cat = new Category();
        String color = getTextNode(e, "color");
        if (!"".equals(color)) {
            cat.setColor(new Color(Integer.parseInt(color, 16)));
        }
        cat.setName(getTextNode(e, "name"));
        return cat;
    }

    public static void setCategoryParent(Project project, Category cat,
            Element e) throws MmfException {
        String parent = getTextNode(e, "parent");
        if ("" != parent) {
            cat.setParent(project.getCategory(Integer.parseInt(parent)));
        }
    }

    public static Mmf elementToMmf(Project project, Element e)
            throws MmfException {
        Mmf mmf = new Mmf(e.getAttributeValue("id"), getTextNode(e, "name"));
        mmf.setProject(project);
        mmf.setPeriod(getIntNode(e, "period"));
        mmf.setLocked(getBoolNode(e, "locked"));
        mmf.setSwimlane(getIntNode(e, "swimlane"));
        String category = getTextNode(e, "category_ref");
        if ("" != category) {
            mmf.setCategory(project.getCategory(Integer.parseInt(category)));
        }

        Elements revenues = e.getFirstChildElement("revenues")
                .getChildElements("revenue");
        for (int i = 0; i < revenues.size(); i++) {
            Element er = revenues.get(i);
            mmf.setRevenue(getIntNode(er, "period"), getIntNode(er, "value"));
        }

        return mmf;
    }

    public static void addMmfPrecursors(Mmf mmf, Element e) throws MmfException {
        mmf.setPrecursorString(getTextNode(e, "precursors"));
    }

    public static Project readProject(TabPanePanelPlacement placement,
            InputStream is) throws ValidityException, MmfException,
            ParsingException, IOException {
        Builder b = new Builder(false);
        Document e = b.build(is);
        Element eprojects = e.getRootElement();
        Element eproject = eprojects.getFirstChildElement("project");
        Project project = elementToProject(eproject);
        Element esettings = eprojects.getFirstChildElement("settings");
        if ((esettings != null) && (placement != null)) {
            elementToTabPanePanelPlacement(placement, esettings
                    .getFirstChildElement("panels"));
        }
        return project;
    }
}
