package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DeviceUnlockedTest {
    private Settings settings;
    private LockStateUtility lockStateUtility;
    private DeviceUnlocked deviceUnlocked;

    @Before
    public void beforeTest() {
        this.lockStateUtility = Mockito.mock(LockStateUtility.class);
        this.settings = Mockito.mock(Settings.class);
        this.deviceUnlocked = new DeviceUnlocked(this.settings, this.lockStateUtility);
    }

    @Test
    public void testDeviceUnlockShouldPreventResponding() throws Exception {
        Mockito.when(this.settings.isPhoneUnlockedInterpretedAsNotRiding()).thenReturn(true);
        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(false);
        Assert.assertTrue(this.deviceUnlocked.isNotRidingBecausePhoneUnlocked() == false);

        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(true);
        Assert.assertTrue(this.deviceUnlocked.isNotRidingBecausePhoneUnlocked() == true);

        Mockito.when(this.settings.isPhoneUnlockedInterpretedAsNotRiding()).thenReturn(false);
        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(false);
        Assert.assertTrue(this.deviceUnlocked.isNotRidingBecausePhoneUnlocked() == false);


        Mockito.when(this.lockStateUtility.isPhoneUnlocked()).thenReturn(true);
        Assert.assertTrue(this.deviceUnlocked.isNotRidingBecausePhoneUnlocked() == false);

    }
}