package com.medziku.motoresponder.redux.sideeffects.base;

import android.content.Context;
import com.medziku.motoresponder.redux.Store;


/**
 * Base class for SideEffect which uses Android context
 */
abstract public class ContextSideEffect implements SideEffect {
    protected Context context;
    protected Store store;
    boolean isStarted = false;

    public void start(Context context, Store store) {
        if (this.isStarted) {
            throw new RuntimeException("Please, don't afterStart twice");
        }
        this.isStarted = true;
        this.context = context;
        this.store = store;
        this.afterStart();
    }

    public void stop() {
        if (!this.isStarted) {
            throw new RuntimeException("Don't stop something which is not started");
        }
        this.isStarted = false;
        this.beforeStop();
    }

    abstract protected void afterStart();

    abstract protected void beforeStop();
}
