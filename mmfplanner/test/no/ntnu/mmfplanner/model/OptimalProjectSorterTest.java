/*
 * OptimalProjectSorterTest.java
 *
 * Created on 12. nov.. 2007, 09.35.53
 *
 * Version $Id: OptimalProjectSorterTest.java 1639 2007-11-20 16:50:30Z erikbagg $
 *
 * Copyright 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset
 */

package no.ntnu.mmfplanner.model;

import static org.junit.Assert.*;

import java.util.List;

import no.ntnu.mmfplanner.model.ProjectSorter.Result;

import org.junit.Test;

/**
 * TODO: Description of class and functionality.
 * 
 * @version $Revision: 1639 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class OptimalProjectSorterTest {
    private static final int REVENUE_2[][] = new int[][] {
            new int[] { -20, -20, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 },
            new int[] { -50, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 } };

    private static final int REVENUE_5[][] = new int[][] {
            new int[] { -50, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                    10, 10, 10 },
            new int[] { -20, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0 },
            new int[] { -20, -20, 10, 13, 16, 19, 22, 25, 25, 25, 25, 25, 25,
                    25, 25, 25 },
            new int[] { -20, -20, 10, 14, 18, 22, 26, 30, 34, 38, 40, 40, 40,
                    40, 40, 40 },
            new int[] { -50, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35,
                    35, 35, 35 } };

    Project project;

    public void initProject(int revenue[][]) throws MmfException {
        project = new Project();
        project.setInterestRate(0.1);
        project.setMaxMmfsPerPeriod(1);
        project.setPeriods(revenue[0].length);

        for (int i = 0; i < revenue.length; i++) {
            String id = "" + (char) ('A' + i);
            Mmf mmf = new Mmf(id, "MMF " + id);
            for (int j = 0; j < revenue[i].length; j++) {
                mmf.setRevenue(j + 1, revenue[i][j]);
            }
            project.add(mmf);
        }
    }

    protected ProjectSorter runSorter() {
        ProjectSorter sorter = new OptimalProjectSorter(project);
        sorter.start(false);
        return sorter;
    }

    /**
     * Compares a list of results with the expected sequences and npvs
     * 
     * @param eseence
     * @param npv
     * @param results
     * @throws MmfException
     */
    private void assertResults(int progress, String[] sequence, int npv[])
            throws MmfException {
        // run project sorter
        ProjectSorter sorter = runSorter();

        // TODO: remove (debug info)
        List<Result> results = sorter.getResults();
        String debSeq = "", debNpv = "";
        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            debSeq += ",\"" + r.sequence + "\"";
            debNpv += "," + r.npv;
        }
        System.out.println(debSeq + " ## " + debNpv);

        // basic checks
        assertTrue(sorter.isDone());
        assertEquals(project, sorter.getProject());
        assertEquals(progress, sorter.getProgress());
        assertEquals(progress, sorter.getProgressMax());

        assertEquals(sequence.length, npv.length);
        assertEquals(sequence.length, results.size());

        // check results (sequence, npv, loss, optimal)
        int maxNpv = results.get(0).npv;
        int maxPeriod = project.getPeriods() + 1;
        int npvDelta = (project.getMmfs().size() + 1) / 2;

        for (int i = 0; i < sequence.length; i++) {
            Result r = results.get(i);
            assertEquals(sequence[i], r.sequence);
            assertEquals(npv[i], r.npv);
            assertEquals(maxNpv - npv[i], r.loss);
            assertEquals((double) npv[i] / maxNpv, r.optimal, 0.00);

            for (int j = 0; j < r.periods.length; j++) {
                int p = (r.periods[j] > 0 ? r.periods[j] : maxPeriod);
                project.get(j).setPeriod(p);
            }
            ProjectRoi roi = ProjectRoi.getRoiTable(project, project
                    .getInterestRate(), false);
            assertEquals(roi.presentValue[roi.presentValue.length - 1], r.npv,
                    npvDelta);
        }
    }

    /**
     * Test method for {@link GreedyProjectSorter#sort()} with two MMFs.
     */
    @Test
    public void testSort2() throws MmfException {
        initProject(REVENUE_2);

        String expSeq[] = new String[] { "BA", "AB", "A", "B", "" };
        int expNpv[] = new int[] { 26, 22, 16, 14, 0 };
        assertResults(5, expSeq, expNpv);
    }

    /**
     * Test method for {@link GreedyProjectSorter#sort()} with two MMFs and one
     * dependency.
     */
    @Test
    public void testSort2D1() throws MmfException {
        initProject(REVENUE_2);
        project.get(0).addPrecursor(project.get(1));

        String expSeq[] = new String[] { "BA", "B", "" };
        int expNpv[] = new int[] { 26, 14, 0 };
        assertResults(5, expSeq, expNpv);
    }

    /**
     * Test method for {@link GreedyProjectSorter#sort()} with two MMFs and one
     * locked.
     */
    @Test
    public void testSort2L1() throws MmfException {
        initProject(REVENUE_2);
        project.get(1).setPeriod(2);
        project.get(1).setLocked(true);

        String expSeq[] = new String[] { "BA", "B" };
        int expNpv[] = new int[] { 17, 9 };
        assertResults(2, expSeq, expNpv);
    }

    /**
     * Test method for {@link GreedyProjectSorter#sort()} with two MMFs.
     */
    @Test
    public void testSort5() throws MmfException {
        initProject(REVENUE_5);

        String expSeq[] = new String[] { "EDCAB", "EDCBA", "EDACB", "EDCB",
                "EDBCA", "EDABC", "ECDAB", "EDBAC", "EDBC", "ECDBA", "EDCA",
                "EADCB", "EDAC", "ECDB", "EDC", "EADBC", "ECADB", "EBDCA",
                "ECDA", "EACDB", "ECBDA", "EADC", "EBDAC", "EBDC", "DECAB",
                "ECBD", "DECBA", "ECD", "DEACB", "ECABD", "EABDC", "EBCDA",
                "ECAD", "DECB", "ECBAD", "AEDCB", "EBADC", "DEBCA", "EACBD",
                "EBCD" };
        int expNpv[] = new int[] { 377, 376, 374, 372, 370, 368, 366, 366, 366,
                365, 365, 364, 362, 361, 359, 358, 357, 356, 354, 353, 353,
                352, 352, 352, 349, 349, 348, 348, 346, 346, 345, 345, 345,
                344, 344, 343, 343, 342, 342, 341 };
        assertResults(326, expSeq, expNpv);
    }

    /**
     * Test method for {@link GreedyProjectSorter#sort()} with two MMFs and two
     * dependencies.
     */
    @Test
    public void testSort5D2() throws MmfException {
        initProject(REVENUE_5);
        project.get(4).addPrecursor(project.get(1));
        project.get(1).addPrecursor(project.get(0));

        String expSeq[] = new String[] { "DABEC", "ABEDC", "ADBEC", "DCABE",
                "ABECD", "DACBE", "DABCE", "ABDEC", "CDABE", "ADCBE", "CABED",
                "ACBED", "DABE", "ADBCE", "CADBE", "ABED", "ACDBE", "ABCED",
                "ADBE", "CABDE", "ABDCE", "ACBDE", "ABCDE", "ABDE", "ABEC",
                "CABE", "ACBE", "ABCE", "DCAB", "DACB", "DABC", "CDAB", "DCA",
                "ADCB", "DAC", "ABE", "ADBC", "CADB", "DC", "CDA" };
        int expNpv[] = new int[] { 306, 303, 294, 293, 292, 289, 283, 280, 280,
                277, 277, 272, 272, 271, 271, 269, 266, 264, 260, 259, 257,
                254, 246, 246, 245, 230, 225, 217, 215, 211, 205, 202, 202,
                199, 198, 193, 193, 193, 193, 189 };
        assertResults(326, expSeq, expNpv);
    }

    /**
     * Test method for {@link GreedyProjectSorter#sort()} with two MMFs, two
     * dependencies and one locked.
     */
    @Test
    public void testSort5D2L1() throws MmfException {
        initProject(REVENUE_5);
        project.get(4).addPrecursor(project.get(1));
        project.get(1).addPrecursor(project.get(0));
        project.get(3).setPeriod(1);
        project.get(3).setLocked(true);

        String expSeq[] = new String[] { "DABEC", "DCABE", "DACBE", "DABCE",
                "DABE", "DCAB", "DACB", "DABC", "DCA", "DAC", "DC", "DAB",
                "DA", "D" };
        int expNpv[] = new int[] { 306, 293, 289, 283, 272, 215, 211, 205, 202,
                198, 193, 162, 146, 130 };
        assertResults(65, expSeq, expNpv);
    }

    /**
     * Test method for {@link GreedyProjectSorter#sort()} with two MMFs, two
     * dependencies and one locked. Using two mmfs per period.
     */
    @Test
    public void testSort5D2L1M2() throws MmfException {
        initProject(REVENUE_5);
        project.get(4).addPrecursor(project.get(1));
        project.get(1).addPrecursor(project.get(0));
        project.get(3).setPeriod(1);
        project.get(3).setLocked(true);
        project.setMaxMmfsPerPeriod(2);

        String expSeq[] = new String[] { "ADBCE", "ADCBE", "CDABE", "ADBE",
                "CDAB", "ADCB", "ADBC", "CDA", "ADC", "CD", "ADB", "AD", "D" };
        int expNpv[] = new int[] { 386, 376, 360, 323, 250, 247, 237, 234, 229,
                218, 174, 154, 130 };
        assertResults(65, expSeq, expNpv);
    }
}
