/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import no.ntnu.mmfplanner.util.TangoColor;

/**
 * A category contains a name, color and parent. Categories can be arranged in a
 * tree hierarchy using the parent variable. Leaf nodes of this tree are then
 * MMFs.
 *
 * Each project can contain a list of categories, and each MMF can have one
 * category set from it's project.
 */
public class Category {
    public static final String EVENT_NAME = "category.name";
    public static final String EVENT_COLOR = "category.color";
    public static final String EVENT_PARENT = "category.parent";

    public static final Color CATEGORY_COLORS[] = new Color[] { Color.WHITE,
            TangoColor.BUTTER_1, TangoColor.ORANGE_1, TangoColor.CHAMELEON_1,
            Color.DARK_GRAY };

    private String name;
    private Color color;
    private Category parent;
    private PropertyChangeSupport changeSupport;

    /**
     * Create a new empty category
     */
    public Category() {
        this(null, null, null);
    }

    /**
     * Create a new category with the given name color and parent. All values
     * could be null.
     *
     * @param name
     * @param color
     * @param parent
     */
    public Category(String name, Color color, Category parent) {
        this.name = name;
        this.color = color;
        this.parent = parent;
        this.changeSupport = new PropertyChangeSupport(this);
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name and fires a EVENT_NAME event.
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        changeSupport.firePropertyChange(EVENT_NAME, oldValue, name);
    }

    public Color getColor() {
        return color;
    }

    /**
     * Sets the color and fires a EVENT_COLOR event.
     */
    public void setColor(Color color) {
        Color oldValue = this.color;
        this.color = color;
        changeSupport.firePropertyChange(EVENT_COLOR, oldValue, color);
    }

    public Category getParent() {
        return parent;
    }

    /**
     * Sets the parent and fires a EVENT_PARENT event.
     * @throws MmfException
     */
    public void setParent(Category parent) throws MmfException {
        checkValidParent(parent);
        Category oldValue = this.parent;
        this.parent = parent;
        changeSupport.firePropertyChange(EVENT_PARENT, oldValue, parent);
    }

    /**
     * Checks that the given parent is valid. This only tests that the parents
     * will not become circular, it does not test that they are both in the same
     * project.
     * @throws MmfException
     */
    private void checkValidParent(Category parent) throws MmfException {
        while (parent != null) {
            if (this == parent) {
                throw new MmfException(
                        "Category can not be a parent to itself");
            }
            parent = parent.getParent();
        }
    }

    /**
     * Add a PropertyChangeListener to be notified of changes to this object.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
