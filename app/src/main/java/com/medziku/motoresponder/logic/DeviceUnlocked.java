package com.medziku.motoresponder.logic;


import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SettingsUtility;

public class DeviceUnlocked {

    private SettingsUtility settingsUtility;
    private LockStateUtility lockStateUtility;



    /**
     * For real usage
     * @param settingsUtility
     * @param lockStateUtility
     */
    public DeviceUnlocked(SettingsUtility settingsUtility, LockStateUtility lockStateUtility) {
        this.settingsUtility = settingsUtility;
        this.lockStateUtility = lockStateUtility;
    }

    public boolean isNotRiding() {
        return this.settingsUtility.isPhoneUnlockedInterpretedAsNotRiding() && this.lockStateUtility.isPhoneUnlocked();
    }
}
