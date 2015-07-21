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
package com.serotonin.bacnet4j.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.serotonin.bacnet4j.type.constructed.PropertyReference;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class PropertyReferences implements Serializable {
    private static final long serialVersionUID = -1512876955215003611L;

    private final Map<ObjectIdentifier, List<PropertyReference>> properties = new LinkedHashMap<ObjectIdentifier, List<PropertyReference>>();

    public void add(ObjectIdentifier oid, PropertyReference... refs) {
        List<PropertyReference> list = getOidList(oid);
        for (PropertyReference ref : refs)
            list.add(ref);
    }

    public void add(ObjectIdentifier oid, PropertyIdentifier... pids) {
        List<PropertyReference> list = getOidList(oid);
        for (PropertyIdentifier pid : pids)
            list.add(new PropertyReference(pid));
    }

    public void add(ObjectIdentifier oid, PropertyIdentifier pid, UnsignedInteger propertyArrayIndex) {
        List<PropertyReference> list = getOidList(oid);
        list.add(new PropertyReference(pid, propertyArrayIndex));
    }

    private List<PropertyReference> getOidList(ObjectIdentifier oid) {
        List<PropertyReference> list = properties.get(oid);
        if (list == null) {
            list = new ArrayList<PropertyReference>();
            properties.put(oid, list);
        }
        return list;
    }

    public Map<ObjectIdentifier, List<PropertyReference>> getProperties() {
        return properties;
    }

    public List<PropertyReferences> getPropertiesPartitioned(int maxPartitionSize) {
        List<PropertyReferences> partitions = new ArrayList<PropertyReferences>();

        if (size() <= maxPartitionSize)
            partitions.add(this);
        else {
            PropertyReferences partition = null;
            List<PropertyReference> refs;
            for (ObjectIdentifier oid : properties.keySet()) {
                refs = properties.get(oid);
                for (PropertyReference ref : refs) {
                    if (partition == null || partition.size() >= maxPartitionSize) {
                        partition = new PropertyReferences();
                        partitions.add(partition);
                    }
                    partition.add(oid, ref);
                }
            }
        }

        return partitions;
    }

    public int size() {
        int size = 0;
        for (ObjectIdentifier oid : properties.keySet())
            size += properties.get(oid).size();
        return size;
    }
}
