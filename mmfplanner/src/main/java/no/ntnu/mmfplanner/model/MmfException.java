/*
 * $Id: MmfException.java 1403 2007-11-17 14:19:58Z erikbagg $
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
 * Exception to throw when you get an unexpected error in the MMF models. This
 * is only used for errors that should be presented to the user.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class MmfException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public MmfException() {
    }

    /**
     * @param message
     */
    public MmfException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public MmfException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public MmfException(String message, Throwable cause) {
        super(message, cause);
    }

}
