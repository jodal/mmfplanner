package no.ntnu.mmfplanner.ui.action;

import static org.junit.Assert.*;

import java.util.List;

import no.ntnu.mmfplanner.model.HeuristicProjectSorter;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.model.ProjectRoi;
import no.ntnu.mmfplanner.model.ProjectSorter;
import no.ntnu.mmfplanner.model.ProjectSorter.Result;

import org.junit.Test;

/**
 * TODO: Description of type.
 * 
 * @version $Id:$
 * @author Erik Bagge Ottesen <erikbagg@idi.ntnu.no>
 */
public class HeuristicProjectSorterTest {
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
        ProjectSorter sorter = new HeuristicProjectSorter(project);
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
        List<Result> results = sorter.getResults();

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
     * Test method for {@link HeuristicProjectSorter#sort()} with two MMFs and
     * one dependency.
     */
    @Test
    public void testSort2D1() throws MmfException {
        initProject(REVENUE_2);
        project.get(0).addPrecursor(project.get(1));

        String expSeq[] = new String[] { "BA" };
        int expNpv[] = new int[] { 26 };
        assertResults(1, expSeq, expNpv);
    }

}
