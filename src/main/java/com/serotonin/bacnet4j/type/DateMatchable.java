package com.serotonin.bacnet4j.type;

import com.serotonin.bacnet4j.type.primitive.Date;

public interface DateMatchable {
    boolean matches(Date that);
}
