package com.medziku.motoresponder.logic;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SettingsTest {

    public static final String STORED_VALUE = "AAA";
    final Function<String, Boolean>[] sharedPreferenceChangedCallback = new Function[]{null};
    private SharedPreferencesUtility sharedPreferencesUtility;
    private Settings settings;

    @Before
    public void setUp() {
        this.sharedPreferencesUtility = mock(SharedPreferencesUtility.class);

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


    @Test
    public void testReadingHashMap() {
        int ID = 3;

        String jsonStr = "{\"789789789\":3000, \"123123123\":3500}";
        when(this.sharedPreferencesUtility.getStringValue(anyString(), anyString())).thenReturn(jsonStr);

        Map<String, Long> mapValue = this.settings.getMapValue(ID);
        assertTrue(mapValue.get("789789789") == 3000);
        assertTrue(mapValue.get("123123123") == 3500);
    }

    @Test
    public void testSavingHashMap() {
        int ID = 3;

        String jsonStr = "{\"789789789\":3000}";


        Map<String, Long> map = new HashMap<>();
        map.put("789789789", (long) 3000);
        this.settings.setMapValue(ID, map);

        verify(this.sharedPreferencesUtility, times(1)).setStringValue(anyString(), contains(jsonStr));


    }

}