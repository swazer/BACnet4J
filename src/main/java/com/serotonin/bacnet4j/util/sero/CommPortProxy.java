/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.util.sero;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy class to abstract Comm Ports
 * 
 * @author Matthew Lohbihler
 */
public class CommPortProxy {
    static final Logger LOG = LoggerFactory.getLogger(CommPortProxy.class);

    private final String name;
    private String portType;
    private final boolean currentlyOwned;
    private final String currentOwner;
    private String hardwareId;
    private String product;

    /**
     * 
     * @param cpid
     */
    public CommPortProxy(CommPortIdentifier cpid) {
        name = cpid.getName();
        switch (cpid.getPortType()) {
        case CommPortIdentifier.PORT_SERIAL:
            portType = "Serial";
            break;
        case CommPortIdentifier.PORT_PARALLEL:
            portType = "Parallel";
            break;
        default:
            portType = "Unknown (" + cpid.getPortType() + ")";
        }
        currentlyOwned = cpid.isCurrentlyOwned();
        currentOwner = cpid.getCurrentOwner();

        if (LOG.isDebugEnabled()) {
            String output = "Creating comm port with id: " + cpid.getName();
            if (currentlyOwned)
                output += " Owned by " + cpid.getCurrentOwner();
            LOG.debug(output);
        }

    }

    public CommPortProxy(String name, boolean serial) {
        this.name = name;
        portType = serial ? "Serial" : "Parallel";
        currentlyOwned = false;
        currentOwner = null;

        if (LOG.isDebugEnabled())
            LOG.debug("Creating comm port with id: " + name);
    }

    public boolean isCurrentlyOwned() {
        return currentlyOwned;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public String getName() {
        return name;
    }

    public String getPortType() {
        return portType;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getId() {
        if (StringUtils.isEmpty(hardwareId))
            return name;
        return hardwareId;
    }

    public String getDescription() {
        if (!StringUtils.isEmpty(hardwareId) && !StringUtils.isEmpty(product))
            return hardwareId + " (" + product.trim() + ")";
        if (!StringUtils.isEmpty(hardwareId))
            return hardwareId + " (" + name + ")";
        return name;
    }
}
