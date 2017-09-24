package com.medziku.motoresponder.redux;

import java.util.ArrayList;
import java.util.List;

class State {
    GPS gps;
    Accelerometer accelerometer;
    RespondingProcesses respondingProcesses;
    Settings settings;
    boolean proximity;
}

class Accelerometer {
    List<AccelerometerEvent> eventsList;
    boolean isEnabled;

    public Accelerometer(Accelerometer old) {
        this.eventsList = old.eventsList;
        this.isEnabled = old.isEnabled;
    }

    public Accelerometer() {
    }

    public Accelerometer clone() {
        return new Accelerometer(this);
    }

    public List<AccelerometerEvent> getClearEventsList() {
        return new ArrayList<>();
    }

    public List<AccelerometerEvent> getEventsListWith(AccelerometerEvent event) {
        List<AccelerometerEvent> list = new ArrayList<>(this.eventsList);
        list.add(event);
        return list;
    }
}

class AccelerometerEvent {
}


class RespondingProcesses {
    List<RespondingProcess> list;
    int nextId;


    public RespondingProcesses(RespondingProcesses old) {
        this.list = old.list;
        this.nextId = old.nextId;
    }

    public RespondingProcesses() {


    }

    public RespondingProcesses clone() {
        return new RespondingProcesses(this);
    }

    public List<RespondingProcess> getListWith(RespondingProcess newProcess) {
        List<RespondingProcess> list = new ArrayList<>(this.list);
        list.add(newProcess);
        return list;
    }
}

abstract class RespondingProcess {
    String phoneNumber;
    int id;

    public RespondingProcess(String phoneNumber, int id) {
        this.phoneNumber = "";
        this.id = id;
    }
}

class CallRespondingProcess extends RespondingProcess {
    public CallRespondingProcess(String phoneNumber, int id) {
        super(phoneNumber, id);
    }
}

class MessageRespondingProcess extends RespondingProcess {
    String message;

    public MessageRespondingProcess(String phoneNumber, String message, int id) {
        super(phoneNumber, id);
        this.message = message;
    }
}

class Settings {
    boolean isEnabled;

    public Settings() {
        this.isEnabled = false;
    }

    public Settings(Settings old) {
        this.isEnabled = old.isEnabled;
    }

    public Settings clone() {
        return new Settings(this);
    }
}

class Contacts {
}

class GPS {
    List<LocationUpdate> updatesList;
    boolean isEnabled;

    public GPS(GPS gps) {
        this.updatesList = gps.updatesList;
        this.isEnabled = gps.isEnabled;
    }

    public GPS() {

    }

    public GPS clone() {
        return new GPS(this);
    }

    public List<LocationUpdate> getClearUpdatesList() {
        return new ArrayList<>();
    }

    public List<LocationUpdate> getUpdatesListWith(LocationUpdate event) {
        List<LocationUpdate> list = new ArrayList<>(this.updatesList);
        list.add(event);
        return list;
    }
}


class LocationUpdate {
}


class Default {
    public static State build() {
        State state = new State();

        state.gps = new GPS();
        state.gps.isEnabled = false;
        state.gps.updatesList = state.gps.getClearUpdatesList();

        state.respondingProcesses = new RespondingProcesses();
        state.respondingProcesses.list = new ArrayList<>();
        state.respondingProcesses.nextId = 0;

        state.proximity = false;

        state.settings = new Settings();
        state.settings.isEnabled = false;

        state.accelerometer = new Accelerometer();
        state.accelerometer.eventsList = state.accelerometer.getClearEventsList();
        state.accelerometer.isEnabled = false;

        return state;
    }
}
