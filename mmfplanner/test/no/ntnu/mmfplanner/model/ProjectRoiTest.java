/*
 * ProjectRoiTest.java
 * 
 * Created on 17. nov.. 2007, 15.09.39
 * 
 * Version $Id: ProjectRoiTest.java 1407 2007-11-17 14:45:25Z erikbagg $
 *
 * Copyright 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset
 */

package no.ntnu.mmfplanner.model;

import static org.junit.Assert.*;

import no.ntnu.mmfplanner.ProjectTestFixture;

import org.junit.Test;

/**
 * Test for {@link ProjectRoi}
 * 
 * @version $Revision: 1407 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class ProjectRoiTest extends ProjectTestFixture {

    @Test
    public void testGetRoiTable() throws MmfException {
        System.out.println("ProjectRoiTest.testGetRoiTable()");

        int[][] inRevenue = new int[][] {
                new int[] { -50, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10 },
                new int[] { -20, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0 },
                new int[] { -20, -20, 10, 13, 16, 19, 22, 25, 25, 25, 25, 25,
                        25, 25, 25, 25 },
                new int[] { -20, -20, 10, 14, 18, 22, 26, 30, 34, 38, 40, 40,
                        40, 40, 40, 40 },
                new int[] { -50, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35,
                        35, 35, 35, 35 } };
        int[] inPeriod = new int[] { 3, 4, 1, 1, 3 };

        String[] expMmfs = new String[] { "C", "D", "A", "E", "B" };
        int[][] expValues = new int[][] {
                new int[] { -20, -20, 10, 13, 16, 19, 22, 25, 25, 25, 25, 25,
                        25, 25, 25, 25, 265 },
                new int[] { -20, -20, 10, 14, 18, 22, 26, 30, 34, 38, 40, 40,
                        40, 40, 40, 40, 392 },
                new int[] { 0, 0, -50, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 80 },
                new int[] { 0, 0, -50, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35,
                        35, 35, 35, 405 },
                new int[] { 0, 0, 0, -20, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0,
                        45 } };
        int[] expCash = new int[] { -40, -40, -80, 52, 89, 96, 102, 108, 111,
                114, 115, 114, 113, 112, 111, 110, 1187 };
        int[] expInvestment = new int[] { -40, -40, -80, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, -160 };
        int[] expPresentValue = new int[] { -40, -39, -78, 50, 86, 92, 96, 101,
                103, 105, 105, 104, 102, 100, 98, 97, 1083 };
        int[] expRollingNpv = new int[] { -40, -79, -157, -107, -21, 70, 167,
                268, 371, 477, 582, 686, 787, 888, 986, 1083 };

        int[][] watValues = new int[][] {
                new int[] { -20, -20, 0, 0, 10, 13, 16, 19, 22, 25, 25, 25, 25,
                        25, 25, 25, 215 },
                new int[] { -20, -20, 0, 0, 10, 14, 18, 22, 26, 30, 34, 38, 40,
                        40, 40, 40, 312 },
                new int[] { 0, 0, -50, 0, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 70 },
                new int[] { 0, 0, -50, 0, 35, 35, 35, 35, 35, 35, 35, 35, 35,
                        35, 35, 35, 370 },
                new int[] { 0, 0, 0, -20, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0,
                        45 } };

        int[] watCash = new int[] { -40, -40, -100, -20, 75, 82, 88, 94, 100,
                106, 109, 112, 113, 112, 111, 110, 1012 };
        int[] watInvestment = new int[] { -40, -40, -100, -20, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, -200 };
        int[] watPresentValue = new int[] { -40, -39, -98, -19, 72, 78, 83, 88,
                93, 98, 100, 102, 102, 100, 98, 97, 916 };
        int[] watRollingNpv = new int[] { -40, -79, -177, -196, -124, -46, 37,
                126, 219, 317, 416, 518, 620, 720, 819, 916 };

        // setup project
        project.remove(mmfA);
        project.remove(mmfB);
        assertEquals(0, project.size());
        for (int i = 0; i < inRevenue.length; i++) {
            Mmf mmf = new Mmf("" + (char) ('A' + i), "ROI Test");
            mmf.setPeriod(inPeriod[i]);
            for (int j = 0; j < inRevenue[i].length; j++) {
                mmf.setRevenue(j + 1, inRevenue[i][j]);
            }
            project.add(mmf);
        }
        project.setPeriods(16);

        // calculate roi
        ProjectRoi roi = ProjectRoi.getRoiTable(project, project.getInterestRate(), false);

        // check output data
        for (int i = 0; i < expMmfs.length; i++) {
            assertEquals(expMmfs[i], roi.mmfs[i].getId());
        }
        assertArrayEquals(expValues, roi.values);
        assertArrayEquals(expCash, roi.cash);
        assertArrayEquals(expInvestment, roi.investment);
        assertArrayEquals(expPresentValue, roi.presentValue);
        assertArrayEquals(expRollingNpv, roi.rollingNpv);

        assertEquals(7.42, roi.roi, 0.005);
        assertEquals(4, roi.selfFundingPeriod);
        assertEquals(6, roi.breakevenPeriod);
        assertEquals(6.23, roi.breakevenRegression, 0.005);

        // calculate roi waterfall
        roi = ProjectRoi.getRoiTable(project, project.getInterestRate(), true);

        // check output data
        for (int i = 0; i < expMmfs.length; i++) {
            assertEquals(expMmfs[i], roi.mmfs[i].getId());
        }
        assertArrayEquals(watValues, roi.values);
        assertArrayEquals(watCash, roi.cash);
        assertArrayEquals(watInvestment, roi.investment);
        assertArrayEquals(watPresentValue, roi.presentValue);
        assertArrayEquals(watRollingNpv, roi.rollingNpv);

        assertEquals(5.06, roi.roi, 0.005);
        assertEquals(5, roi.selfFundingPeriod);
        assertEquals(7, roi.breakevenPeriod);
        assertEquals(7.55, roi.breakevenRegression, 0.005);
    }

}
