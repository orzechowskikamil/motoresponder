package com.medziku.motoresponder.redux;

import android.content.Context;
import com.medziku.motoresponder.redux.sideeffects.CallLog;
import com.medziku.motoresponder.redux.sideeffects.CallsListener;
import com.medziku.motoresponder.redux.sideeffects.base.SideEffect;


/**
 * Main component
 */
public class Responder {

    private final Store store;
    private final Context context;

    private SideEffect[] sideEffectsList;

    public Responder(Store store, Context context) {
        this.context = context;
        this.store = store;

        this.sideEffectsList = new SideEffect[]{
                new CallLog(),
                new CallsListener()
        };
    }

    public void start() {
        for (SideEffect effect : this.sideEffectsList) {
            effect.start(this.context, this.store);
        }
    }

    public void stop() {
        for (SideEffect effect : this.sideEffectsList) {
            effect.stop();
        }
    }

}
