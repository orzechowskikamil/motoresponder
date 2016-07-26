package com.medziku.motoresponder.mocks;

import android.location.Location;
import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.MotionUtility;

import java.util.concurrent.Future;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MotionUtilityMock {

    public MotionUtility mock;

    public void setIsDeviceInMotionResult(boolean result) {
        SettableFuture<Boolean> value = SettableFuture.create();
        value.set(result);
        when(this.mock.isDeviceInMotion(anyDouble())).thenReturn(value);
    }

    public MotionUtilityMock() {
        this.mock = mock(MotionUtility.class);
    }
}
