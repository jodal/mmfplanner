package no.ntnu.mmfplanner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class uses the IFM heuristic, also known as the weighted look-ahead
 * approach, to find the optimal NPV of a project.
 */
public class HeuristicProjectSorter extends ProjectSorter {
    /**
     * @param project
     */
    public HeuristicProjectSorter(Project project) {
        super(project);
    }

    @Override
    protected void sort() {
        setProgressMax(1);

        // Initialize variables
        List<Mmf> mmfs = project.getMmfs();
        int[] periods = new int[project.size()];
        List<Mmf> finalStrand = new ArrayList<Mmf>();
        int finalPeriod = 0;
        int finalNpv = 0;

        List<Mmf> unusedMmfs = new ArrayList<Mmf>(mmfs);

        // repeatedly find the most profitable MMF using the look-ahead
        // approach, and add this to the final strand
        while (unusedMmfs.size() > 0) {
            List<List<Mmf>> strands = generateStrands(unusedMmfs, finalStrand);

            int maxNpv = Integer.MIN_VALUE;
            Mmf maxMmf = null;
            
            // Calculate npvs for all strands, and add the most profitable to the final strand 
            for (List<Mmf> strand : strands) {
                int period = finalPeriod;
                int npv = 0;

                for (Mmf mmf : strand) {
                    npv += mmf.getSaNpv(project.getInterestRate(), period);
                    period += mmf.getPeriodCount();
                }

                if (npv >= maxNpv) {
                    maxNpv = npv;
                    maxMmf = strand.get(0);
                }
            }

            // we found the most profitable MMF, add it and remove it from
            // unusedMmfs
            unusedMmfs.remove(maxMmf);

            finalStrand.add(maxMmf);
            finalNpv += maxMmf.getSaNpv(project.getInterestRate(), finalPeriod);
            ;
            periods[project.getMmfs().indexOf(maxMmf)] = finalPeriod + 1;
            finalPeriod += maxMmf.getPeriodCount();
        }

        addResult(finalNpv, periods);
        setProgress(1);
    }

    /**
     * Generates all MMF-strands from the given MMFs.
     * 
     * @param usedMmfs
     * 
     * @param usedMmfs
     * @return
     */
    private List<List<Mmf>> generateStrands(List<Mmf> availableMmfs,
            List<Mmf> usedMmfs) {
        List<List<Mmf>> strands = new ArrayList<List<Mmf>>();
        List<Mmf> mmfs = new ArrayList<Mmf>(availableMmfs);

        // repeat until no more mmfs are available
        while (mmfs.size() > 0) {
            for (int i = mmfs.size() - 1; i >= 0; i--) {
                Mmf mmf = mmfs.get(i);
                List<Mmf> precursors = new ArrayList<Mmf>(mmf.getPrecursors());
                precursors.removeAll(usedMmfs);

                // if this mmf has no precursors, add it to the strands
                if (precursors.size() == 0) {
                    strands.add(Collections.singletonList(mmf));
                    mmfs.remove(mmf);
                    continue;
                }

                // go over all the existing strands, if all precursors are
                // available in a strand, and the strand contain only these
                // precursors: add the given mmf and remove it from
                // the available mmfs.
                for (int j = 0; j < strands.size(); j++) {
                    List<Mmf> strand = strands.get(j);
                    if ((strand.size() == precursors.size())
                            && strand.containsAll(precursors)) {
                        ArrayList<Mmf> newStrand = new ArrayList<Mmf>(strand);
                        newStrand.add(mmf);

                        strands.add(newStrand);
                        mmfs.remove(mmf);
                    }
                }
            }
        }

        return strands;
    }
}
