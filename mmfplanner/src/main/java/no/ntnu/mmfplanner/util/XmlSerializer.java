/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.util;

import java.io.IOException;
import java.io.OutputStream;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement.PanelInfo;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * Utility class for serializing a MMF Project into an XML file.
 */
public class XmlSerializer {
    /**
     * Helper method for appending a new child text element to an element.
     */
    private static void appendTextNode(Element e, String name, Object text) {
        Element child = new Element(name);
        if ((text != null) && !"".equals(text)) {
            child.appendChild("" + text);
        }
        e.appendChild(child);
    }

    /**
     * Creates an Element describing the entire project and all it's MMFs and
     * categories.
     */
    public static Element tabPanePanelPlacementToElement(
            TabPanePanelPlacement placement) {
        // Create panels
        Element epanels = new Element("panels");
        for (PanelInfo panelInfo : placement.getPanels()) {
            epanels.appendChild(panelInfoToElement(panelInfo));
        }

        return epanels;
    }

    /**
     * Creates an Element describing panelInfo of a panel
     */
    public static Element panelInfoToElement(PanelInfo panelInfo) {
        // Panel info
        Element epanel = new Element("panel");
        epanel.addAttribute(new Attribute("id", panelInfo.id));
        appendTextNode(epanel, "visible", panelInfo.visible);
        appendTextNode(epanel, "placement", panelInfo.placement);

        return epanel;
    }

    /**
     * Creates an Element describing the entire project and all it's MMFs and
     * categories.
     */
    public static Element projectToElement(Project project) {
        // project and properties
        Element eproj = new Element("project");
        appendTextNode(eproj, "name", project.getName());
        appendTextNode(eproj, "periods", project.getPeriods());
        appendTextNode(eproj, "interestrate", project.getInterestRate());
        appendTextNode(eproj, "nextid", project.getNextId());
        appendTextNode(eproj, "maxmmfs", project.getMaxMmfsPerPeriod());

        // categories
        Element ecategories = new Element("categories");
        for (Category cat : project.getCategories()) {
            ecategories.appendChild(categoryToElement(project, cat));
        }
        eproj.appendChild(ecategories);

        // mmfs
        Element emmfs = new Element("mmfs");
        for (int i = 0; i < project.size(); i++) {
            emmfs.appendChild(mmfToElement(project, project.get(i)));
        }
        eproj.appendChild(emmfs);

        return eproj;
    }

    /**
     * Creates an element describing the given category.
     * 
     * Id's are added to the category according to index in the given project,
     * this id is only valid in the context of the current version of the given
     * project, and can not be assumed to be valid in the future. If the
     * category is not in the given project, an id of -1 is returned.
     */
    public static Element categoryToElement(Project project, Category category) {
        Element ecat = new Element("category");
        ecat.addAttribute(new Attribute("id", ""
                + project.getCategories().indexOf(category)));

        appendTextNode(ecat, "name", category.getName());
        if (null == category.getColor()) {
            appendTextNode(ecat, "color", null);
        } else {
            appendTextNode(ecat, "color", GuiUtil.getColorString(category
                    .getColor()));
        }
        if (null == category.getParent()) {
            appendTextNode(ecat, "parent", null);
        } else {
            appendTextNode(ecat, "parent", project.getCategories().indexOf(
                    category.getParent()));
        }
        return ecat;
    }

    /**
     * Creates and returns an element describing the given mmf.
     * 
     * For category an id is used corresponding to the categories index in the
     * given project, this id is only valid in the context of the current
     * version of the given project, and can not be assumed to be valid in the
     * future. If the category is not in the given project, an id of -1 is
     * returned.
     */
    public static Element mmfToElement(Project project, Mmf mmf) {
        // mmf and properties
        Element emmf = new Element("mmf");
        emmf.addAttribute(new Attribute("id", mmf.getId()));
        appendTextNode(emmf, "name", mmf.getName());
        appendTextNode(emmf, "period", mmf.getPeriod());
        appendTextNode(emmf, "locked", mmf.isLocked());
        appendTextNode(emmf, "swimlane", mmf.getSwimlane());
        if (null == mmf.getCategory()) {
            appendTextNode(emmf, "category_ref", null);
        } else {
            appendTextNode(emmf, "category_ref", mmf.getProject()
                    .getCategories().indexOf(mmf.getCategory()));
        }

        // precursors
        appendTextNode(emmf, "precursors", mmf.getPrecursorString());

        // revenues
        Element erevenues = new Element("revenues");
        for (int period = 1; period <= mmf.getRevenueLength(); period++) {
            Element erev = new Element("revenue");
            appendTextNode(erev, "period", "" + period);
            appendTextNode(erev, "value", "" + mmf.getRevenue(period));
            erevenues.appendChild(erev);
        }
        emmf.appendChild(erevenues);

        return emmf;
    }

    /**
     * Converts the project to a XML Element and adds a XML Document as wrapper
     * around the element.
     * 
     * @see #projectToElement()
     * @param mainFrame.getProject()
     *            the project to create a document for
     * @return a XML Document element for the given project
     */
    public static Document workspaceToDocument(TabPanePanelPlacement placement,
            Project project) {
        Element eproj = projectToElement(project);
        Element esettings = new Element("settings");
        if (placement != null) {
            Element epanels = tabPanePanelPlacementToElement(placement);
            esettings.appendChild(epanels);
        }
        Element eprojects = new Element("mmfproject");
        eprojects.appendChild(eproj);
        eprojects.appendChild(esettings);
        Document doc = new Document(eprojects);
        // FIXME: should verify how to correctly validate the dtd on open, or
        // disable validation completely
        // DocType doctype = new DocType("mmfproject", "http://mmfplanner.googlecode.com/svn/dist/mmfproject.dtd");
        // doc.setDocType(doctype);
        return doc;
    }

    /**
     * Writes the project to the given {@link OutputStream} in a nicely
     * formatted and valid XML-format, using UTF-8.
     * 
     * @throws IOException
     *             if the underlying IO layer gives an error
     * @see Serializer#write()
     */
    public static void writeWorkspace(TabPanePanelPlacement placement,
            Project project, OutputStream os) throws IOException {
        Document doc = workspaceToDocument(placement, project);

        Serializer serializer = new Serializer(os, "UTF-8");
        serializer.setIndent(2);
        // serializer.setMaxLength(80);
        serializer.write(doc);
    }
}