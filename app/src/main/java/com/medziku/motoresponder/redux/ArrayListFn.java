package com.medziku.motoresponder.redux;

import com.android.internal.util.Predicate;

import java.util.Collection;

/**
 * Project specific version of array list which returns new list instead of modyfing original for some operations.
 * Inspired by JS methods like map, forEach, etc
 */
public class ArrayListFn<E> extends java.util.ArrayList<E> {

    public ArrayListFn() {
        super();
    }

    public ArrayListFn(Collection<E> collection) {
        super(collection);
    }


    public ArrayListFn<E> map(Predicate<E> iterator) {
        ArrayListFn<E> mapResult = new ArrayListFn<>();

        for (E item : this) {
            if (iterator.apply(item)) {
                mapResult.add(item);
            }
        }

        return mapResult;
    }


    public ArrayListFn<E> substract(E item) {
        if (item == null) {
            return this;
        }

        ArrayListFn<E> clone = (ArrayListFn<E>) this.clone();

        clone.remove(item);
        return clone;
    }


    public ArrayListFn<E> substract(Collection<E> items) {
        if (items == null || items.size() == 0) {
            return this;
        }

        ArrayListFn<E> clone = (ArrayListFn<E>) this.clone();

        clone.removeAll(items);
        return clone;
    }

    public ArrayListFn<E> union(E item) {
        if (item == null) {
            return this;
        }

        ArrayListFn<E> clone = (ArrayListFn<E>) this.clone();

        clone.add(item);
        return clone;
    }

    public ArrayListFn<E> union(Collection<E> items) {
        if (items == null || items.size() == 0) {
            return this;
        }

        ArrayListFn<E> clone = (ArrayListFn<E>) this.clone();

        clone.addAll(items);
        return clone;
    }
}
