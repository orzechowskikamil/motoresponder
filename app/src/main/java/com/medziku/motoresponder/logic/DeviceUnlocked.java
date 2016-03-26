package com.medziku.motoresponder.logic;


import com.medziku.motoresponder.utils.LockStateUtility;

public class DeviceUnlocked {

    private Settings settings;
    private LockStateUtility lockStateUtility;


    /**
     * For real usage
     *
     * @param settings
     * @param lockStateUtility
     */
    public DeviceUnlocked(Settings settings, LockStateUtility lockStateUtility) {
        this.settings = settings;
        this.lockStateUtility = lockStateUtility;
    }

    public boolean isNotRidingBecausePhoneUnlocked() {
        return this.settings.isPhoneUnlockedInterpretedAsNotRiding() && this.lockStateUtility.isPhoneUnlocked();
    }
}
