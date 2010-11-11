/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.action;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Filter for mmfproject files. All files ending with .mmfproject are accepted,
 * as well as directories.
 */
public class MmfprojectFileFilter extends FileFilter {
    public static final String SUFFIX = ".mmfproject";

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        return (f != null) && (f.isDirectory() || f.getName().endsWith(SUFFIX));
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return "MMF Projects";
    }

}
