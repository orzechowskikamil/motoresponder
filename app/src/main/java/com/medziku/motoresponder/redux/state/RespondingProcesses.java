package com.medziku.motoresponder.redux.state;

import com.medziku.motoresponder.redux.ArrayListFn;

import java.util.Arrays;

public class RespondingProcesses implements Cloneable {
    public ArrayListFn<Process> list;
    public int nextId;

    public RespondingProcesses clone() {
        try {
            return (RespondingProcesses) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }


    public static abstract class Process implements Cloneable {
        public String phoneNumber;
        public int id;

        public Check proximityCheck;
        public Check screenOnCheck;
        public Check gpsCheck;
        public Check numberRulesCheck;
        public Check accelerometerCheck;

        public Process(String phoneNumber, int id) {
            this.phoneNumber = phoneNumber;
            this.id = id;
        }

        public ArrayListFn<Check> getConditionsAsList() {
            return new ArrayListFn<>(Arrays.asList(
                    this.proximityCheck,
                    this.screenOnCheck,
                    this.gpsCheck,
                    this.accelerometerCheck,
                    this.numberRulesCheck
            ));
        }

        public class Check {
            public boolean enabled;
            public Boolean result;

            public boolean isPending() {
                return this.enabled && this.result == null;
            }

            public boolean isFailed() {
                return this.enabled && this.result == false;
            }
        }


    }

    public static class CallProcess extends Process {
        public CallProcess(String phoneNumber, int id) {
            super(phoneNumber, id);
        }

        public CallProcess clone() {
            try {
                return (CallProcess) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }
    }

    public static class MessageProcess extends Process {
        String message;

        public MessageProcess(String phoneNumber, String message, int id) {
            super(phoneNumber, id);
            this.message = message;
        }

        public MessageProcess clone() {
            try {
                return (MessageProcess) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }
    }
}
