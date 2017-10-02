package com.medziku.motoresponder.redux.sideeffects;

import android.content.Context;
import android.util.Pair;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.Store;
import com.medziku.motoresponder.utils.SMSObject;
import com.medziku.motoresponder.utils.SMSUtility;
import trikita.jedux.Action;

public class Messages {

    private final Store store;
    private final SMSUtility smsUtility;

    public Messages(Store store, Context context) {
        this.store = store;
        this.smsUtility = new SMSUtility(context);
    }

    public void start() {
        //new method listenForSms
        this.smsUtility.listenForSMS(new Predicate<SMSObject>() {
            @Override
            public boolean apply(SMSObject input) {
                Action action = new Action(Actions.Messages.INCOMING_MESSAGE, new Pair<String, String>(input.phoneNumber, input.message));
                Messages.this.store.dispatch(action);
                return false;
            }
        });
        
        // new method listenForPendingResponses
        Runnable unsubscribeFromStore=this.store.subscribe(new Runnable(){
            public void run(){
                this.handleResponses();
            }
        });
    }
    
    public void handleResponses(){
        List<int> handledResponsesIds=new ArrayList<>();
                
                for (Response response:Messages.this.store.getState().responses.list){
                    if (response instanceof SmsResponse){
                        this.smsUtility.sendSms();
                        handleResponsesIds.add(response.id);
                    }
                }
        
        this.store.dispatch(new Action(Actions.Response.HANDLED_RESPONSES,handleResponsesIds));
    }

    public void stop() {
        this.smsUtility.stopListeningForSMS();
        this.unsubscribeFromStore.run();
    }
}
