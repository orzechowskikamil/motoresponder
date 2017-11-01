package com.medziku.motoresponder.redux;

public final class Actions {
    public enum GPS {ON, OFF, LOCATION_UPDATE}

    public enum Accelerometer {ON, OFF, MOTION_EVENT}

    public enum RespondingProcess {}

    public enum Calls {INCOMING_CALL, CALL_LOG_UPDATE}

    public enum Messages {INCOMING_MESSAGE, OUTGOING_MESSAGES_LOG, SEND_SMS}

    public enum Settings {LOAD_SETTINGS, SETTINGS_UPDATED}

    public enum Contacts {LOAD_CONTACTS}

    public enum Proximity {PROXIMITY, NO_PROXIMITY}
    
    public enum Responses {RESPONSES_HANDLED}

    public enum AppBuild{PACKAGE_NAME}
}
