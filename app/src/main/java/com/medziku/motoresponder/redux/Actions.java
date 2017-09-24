package com.medziku.motoresponder.redux;

public final class Actions {
    public enum GPS {ON, OFF, LOCATION_UPDATE}
    public enum Accelerometer {ON, OFF, MOTION_EVENT}
    public enum RespondingProcess  { INCOMING_CALL, INCOMING_MESSAGE, RESPOND_WITH_MESSAGE   }
    public enum Settings {  LOAD_SETTINGS, SETTINGS_UPDATED  }
    public enum Contacts  { LOAD_CONTACTS   }
    public enum Proximity{ PROXIMITY, NO_PROXIMITY}
}
