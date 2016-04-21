package com.medziku.motoresponder.logic;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class GeolocationRequestRecognitionTest extends TestCase {

    private GeolocationRequestRecognition geolocationRequestRecognition;

    @Before
    public void setUp() {
        Settings settings = Mockito.mock(Settings.class);
        when(settings.getGeolocationRequestPatterns()).thenReturn(new String[]{"where are you", "your location"});
        this.geolocationRequestRecognition = new GeolocationRequestRecognition(settings);
    }

    @Test
    public void testSimpleGeolocationRequest() throws Exception {
        boolean result = this.geolocationRequestRecognition.isGeolocationRequest("hey dear developer, where are you?");
        assertTrue(result);
    }

    @Test
    public void testNegativeGeolocationRequest() throws Exception {
        boolean result = this.geolocationRequestRecognition.isGeolocationRequest("hey developer, give me your salary?");
        assertFalse(result);
    }


    @Test
    public void testUpperCaseGeolocationRequest() throws Exception {
        boolean result = this.geolocationRequestRecognition.isGeolocationRequest("hey dear, Where are you?");
        assertTrue(result);
    }

}