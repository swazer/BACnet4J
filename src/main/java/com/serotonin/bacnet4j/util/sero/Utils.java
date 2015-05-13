package com.serotonin.bacnet4j.util.sero;

public class Utils {
    // TODO replace with Objects.equals when upgraded to Java8.
    public static boolean equals(final Object object1, final Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }
}
