package com.medziku.motoresponder.redux.sideeffects;

import android.content.Context;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.Store;
import com.medziku.motoresponder.utils.CallsUtility;
import trikita.jedux.Action;

public class Calls {
    private final Store store;
    private final CallsUtility callsUtility;

    public Calls(Store store, Context context) {
        this.store = store;
        this.callsUtility = new CallsUtility(context);
    }

    public void start() {
       this.unsubscribe= this.store.subscribe(new Runnable(){ run(){
            if (this.store.getState().calls.callLogIsFresh==false){
                Calls.this.store.dispatch(new Action(Actions.Calls.CALL_LOG_UPDATE), this.callsUtility.getCallLog());
            }
        }});
        
        
        this.callsUtility.listenForUnansweredCalls(new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                Calls.this.store.dispatch(new Action(Actions.Calls.INCOMING_CALL, input));
                return false;
            }
        });
    }

    public void stop() {
        this.callsUtility.stopListeningForCalls();
    }
}
