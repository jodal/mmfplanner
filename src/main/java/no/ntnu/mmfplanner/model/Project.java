/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains all information related to an IFM Project. This will handle involved
 * MMFs, data-consistency, calculations that include more than one MMF. If will
 * also forward any events from MMFs and Categories so that it is easier for
 * observers to observe only the Project object and not handle which MMFs and
 * Categories are a part of the Project.
 */
public class Project implements PropertyChangeListener {

    public static final String EVENT_NAME = "project.name";
    public static final String EVENT_PERIODS = "project.periods";
    public static final String EVENT_INTEREST_RATE = "project.interestRate";
    public static final String EVENT_CATEGORIES = "project.categories";
    public static final String EVENT_MMFS = "project.mmfs";
    public static final String EVENT_MAX_MMFS = "project.maxMMFs";

    private String name;
    private int periods;
    private double interestRate;
    private List<Category> categories;
    private List<Mmf> mmfs;
    private String nextId;
    private int maxMmfsPerPeriod;

    private PropertyChangeSupport changeSupport;

    /**
     * Creates a new IFM Project with default values for all properties.
     */
    public Project() {
        this.name = "New MMF Project";
        this.periods = 12;
        this.interestRate = 0.008;
        this.nextId = "A";
        this.categories = new ArrayList<Category>();
        this.mmfs = new ArrayList<Mmf>();
        this.maxMmfsPerPeriod = 1;
        this.changeSupport = new PropertyChangeSupport(this);
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the project name and fires the event EVENT_NAME.
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        changeSupport.firePropertyChange(EVENT_NAME, oldValue, name);
    }

    public int getPeriods() {
        return periods;
    }

    /**
     * Sets the number of periods and fires the event EVENT_PERIODS. Must be
     * greater than 0. Will not make any changes to the revenue data of MMFs.
     * That means MMF will remember revenue data that is entered for periods
     * beyond this setting.
     *
     * @throws MmfException
     */
    public void setPeriods(int periods) throws MmfException {
        if ((periods < 1) || (periods > 105)) {
            throw new MmfException("Invalid number of periods: " + periods);
        }
        int oldValue = this.periods;
        this.periods = periods;
        changeSupport.firePropertyChange(EVENT_PERIODS, oldValue, periods);
    }

    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Sets the interest rate per period and fires the event
     * EVENT_INTEREST_RATE. The value should be absolute, not in percent. (i.e.
     * 12% = 0.12)
     */
    public void setInterestRate(double interestRate) {
        double oldValue = this.interestRate;
        this.interestRate = interestRate;
        changeSupport.firePropertyChange(EVENT_INTEREST_RATE, oldValue,
                interestRate);
    }

    /**
     * Adds the category to the list of categories and fires the
     * EVENT_CATEGORIES event.
     */
    public void addCategory(Category category) {
        if (categories.indexOf(category) >= 0) {
            throw new IllegalArgumentException("This category already exists: "
                    + category);
        }
        categories.add(category);
        category.addPropertyChangeListener(this);
        changeSupport.firePropertyChange(EVENT_CATEGORIES, null, category);
    }

    public int getFirstFreeSwimlane(int period, int swimlane) {
        // find occupied count of all swimlanes
        int occupied[] = new int[mmfs.size() + 1];
        for (int i = 0; i < mmfs.size(); i++) {
            int s = mmfs.get(i).getSwimlane() - 1;
            if ((mmfs.get(i).getPeriod() == period) && (s >= 0) && (s < occupied.length)) {
                occupied[s]++;
            }
        }

        // return first free swimlane
        for (int i = swimlane - 1; i < occupied.length; i++) {
            if (occupied[i] == 0) {
                return i + 1;
            }
        }
        // should not come this far
        return 1;
    }

    /**
     *
     * @param category
     * @return true of this is a category of the current project, false if not
     */
    public boolean isValidCategory(Category category) {
        return (null == category) || categories.contains(category);
    }

    public Category getCategory(int index) {
        return categories.get(index);
    }

    /**
     * @return a copy of the category list. Modifications to this list will not
     *         have any effect on the category list.
     */
    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /**
     * Removes the category from the list of categories and fires the
     * EVENT_CATEGORIES event.
     */
    public void removeCategory(Category category) {
        category.removePropertyChangeListener(this);
        // remove as parent category
        for (Category cat : categories) {
            if (category == cat.getParent()) {
                try {
                    cat.setParent(null);
                } catch (Exception e) {
                    // This should never occur, but we print the stack trace
                    // just in case
                    e.printStackTrace();
                }
            }
        }

        // remove as category from MMFs
        for (Mmf mmf : mmfs) {
            if (category == mmf.getCategory()) {
                try {
                    mmf.setCategory(null);
                } catch (Exception e) {
                    // This should never occur, but we print the stack trace
                    // just in case
                    e.printStackTrace();
                }
            }
        }

        categories.remove(category);
        changeSupport.firePropertyChange(EVENT_CATEGORIES, category, null);
    }

    public int getCategorySize() {
        return categories.size();
    }

    /**
     * @return the maxMMFsPerPeriod
     */
    public int getMaxMmfsPerPeriod() {
        return maxMmfsPerPeriod;

    }

    /**
     * @param maxMmfsPerPeriod the maxMMFsPerPeriod to set
     * @throws MmfException
     */
    public void setMaxMmfsPerPeriod(int maxMmfsPerPeriod) throws MmfException {
        if (maxMmfsPerPeriod < 0) {
            throw new MmfException("Invalid maxMMFsPerPeriod: "
                    + maxMmfsPerPeriod);
        }
        int oldValue = this.maxMmfsPerPeriod;
        this.maxMmfsPerPeriod = maxMmfsPerPeriod;
        changeSupport.firePropertyChange(EVENT_MAX_MMFS, oldValue,
                maxMmfsPerPeriod);
    }

    /**
     * Adds the mmf to the list of mmfs and fires the EVENT_MMFS event.
     *
     * If the MMF contains an invalid or duplicate id it will be reassigned a
     * new id.
     */
    public void add(Mmf mmf) {
        if (mmfs.indexOf(mmf) >= 0) {
            throw new IllegalArgumentException("This MMF already exists: "
                    + mmf);
        }

        if (!isValidId(mmf.getId())) {
            try {
                mmf.setId(getNextId());
            } catch (Exception e) {
                // Should never happend so we rethrow as a RuntimeException
                throw new IllegalArgumentException(
                        "isValidId() != isValidId()", e);
            }
        }
        mmf.setProject(this);
        try {
            mmf.setSwimlane(getFirstFreeSwimlane(mmf.getPeriod(), 1));
        } catch (MmfException e) {
            // Should never happen!
            e.printStackTrace();
        }
        mmf.addPropertyChangeListener(this);
        mmfs.add(mmf);
        changeSupport.firePropertyChange(EVENT_MMFS, null, mmf);
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }

    /**
     * Checks if the given id is valid and has no duplicates in the current
     * project
     *
     * @return true of valid, false if not or a duplicate exists
     */
    public boolean isValidId(String id) {
        return (null != id) && id.matches("Z*[A-Y]") && (null == get(id));
    }

    /**
     * @return the id that should be used for the next MMF that is added. The
     *         value is not increased until the next MMF is actually added.
     */
    public String getNextId() {
        // check if next id is correct.
        while (!isValidId(nextId)) {
            // find next id value
            char nextChar = (char) (1 + nextId.charAt(nextId.length() - 1));
            String pre = nextId.substring(0, nextId.length() - 1);
            nextId = pre + nextChar;
            if (nextChar == 'Z') {
                // We're at the last usable character in this set. We retry all
                // previous characters
                // in an attempt to avoid multiple characters, otherwise we add
                // another 'A' character
                for (int i = 0; i < 25 * 10 + 1; i++) {
                    nextId = "ZZZZZZZZZZ".substring(0, i / 25)
                            + (char) ('A' + i % 25);
                    if (isValidId(nextId)) {
                        return nextId;
                    }
                }
            }
        }
        return nextId;
    }

    public Mmf get(int index) {
        return mmfs.get(index);
    }

    public Mmf get(String id) {
        for (Mmf mmf : mmfs) {
            if (id.equals(mmf.getId())) {
                return mmf;
            }
        }
        return null;
    }

    /**
     * @return a unmodifiable copy of the mmf list
     */
    public List<Mmf> getMmfs() {
        return Collections.unmodifiableList(mmfs);
    }

    /**
     * Removes the mmf from the list of mmfs and fires the EVENT_MMFS event.
     */
    public void remove(Mmf mmf) {
        mmf.removePropertyChangeListener(this);
        for (Mmf m : mmfs) {
            if (m.getPrecursors().contains(mmf)) {
                m.removePrecursor(mmf);
            }
        }
        mmf.setProject(null);
        mmfs.remove(mmf);
        changeSupport.firePropertyChange(EVENT_MMFS, mmf, null);
    }

    /**
     * @return the number of MMFs in the project
     */
    public int size() {
        return mmfs.size();
    }

    /**
     * @return An array of SANPV arrays. The first element is the SANPV array of
     *         the first MMF, etc.
     */
    public int[][] getSaNpvTable() {
        int table[][] = new int[size()][getPeriods()];
        for (int i = 0; i < table.length; i++) {
            table[i] = get(i).getSaNpvList(interestRate);
        }
        return table;
    }

    /**
     * Is called whenever there is a change in a child MMF or Category. Project
     * does not directly use this, but forwards all events to the
     * PropertyChangeListeners of this project.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        changeSupport.firePropertyChange(evt);
    }

    /**
     * Add a PropertyChangeListener to be notified of changes to this object or
     * child objects (MMFs and Categories)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}