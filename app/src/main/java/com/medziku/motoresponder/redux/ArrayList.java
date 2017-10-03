package com.medziku.motoresponder.redux;

import com.android.internal.util.Predicate;

import java.util.Collection;

/**
 * Project specific version of array list which returns new list instead of modyfing original for some operations.
 * Inspired by JS methods like map, forEach, etc
 */
public class ArrayList<E> extends java.util.ArrayList<E> {

    public ArrayList() {
        super();
    }

    public ArrayList(Collection<E> collection) {
        super(collection);
    }

    
    public ArrayList<F> map<F>(Function<F,E> iterator){
          ArrayList<F> mapResult = new ArrayList<>();

        for (E item : this) {
          mapResult.add(iterator.call(item));
      
        }

        return mapResult;
    }

    public ArrayList<E> filter(Predicate<E> iterator) {
        ArrayList<E> mapResult = new ArrayList<>();

        for (E item : this) {
            if (iterator.apply(item)) {
                mapResult.add(item);
            }
        }

        return mapResult;
    }


    public ArrayList<E> substract(E item) {
        if (item == null) {
            return this;
        }

        ArrayList<E> clone = (ArrayList<E>) this.clone();

        clone.remove(item);
        return clone;
    }


    public ArrayList<E> substract(Collection<E> items) {
        if (items == null || items.size() == 0) {
            return this;
        }

        ArrayList<E> clone = (ArrayList<E>) this.clone();

        clone.removeAll(items);
        return clone;
    }

    public ArrayList<E> union(E item) {
        if (item == null) {
            return this;
        }

        ArrayList<E> clone = (ArrayList<E>) this.clone();

        clone.add(item);
        return clone;
    }

    public ArrayList<E> union(Collection<E> items) {
        if (items == null || items.size() == 0) {
            return this;
        }

        ArrayList<E> clone = (ArrayList<E>) this.clone();

        clone.addAll(items);
        return clone;
    }
}
