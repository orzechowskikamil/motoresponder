package com.medziku.motoresponder.redux;

public final class Actions {
    public enum GPS {ON, OFF, LOCATION_UPDATE}

    public enum Accelerometer {ON, OFF, MOTION_EVENT}

    public enum RespondingProcess {}

    public enum Calls {INCOMING_CALL}

    public enum Messages {INCOMING_MESSAGE, SEND_SMS}

    public enum Settings {LOAD_SETTINGS, SETTINGS_UPDATED}

    public enum Contacts {LOAD_CONTACTS}

    public enum Proximity {PROXIMITY, NO_PROXIMITY}
    
    public enum Responses {RESPONSE_SEND}
}
