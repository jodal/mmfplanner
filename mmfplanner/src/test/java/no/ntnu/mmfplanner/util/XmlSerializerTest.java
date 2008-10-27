/*
 * $Id: XmlSerializerTest.java 1397 2007-11-17 13:55:32Z erikbagg $
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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import no.ntnu.mmfplanner.ProjectTestFixture;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement;
import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Test for class {@link link no.ntnu.mmfplanner.util.XmlSerializer}.
 * 
 * @version $Revision: 1397 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class XmlSerializerTest extends ProjectTestFixture {

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
     * {@link no.ntnu.mmfplanner.util.XmlSerializer#projectToElement(no.ntnu.mmfplanner.model.Project)}.
     */
    @Test
    public void testProjectToElement() {
        System.out.println("XmlSerializerTest.testProjectToElement()");

        String exp = "<project><name>Test project</name><periods>12</periods><"
                + "interestrate>0.0080</interestrate><nextid>C</nextid><maxmmfs>"
                + "1</maxmmfs><catego"
                + "ries><category id=\"0\"><name>Online Travel Agency</name><c"
                + "olor>FF0000</color><parent /></category><category id=\"1\">"
                + "<name>Trip Planner</name><color>0000FF</color><parent>0</pa"
                + "rent></category></categories><mmfs><mmf id=\"A\"><name>Test"
                + " A</name><period>2</period><locked>true</locked><swimlane>1</swimlane><category_ref "
                + "/><precursors /><revenues><revenue><period>1</period><value"
                + ">-200</value></revenue><revenue><period>2</period><value>-2"
                + "00</value></revenue><revenue><period>3</period><value>100</"
                + "value></revenue><revenue><period>4</period><value>120</valu"
                + "e></revenue><revenue><period>5</period><value>140</value></"
                + "revenue><revenue><period>6</period><value>160</value></reve"
                + "nue><revenue><period>7</period><value>200</value></revenue>"
                + "<revenue><period>8</period><value>220</value></revenue><rev"
                + "enue><period>9</period><value>240</value></revenue><revenue"
                + "><period>10</period><value>300</value></revenue><revenue><p"
                + "eriod>11</period><value>320</value></revenue><revenue><peri"
                + "od>12</period><value>340</value></revenue><revenue><period>"
                + "13</period><value>1000</value></revenue><revenue><period>14"
                + "</period><value>2000</value></revenue><revenue><period>15</"
                + "period><value>3000</value></revenue></revenues></mmf><mmf i"
                + "d=\"B\"><name>Test B</name><period>1</period><locked>false</locked><swimlane>1</s"
                + "wimlane><category_ref>0</category_ref><precursors>A</precursors><re"
                + "venues /></mmf></mmfs></project>";

        Element e = XmlSerializer.projectToElement(project);
        assertEquals(exp, e.toXML());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlSerializer#categoryToElement(no.ntnu.mmfplanner.model.Project, no.ntnu.mmfplanner.model.Category)}.
     */
    @Test
    public void testCategoryToElement() {
        System.out.println("XmlSerializerTest.testCategoryToElement()");

        String exp1 = "<category id=\"0\"><name>Online Travel Agency</name><co"
                + "lor>FF0000</color><parent /></category>";
        String exp2 = "<category id=\"1\"><name>Trip Planner</name><color>0000"
                + "FF</color><parent>0</parent></category>";

        Element e1 = XmlSerializer.categoryToElement(project, category1);
        assertEquals(exp1, e1.toXML());
        Element e2 = XmlSerializer.categoryToElement(project, category2);
        assertEquals(exp2, e2.toXML());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlSerializer#tabPanePanelPlacementToElement(no.ntnu.mmfplanner.model.Project)}.
     */
    @Test
    public void testTabPanePanelPlacementToElement() {
        System.out
                .println("XmlSerializerTest.testTabPanePanelPlacementToElement()");

        String exp1 = "<panels><panel id=\"projectPropPanel\"><visible>true</visible>"
                + "<placement>upper</placement></panel><panel id=\"mmfTablePanel\"><visible>false</visible>"
                + "<placement>lower</placement></panel></panels>";

        Element e1 = XmlSerializer.tabPanePanelPlacementToElement(placement);
        assertEquals(exp1, e1.toXML());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlSerializer#mmfToElement(no.ntnu.mmfplanner.model.Project, no.ntnu.mmfplanner.model.Mmf)}.
     */
    @Test
    public void testMmfToElement() {
        System.out.println("XmlSerializerTest.testMmfToElement()");

        String expB = "<mmf id=\"B\"><name>Test B</name><period>1</period><locked>false</locked><swi"
                + "mlane>1</swimlane><category_ref>0</category_ref><precursors>A</prec"
                + "ursors><revenues /></mmf>";
        String expA = "<mmf id=\"A\"><name>Test A</name><period>2</period><locked>true</locked><swi"
                + "mlane>1</swimlane><category_ref /><precursors /><revenues><reve"
                + "nue><period>1</period><value>-200</value></revenue><revenue"
                + "><period>2</period><value>-200</value></revenue><revenue><p"
                + "eriod>3</period><value>100</value></revenue><revenue><perio"
                + "d>4</period><value>120</value></revenue><revenue><period>5<"
                + "/period><value>140</value></revenue><revenue><period>6</per"
                + "iod><value>160</value></revenue><revenue><period>7</period>"
                + "<value>200</value></revenue><revenue><period>8</period><val"
                + "ue>220</value></revenue><revenue><period>9</period><value>2"
                + "40</value></revenue><revenue><period>10</period><value>300<"
                + "/value></revenue><revenue><period>11</period><value>320</va"
                + "lue></revenue><revenue><period>12</period><value>340</value"
                + "></revenue><revenue><period>13</period><value>1000</value><"
                + "/revenue><revenue><period>14</period><value>2000</value></r"
                + "evenue><revenue><period>15</period><value>3000</value></rev"
                + "enue></revenues></mmf>";

        Element eB = XmlSerializer.mmfToElement(project, mmfB);
        assertEquals(expB, eB.toXML());
        Element eA = XmlSerializer.mmfToElement(project, mmfA);
        assertEquals(expA, eA.toXML());
    }

    /**
     * Test method for
     * {@link no.ntnu.mmfplanner.util.XmlSerializer#writeWorkspace(no.ntnu.mmfplanner.model.Project, java.io.OutputStream)}.
     */
    @Test
    public void testWriteProject() throws Exception {
        System.out.println("XmlSerializerTest.testWriteProject()");

        String exp = "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>\\s*<!DOCTYPE mmfproject SYSTEM \"http://mmfplanner.googlecode.com/svn/dist/mmfproject.dtd\">\\s*<mmfproject>\\s*<project>.+</project>\\s*<settings>.+</settings>\\s*</mmfproject>\\s*";
        Pattern pexp = Pattern.compile(exp, Pattern.DOTALL);
        int expSize = 3086;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XmlSerializer.writeWorkspace(placement, project, os);
        String xml = os.toString("UTF-8");

        assertTrue(pexp.matcher(xml).matches());
        assertEquals(expSize, os.size());
        assertEquals(expSize, xml.length());

        // UTF-8
        os.reset();
        project.setName("ÆØÅæøå");
        XmlSerializer.writeWorkspace(placement, project, os);
        xml = os.toString("UTF-8");
        assertTrue(pexp.matcher(xml).matches());
        assertTrue(xml.contains("<name>ÆØÅæøå</name>"));
        assertEquals(expSize, os.size());
        assertEquals(expSize - 6, xml.length());
    }

    @Test
    public void testDocumentTypeDefinition() throws Exception {
        System.out.println("XmlSerializerTest.testDocumentTypeDefinition()");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XmlSerializer.writeWorkspace(placement, project, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        XMLReader xerces = XMLReaderFactory
                .createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");
        xerces.setFeature("http://apache.org/xml/features/validation/schema",
                true);
        xerces.setProperty(
                "http://java.sun.com/xml/jaxp/properties/schemaSource",
                "mmfproject.schema");

        Builder parser = new Builder(xerces, true);
        parser.build(is);
    }

    @Test
    public void testXmlSchema() throws Exception {
        System.out.println("XmlSerializerTest.testXmlSchema()");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XmlSerializer.writeWorkspace(placement, project, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        XMLReader xerces = XMLReaderFactory
                .createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");
        xerces.setFeature("http://apache.org/xml/features/validation/schema",
                true);
        xerces.setProperty(
                "http://java.sun.com/xml/jaxp/properties/schemaSource",
                "mmfproject.schema");

        Builder parser = new Builder(xerces, true);
        parser.build(is);
    }
}
