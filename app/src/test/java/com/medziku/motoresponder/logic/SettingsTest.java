package com.medziku.motoresponder.logic;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class SettingsTest {

    public static final String STORED_VALUE = "AAA";
    final Function<String, Boolean>[] sharedPreferenceChangedCallback = new Function[]{null};
    private SharedPreferencesUtility sharedPreferencesUtility;
    private Settings settings;


    @Before
    public void setUp() {
        this.sharedPreferencesUtility = mock(SharedPreferencesUtility.class);

        this.settings = new Settings(this.sharedPreferencesUtility);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                sharedPreferenceChangedCallback[0] = (Function<String, Boolean>) invocation.getArguments()[0];
                return null;
            }
        }).when(this.sharedPreferencesUtility).listenToSharedPreferenceChanged(any(Function.class));


        this.settings = new Settings(this.sharedPreferencesUtility);
        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn(STORED_VALUE);
    }

    @Test
    public void testOfWhitelistName() {
        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn("dd");
        assertTrue(this.settings.getWhiteListGroupName().equals("dd"));

        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn(null);
        assertTrue(this.settings.getWhiteListGroupName() == null);

        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn("");
        assertTrue(this.settings.getWhiteListGroupName() == null);
    }


    @Test
    public void testOfBlacklistName() {
        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn("dd");
        assertTrue(this.settings.getBlackListGroupName().equals("dd"));

        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn(null);
        assertTrue(this.settings.getBlackListGroupName() == null);

        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn("");
        assertTrue(this.settings.getBlackListGroupName() == null);
    }

    @Test
    public void testOfSavingStringArray() {
        int RES_ID = 1;
        String KEY_VALUE = "key";
        when(this.sharedPreferencesUtility.getStringFromRes(RES_ID)).thenReturn(KEY_VALUE);

        List<String> TEST_ARRAY = new ArrayList<>();
        TEST_ARRAY.add("aaa");
        TEST_ARRAY.add("bbb");
        this.settings.setStringArrayValue(RES_ID, TEST_ARRAY);
        verify(this.sharedPreferencesUtility, times(1)).setStringValue(eq(KEY_VALUE), contains("[\"aaa\",\"bbb\"]"));


    }


    @Test
    public void testOfErasingStringArray() {
        int RES_ID = 1;
        String KEY_VALUE = "key";

        when(this.sharedPreferencesUtility.getStringFromRes(RES_ID)).thenReturn(KEY_VALUE);

        this.settings.setStringArrayValue(RES_ID, null);
        verify(this.sharedPreferencesUtility, times(1)).setStringValue(eq(KEY_VALUE), contains(""));
    }

    @Test
    public void testOfReadingStringArray() {
        int RES_ID = 1;

        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn("[\"aaa\",\"bbb\"]");
        List<String> stringArrayValue = this.settings.getStringArrayValue(RES_ID);

        assertTrue(stringArrayValue.size() == 2);
        assertTrue(stringArrayValue.get(0).equals("aaa"));
        assertTrue(stringArrayValue.get(1).equals("bbb"));

        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn("");
        assertTrue(this.settings.getStringArrayValue(RES_ID) == null);
    }
    @Test
    public void testListeningToSettings() {
        final boolean[] changed = {false};

        this.settings.listenToSettingChange(this.settings.RESPONDER_ENABLED_KEY, new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                changed[0] = true;
                return false;
            }
        });

        sharedPreferenceChangedCallback[0].apply(this.settings.RESPONDER_ENABLED_KEY);

        assertTrue(changed[0]);

    }

}