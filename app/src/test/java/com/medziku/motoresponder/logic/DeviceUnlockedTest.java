package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SettingsUtility;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DeviceUnlockedTest {
    private SettingsUtility settingsUtility;
    private LockStateUtility lockStateUtility;
    private DeviceUnlocked deviceUnlocked;

    @Before
    public void beforeTest() {
        this.lockStateUtility = Mockito.mock(LockStateUtility.class);
        this.settingsUtility = Mockito.mock(SettingsUtility.class);
        this.deviceUnlocked = new DeviceUnlocked(this.settingsUtility, this.lockStateUtility);
    }

    @Test
    public void testDeviceUnlockShouldPreventResponding() throws Exception {
        Mockito.when(this.settingsUtility.isPhoneUnlockedInterpretedAsNotRiding()).thenReturn(true);
        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(false);
        Assert.assertTrue(this.deviceUnlocked.isNotRiding() == false);

        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(true);
        Assert.assertTrue(this.deviceUnlocked.isNotRiding() == true);

        Mockito.when(this.settingsUtility.isPhoneUnlockedInterpretedAsNotRiding()).thenReturn(false);
        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(false);
        Assert.assertTrue(this.deviceUnlocked.isNotRiding() == false);


        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(true);
        Assert.assertTrue(this.deviceUnlocked.isNotRiding() == false);

    }
}