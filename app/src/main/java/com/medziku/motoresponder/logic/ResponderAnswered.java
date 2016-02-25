package com.medziku.motoresponder.logic;

import android.content.Context;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SettingsUtility;

public class ResponderAnswered {

    private LockStateUtility lockStateUtility;
    private SettingsUtility settingsUtility;

    public ResponderAnswered(SettingsUtility settingsUtility, LockStateUtility lockStateUtility) {
        this.settingsUtility = settingsUtility;
        this.lockStateUtility = lockStateUtility;
    }


    public boolean responderAnsweredFromLastUserAction(String phoneNumber) {
        // TODO if there was already an auto response to this number, return true.
        return false;
    }

    public boolean tooMuchAutomaticalAnswersIn24h(String phoneNumber) {
        return false;
    }

    // TODO K. Orzechowski: Rename class or move this method to more suitable class.
    public boolean shouldNotRespondBecauseDeviceUnlocked() {
        return this.settingsUtility.isPhoneUnlockedInterpretedAsNotRiding()
                && this.lockStateUtility.isPhoneUnlocked();
    }
}
