/*
 * $Id: ProjectRoi.java 1403 2007-11-17 14:19:58Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.model;

/**
 *
 * Class for holding the project ROI values. Also has a method for calculating the values.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class ProjectRoi {
    public Mmf mmfs[];
    public int values[][];
    public int cash[];
    public int investment[];
    public int presentValue[];
    public int rollingNpv[];

    public double interestRate;
    public double roi;
    public int selfFundingPeriod;
    public int breakevenPeriod;
    public double breakevenRegression;

    /**
     * This method will calculate the most important variables in valuing the
     * project. All values for each MMF and each period are given, as well as
     * relevant summations and NPVs.
     *
     * @return A complete ProjectRoi object with the projected return on
     *         investment for this project.
     */
    public static ProjectRoi getRoiTable(Project project, double interestRate, boolean waterfall) {
        ProjectRoi roi = new ProjectRoi();
        int mmfCount = project.size();
        int periods = project.getPeriods();

        // roi.interestRate
        roi.interestRate = interestRate;

        // roi.mmfs
        roi.mmfs = new Mmf[mmfCount];
        int nextRoiMmf = 0;
        for (int i = 1; i <= periods; i++) {
            for (Mmf mmf : project.getMmfs()) {
                if (i == mmf.getPeriod()) {
                    roi.mmfs[nextRoiMmf] = mmf;
                    nextRoiMmf++;
                }
            }
        }
        for (Mmf mmf : project.getMmfs()) {
            if (mmf.getPeriod() > periods) {
                roi.mmfs[nextRoiMmf] = mmf;
                nextRoiMmf++;
            }
        }

        // roi.values
        roi.values = new int[mmfCount][periods + 1];
        for (int i = 0; i < mmfCount; i++) {
            Mmf mmf = roi.mmfs[i];
            int sum = 0;
            int skipPeriods = mmf.getPeriod() - 1;
            for (int p = skipPeriods; p < periods; p++) {
                roi.values[i][p] = mmf.getRevenue(p - skipPeriods + 1);
                sum += roi.values[i][p];
            }
            roi.values[i][periods] = sum;
        }

        if (waterfall) {
            // for waterfall we move all positive revenue beyond the last
            // negative revenue

            // first we find the last period with negative revenue for any mmf
            int negativeMax = 0;
            int negative[] = new int[mmfCount];
            for (int i = 0; i < mmfCount; i++) {
                Mmf mmf = roi.mmfs[i];
                if (mmf.getPeriod() > periods) {
                    negative[i] = periods;
                    continue;
                }

                for (int p = 1; p <= periods; p++) {
                    if (mmf.getRevenue(p) >= 0) {
                        negative[i] = p + mmf.getPeriod() - 2;
                        negativeMax = Math.max(negativeMax, negative[i]);
                        break;
                    }
                }
            }
            negativeMax = Math.min(negativeMax, periods);

            // then move all the positive revenue (and update the net value)
            for (int i = 0; i < mmfCount; i++) {
                if (negative[i] >= negativeMax) {
                    continue;
                }
                // we first swap all the revenues to the left
                for (int p = periods - 1; p >= negativeMax; p--) {
                    int pd = p - (negativeMax - negative[i]);
                    int tmp = roi.values[i][p];
                    roi.values[i][p] = roi.values[i][pd];
                    roi.values[i][pd] = tmp;
                }
                // and then remove all the ones that are left
                for (int p = negative[i]; p < negativeMax; p++) {
                    roi.values[i][periods] -= roi.values[i][p];
                    roi.values[i][p] = 0;
                }
            }
        }

        // roi.cash, investement, presentValue, rollingNpv
        roi.cash = new int[periods + 1];
        roi.investment = new int[periods + 1];
        roi.presentValue = new int[periods + 1];
        roi.rollingNpv = new int[periods];
        double sumPV = 0.0;
        double minPV = -0.001;
        
        for (int p = 0; p < periods; p++) {
            // roi.cash
            int sum = 0;
            for (int[] values : roi.values) {
                sum += values[p];
            }
            roi.cash[p] = sum;
            roi.cash[periods] += sum;

            // roi.investment
            roi.investment[p] = (roi.cash[p] < 0 ? roi.cash[p] : 0);
            roi.investment[periods] += roi.investment[p];

            // roi.presentValue
            double pv = roi.cash[p] / Math.pow(1 + project.getInterestRate(), p + 1);
            sumPV += pv;
            roi.presentValue[p] = (int) Math.round(pv);

            // roi.rollingNpv
            roi.rollingNpv[p] = (int) Math.round(sumPV);

            // roi.selfFundingPeriod
            if (sumPV <= minPV) {
                minPV = roi.rollingNpv[p];
                if (p + 1 < periods) {
                    roi.selfFundingPeriod = p + 2; 
                } else {
                    roi.selfFundingPeriod = 0;
                }
                
            }

            // roi.breakevenPeriod
            if ((0 == roi.breakevenPeriod) && (p > 0)
                    && (0 <= roi.rollingNpv[p]) && (0 > roi.rollingNpv[p - 1])) {
                roi.breakevenPeriod = p + 1;
                // roi.breakevenRegression
                if (p > 0) {
                    roi.breakevenRegression = p
                            + 1
                            - (roi.rollingNpv[p - 1] / (double) (roi.rollingNpv[p] - roi.rollingNpv[p - 1]));
                }
            }
        }
        roi.presentValue[periods] = (int) Math.round(sumPV);

        // roi.roi
        roi.roi = ((double) -roi.cash[periods]) / roi.investment[periods];

        return roi;
    }
}
