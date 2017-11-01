package com.medziku.motoresponder.redux.sideeffects.base;


import android.content.Context;
import com.medziku.motoresponder.redux.Store;

/**
 * Context side effect which subscribes into store changes
 */
abstract public class SubscribedContextSideEffect extends ContextSideEffect {

    private Runnable unsubscribe;

    abstract public void onStoreChanged();

    @Override
    public void start(Context context, Store store) {
        super.start(context, store);
        this.unsubscribe = this.store.subscribe(new Runnable() {
            public void run() {
                SubscribedContextSideEffect.this.onStoreChanged();
            }
        });
    }

    public void stop() {
        super.stop();
        this.unsubscribe.run();
    }
}
