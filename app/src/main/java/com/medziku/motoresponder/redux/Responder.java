package com.medziku.motoresponder.redux;

import android.content.Context;
import com.medziku.motoresponder.redux.sideeffects.Calls;
import com.medziku.motoresponder.redux.sideeffects.Messages;


/**
 * Main component
 */
public class Responder {


    private final AppStore store;
    private final Context context;
    private Messages messages;
    private Calls calls;

    public Responder(AppStore store, Context context) {
        this.context=context;
        this.store = store;

        this.messages=new Messages(store,context);
        this.calls=new Calls(store,context);
    }

    public void start() {
        this.messages.start();
        this.calls.start();

    }

    public void stop() {
        this.messages.stop();
        this.calls.stop();
    }

}
