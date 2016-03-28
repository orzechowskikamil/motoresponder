package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class SettingsTest {

    public static final String STORED_VALUE = "AAA";
    private SharedPreferencesUtility sharedPreferencesUtility;

    @Before
    public void setUp() {
        this.sharedPreferencesUtility = mock(SharedPreferencesUtility.class);
        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn(STORED_VALUE);
    }

}