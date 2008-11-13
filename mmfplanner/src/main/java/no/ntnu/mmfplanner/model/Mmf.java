/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MMF model class
 * 
 * Has the MMF properties. Changes are handled and notifies if changes are made.
 */
public class Mmf {
	public static final String EVENT_ID = "mmf.id";
	public static final String EVENT_NAME = "mmf.name";
	public static final String EVENT_PERIOD = "mmf.period";
	public static final String EVENT_PERIOD_COUNT = "mmf.periodCount";
	public static final String EVENT_SWIMLANE = "mmf.swimlane";
	public static final String EVENT_CATEGORY = "mmf.category";
	public static final String EVENT_PRECURSORS = "mmf.precursors";
	public static final String EVENT_REVENUES = "mmf.revenues";
	public static final String EVENT_PROJECT = "mmf.project";
	private static final String EVENT_LOCKED = "mmf.locked";

	/**
	 * The unique ID of the MMF. This is used to identify the MMF and displayed
	 * in both the graph and table views. Some places this is the only
	 * identifier used.
	 */
	private String id;

	/**
	 * The name of the MMF. Should concisely describe what the MMF does. This is
	 * displayed in the graph view.
	 */
	private String name;

	/**
	 * A period is the basic time measurement used in MMFs. A period could be a
	 * week, a month, or any other period of time. This usually corresponds to
	 * one or more development iteration(s), so that there is enough time to
	 * finish at least one MMF. The periods could also be used more loosely,
	 * simply identifying the approximate order of work.
	 * <p>
	 * This value assigns period work on the MMF will start. This is where the
	 * first part of the MMF is drawn. If periodCount == 1 then work will also
	 * finish in this period.
	 */
	private int period;

	/**
	 * The number of consecutive periods that will be used working on an MMF.
	 * This is usually equal to 1, as the general structure of the project is
	 * more important than the exact period count.
	 */
	private int periodCount;

	/**
	 * The view is divided into several swimlanes from top to bottom. This
	 * parameter tells which swimlane the MMF will be drawn in. This parameter
	 * has no effect expect for positioning in the graph view.
	 */
	private int swimlane;

	/**
	 * The category for the MMF
	 */
	private Category category;

	/**
	 * List of precursors by id string. These are MMFs that have to be completed
	 * before this MMF. In the graph view this is shown with a directed edge
	 * from the precursors towards this MMF. Usually the MMF is positioned in a
	 * period after all precursors are completed.
	 */
	private List<Mmf> precursors;

	/**
	 * ArrayList of integers representing the revenue for each period.
	 */
	private List<Integer> revenues;

	/**
	 * The project this MMF belongs to.
	 */
	private Project project;

	private PropertyChangeSupport changeSupport;

	/**
	 * Specify a MMF as locked to it's period. Helps automatic sorting.
	 */
	private boolean locked;

	/**
	 * Creates a new MMF with the given id and name.
	 * 
	 * @param id
	 * @param name
	 */
	public Mmf(String id, String name) {
		this.id = id;
		this.name = name;
		this.period = 1;
		this.periodCount = 1;
		this.swimlane = 1;
		this.precursors = new ArrayList<Mmf>();
		this.category = null;
		this.revenues = new ArrayList<Integer>();
		this.locked = false;

		this.changeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Returns a string representation of this MMF.
	 */
	@Override
	public String toString() {
		return "MMF " + id + ": " + name + " [" + period + "," + swimlane
				+ "] > " + precursors;
	}

	public String getId() {
		return id;
	}

	/**
	 * Sets the id and fires an EVENT_ID event. The id must a valid id, and not
	 * have duplicates in the project.
	 * 
	 * @param id
	 * @throws MmfException
	 */
	public void setId(String id) throws MmfException {
		if ((null != project) && !project.isValidId(id)) {
			throw new MmfException("The id is not valid or has a duplicate: "
					+ id);
		}
		String oldValue = this.id;
		this.id = id;
		changeSupport.firePropertyChange(EVENT_ID, oldValue, id);
	}

	public String getName() {
		return name;
	}

	/**
	 * Sets the name and fires an EVENT_NAME event
	 * 
	 * @param name
	 */
	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		changeSupport.firePropertyChange(EVENT_NAME, oldValue, name);
	}

	public int getPeriod() {
		return period;
	}

	/**
	 * Sets the period and fires an EVENT_PERIOD event
	 * 
	 * @param period
	 *            Valid values of period are 1 &lt;= period < ...
	 * @throws MmfException
	 */
	public void setPeriod(int period) throws MmfException {
		if (period < 1) {
			throw new MmfException("Invalid period: " + period);
		}
		if (period == this.period) {
			return;
		}
		try {
			if (project != null) {
				setSwimlane(project.getFirstFreeSwimlane(period, swimlane));
			}
		} catch (MmfException e) {
			// Should never happen!
			e.printStackTrace();
		}

		int oldValue = this.period;
		this.period = period;
		changeSupport.firePropertyChange(EVENT_PERIOD, oldValue, period);
	}

	public int getSwimlane() {
		return swimlane;
	}

	/**
	 * Sets the swimlane and fires an EVENT_SWIMLANE event
	 * 
	 * @param swimlane
	 *            Valid values of swimlane are 1 &lt;= swimlane < ...
	 * @throws MmfException
	 */
	public void setSwimlane(int swimlane) throws MmfException {
		if (swimlane < 1) {
			throw new MmfException("Invalid swimlane: " + swimlane);
		}

		int oldValue = this.swimlane;
		this.swimlane = swimlane;
		changeSupport.firePropertyChange(EVENT_SWIMLANE, oldValue, swimlane);
	}

	public int getPeriodCount() {
		return periodCount;
	}

	/**
	 * Sets the periodCount and fires an EVENT_PERIOD_COUNT event
	 * 
	 * @param periodCount
	 *            Valid values of periodCount are 1 &lt;= periodCount < ...
	 * @throws MmfException
	 */
	public void setPeriodCount(int periodCount) throws MmfException {
		if (periodCount < 1) {
			throw new MmfException("Invalid periodCount: " + periodCount);
		}

		int oldValue = this.periodCount;
		this.periodCount = periodCount;
		changeSupport.firePropertyChange(EVENT_PERIOD_COUNT, oldValue,
				periodCount);
	}

	public List<Mmf> getPrecursors() {
		return Collections.unmodifiableList(precursors);
	}

	/**
	 * @return all precursors as a comma-separated string of ids.
	 */
	public String getPrecursorString() {
		if (precursors.size() == 0) {
			return "";
		} else {
			String result = "";
			for (Mmf mmfPre : precursors) {
				result += ", " + mmfPre.getId();
			}
			return result.substring(2);
		}
	}

	/**
	 * Sets all the precurors
	 * 
	 * @param prestring
	 */
	public void setPrecursorString(String prestring) throws MmfException {
		List<Mmf> newPrecursors = new ArrayList<Mmf>();

		Pattern pattern = Pattern.compile("Z*[A-Y]");
		Matcher matcher = pattern.matcher(prestring.toUpperCase());

		// check validity of all new precursors
		while (matcher.find()) {
			Mmf preMmf = project.get(matcher.group());
			if (newPrecursors.contains(preMmf)) {
				continue;
			}
			checkValidPrecursor(preMmf);
			newPrecursors.add(preMmf);
		}

		// replace existing list
		this.precursors = newPrecursors;
		changeSupport.firePropertyChange(EVENT_PRECURSORS, null, null);
	}

	/**
	 * Adds a precursor and fires an EVENT_PRECURSORS event. Will cause an
	 * exception if a circle of precedence will be created.
	 * 
	 * @param precursor
	 * @throws MmfException
	 */
	public void addPrecursor(Mmf precursor) throws MmfException {
		if (this.precursors.indexOf(precursor) < 0) {
			checkValidPrecursor(precursor);
			this.precursors.add(precursor);
			changeSupport.firePropertyChange(EVENT_PRECURSORS, null, precursor);
		}
	}

	/**
	 * Checks if the precursor is valid. Mostly that no circular precursors
	 * exists.
	 * 
	 * @param precursor
	 * @throws MmfException
	 */
	private void checkValidPrecursor(Mmf precursor) throws MmfException {
		if (null == precursor) {
			throw new MmfException("Precursor does not exist");
		} else if (this.getProject() != precursor.getProject()) {
			throw new MmfException(
					"Precursor is not a part of the same project");
		} else if (this == precursor) {
			throw new MmfException(
					"MMF can not be a precursor to itself (circular precedence)");
		}
		List<Mmf> prePre = precursor.getPrecursors();
		for (Mmf pre : prePre) {
			checkValidPrecursor(pre);
		}
	}

	/**
	 * Remove a precursor and fires an EVENT_PRECURSORS event.
	 * 
	 * @param precursor
	 */
	public void removePrecursor(Mmf precursor) {
		if (this.precursors.indexOf(precursor) >= 0) {
			this.precursors.remove(precursor);
			changeSupport.firePropertyChange(EVENT_PRECURSORS, precursor, null);
		}
	}

	public Category getCategory() {
		return category;
	}

	/**
	 * Sets the category of this MMF and fires an EVENT_CATEGORY event.
	 * 
	 * @param category
	 * @throws MmfException
	 */
	public void setCategory(Category category) throws MmfException {
		if ((null != project) && !project.isValidCategory(category)) {
			throw new MmfException("Category is not a part of this project: "
					+ category);
		}
		Category oldValue = this.category;
		this.category = category;
		changeSupport.firePropertyChange(EVENT_CATEGORY, oldValue, category);
	}

	/**
	 * Sets the revenue for the given period. Can be both positive and negative,
	 * and can be for a period beyond what is the periodCount in the project.
	 * 
	 * @param period
	 *            to set revenue for
	 * @param revenue
	 *            value of revenue in this period
	 */
	public void setRevenue(int period, int revenue) {
		while (period > revenues.size()) {
			revenues.add(0);
		}
		int oldValue = this.getRevenue(period);
		this.revenues.set(period - 1, revenue);
		changeSupport.firePropertyChange(EVENT_REVENUES, oldValue, revenue);
	}

	public int getRevenue(int period) {
		if (period > revenues.size()) {
			return 0;
		}
		return revenues.get(period - 1);
	}

	public int getRevenueLength() {
		return revenues.size();
	}

	/**
	 * Add a PropertyChangeListener to be notified of changes to this object
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

	/**
	 * Returns a list of SANPV for this project for each start period. This
	 * function will call getSaNpv(int, int) for each possible start period.
	 * 
	 * @param interestRate
	 */
	public int[] getSaNpvList(double interestRate) {
		int periods = project.getPeriods();
		int sanpv[] = new int[periods];
		for (int p = 0; p < periods; p++) {
			sanpv[p] = getSaNpv(interestRate, p);
		}
		return sanpv;
	}

	/**
	 * Returns the SANPV for the given start period and interest rate.
	 * 
	 * @param interestRate
	 * @param skipPeriods
	 * @throws MmfException
	 */
	public int getSaNpv(double interestRate, int skipPeriods) {
		if (skipPeriods < 0) {
			throw new IllegalArgumentException("Invalid startPeriod: "
					+ skipPeriods);
		}

		double npv = 0.0F;
		for (int p = 1; p <= project.getPeriods() - skipPeriods; p++) {
			int rev = getRevenue(p);
			int per = (skipPeriods + p);
			npv += rev / Math.pow(interestRate + 1, per);
		}
		return (int) Math.round(npv);
	}

	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project for this MMF and fires an EVENT_PROJECT event.
	 * 
	 * @param project
	 */
	public void setProject(Project project) {
		Project oldValue = this.project;
		this.project = project;
		changeSupport.firePropertyChange(EVENT_PROJECT, oldValue, project);
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		boolean oldValue = this.locked;
		this.locked = locked;
		changeSupport.firePropertyChange(EVENT_LOCKED, oldValue, locked);
	}
}