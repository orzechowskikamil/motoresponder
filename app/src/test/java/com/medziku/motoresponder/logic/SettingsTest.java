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
    private Settings settings;

    @Before
    public void setUp() {
        this.sharedPreferencesUtility = mock(SharedPreferencesUtility.class);
        this.settings = new Settings(this.sharedPreferencesUtility);
        when(this.sharedPreferencesUtility.getStringValue(anyString())).thenReturn(STORED_VALUE);
    }

    @Test
    public void testTypicalSettingGetSetOperation() {
        String result = this.settings.getAutoResponseToCallTemplate();
        assertEquals(result, STORED_VALUE);

        this.settings.setAutoResponseToCallTemplate("New value");
        verify(this.sharedPreferencesUtility, times(1)).setStringValue(anyString(), anyString());
    }
}