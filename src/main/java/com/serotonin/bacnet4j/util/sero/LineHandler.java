/*
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
 	@author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.util.sero;

/**
 * @author Matthew Lohbihler
 */
public interface LineHandler {
    public void handleLine(String line);

    public void done();
}
