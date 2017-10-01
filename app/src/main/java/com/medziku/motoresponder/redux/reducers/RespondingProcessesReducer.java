package com.medziku.motoresponder.redux.reducers;

import android.util.Pair;
import com.android.internal.util.Predicate;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.ArrayListFn;
import com.medziku.motoresponder.redux.State;
import com.medziku.motoresponder.redux.state.RespondingProcesses;
import com.medziku.motoresponder.utils.SMSObject;
import trikita.jedux.Action;
import trikita.jedux.Store;

import java.util.List;

public class RespondingProcessesReducer implements Store.Reducer<Action, State> {

    public State reduce(Action action, State old) {
        State newState = old.clone();

        if (action.type == Actions.Calls.INCOMING_CALL || action.type == Actions.Messages.INCOMING_MESSAGE) {
            newState.respondingProcesses = this.reduceIncoming(action, newState.respondingProcesses);
        }

        newState = this.reducePendingProcesses(newState);

        return newState;
    }


    private RespondingProcesses reduceIncoming(Action action, RespondingProcesses old) {
        RespondingProcesses newProcesses = old.clone();
        RespondingProcesses.Process process = null;

        newProcesses.nextId++;

        if (action.type == Actions.Calls.INCOMING_CALL) {
            String phoneNumber = (String) action.value;
            process = new RespondingProcesses.CallProcess(phoneNumber, newProcesses.nextId);
        } else if (action.type == Actions.Messages.INCOMING_MESSAGE) {
            Pair pair = (Pair) action.value;
            String phoneNumber = (String) pair.first;
            String message = (String) pair.second;
            process = new RespondingProcesses.MessageProcess(phoneNumber, message, newProcesses.nextId);
        }


        // TODO combine it with actual settings later
        process.accelerometerCheck.enabled=true;
        process.numberRulesCheck.enabled=true;
        process.gpsCheck.enabled=true;
        process.screenOnCheck.enabled=true;
        process.proximityCheck.enabled=true;

        newProcesses.list = newProcesses.list.union(process);

        return newProcesses;
    }

    private State reducePendingProcesses(State old) {
        final State newState = old.clone();
        newState.respondingProcesses = newState.respondingProcesses.clone();
        newState.messages = newState.messages.clone();

        final List<SMSObject> smsesToSend = new ArrayListFn<>();

        newState.respondingProcesses.list = newState.respondingProcesses.list.map(new Predicate<RespondingProcesses.Process>() {
            @Override
            public boolean apply(RespondingProcesses.Process process) {
                // setting it to false will remove currently iterated process from new version of list
                boolean keepInList = true;
                boolean shouldRespond = RespondingProcessesReducer.this.shouldRespond(process);

                if (shouldRespond == true) {
                    smsesToSend.add(new SMSObject(process.phoneNumber, RespondingProcessesReducer.this.generateMessage()));
                    keepInList = false;
                }

                if (shouldRespond == false) {
                    keepInList = false;
                }

                // shouldRespond == null
                return keepInList;
            }
        });

        newState.messages.toSend = newState.messages.toSend.union(smsesToSend);
        return newState;
    }

    private String generateMessage() {
        return null;
    }

    /**
     * Reduces Process to Boolean value
     * <p>
     * true - this process should respond
     * false - this process shouldn't respond
     * null - this process is pending so response is undefined.
     */
    private Boolean shouldRespond(RespondingProcesses.Process process) {
        for (RespondingProcesses.Process.Check check : process.getConditionsAsList()) {
            if (check.isPending()) {
                return null;
            }

            if (check.isFailed()) {
                return false;
            }
        }

        // being here means that all conditions are fullfilled (SUCCED) or skipped (NOT_USED)
        return true;
    }
}


