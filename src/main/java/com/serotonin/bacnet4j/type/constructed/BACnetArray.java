package com.serotonin.bacnet4j.type.constructed;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class BACnetArray<E extends Encodable> extends SequenceOf<E> {
    private static final long serialVersionUID = 7930010486564820089L;

    @SuppressWarnings("unchecked")
    public BACnetArray(int size) {
        super((List<E>) nullList(size));
    }

    private static final <T extends Encodable> List<T> nullList(int size) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < size; i++)
            list.add(null);
        return list;
    }

    public BACnetArray(E... values) {
        super(values);
    }

    public BACnetArray(List<E> values) {
        super(values);
    }

    public BACnetArray(BACnetArray<E> that) {
        super(that.values);
    }

    public BACnetArray(ByteQueue queue, Class<E> clazz, int contextId) throws BACnetException {
        super(queue, clazz, contextId);
    }

    @Override
    public void set(int indexBase1, E value) {
        values.set(indexBase1 - 1, value);
    }

    @Override
    public void add(E value) {
        throw new BACnetRuntimeException("Illegal operation");
    }

    @Override
    public void remove(int indexBase1) {
        throw new BACnetRuntimeException("Illegal operation");
    }

    @Override
    public void remove(E value) {
        throw new BACnetRuntimeException("Illegal operation");
    }

    @Override
    public void removeAll(E value) {
        throw new BACnetRuntimeException("Illegal operation");
    }
}
