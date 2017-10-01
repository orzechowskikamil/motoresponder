package com.medziku.motoresponder.redux;

import trikita.jedux.Action;


/**
 * Wraps store - Writing Store<A,S> is not longer needed.
 */
public class Store {

    private final trikita.jedux.Store<Action,State> store;

    public Store(trikita.jedux.Store<Action,State> store) {
        this.store = store;
    }

    public static Store createDefault() {
        return new Store(new trikita.jedux.Store(new Reducer(), Default.build()));
    }

    public State dispatch(Action action) {
        return this.store.dispatch(action);
    }

    public State getState() {
        return this.store.getState();
    }

    public Runnable subscribe(Runnable r) {
        return this.store.subscribe(r);
    }
}
