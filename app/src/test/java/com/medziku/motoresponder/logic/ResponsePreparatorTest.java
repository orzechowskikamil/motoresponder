package com.medziku.motoresponder.logic;

import android.location.Location;
import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;


public class ResponsePreparatorTest {

    public static final String RESPONSE_TEXT = "AAA";
    public static final String RESPONSE_TEXT_LOCATION_KEYWORD = "LOCATIONRESPONSE";
    public static final String RESPONSE_TEXT_LOCATION = RESPONSE_TEXT_LOCATION_KEYWORD + " %location%";
    public static final String MAPS_URL = "maps.google.com/maps?q=";
    public static final double LATITUDE = 50.03030303;
    public static final double LONGITUDE = 30.303030;
    public static final String GEOLOCATION_REQUEST_INCOMING_MESSAGE = "Hey bert, where are you?";
    public static final String FAKE_PHONE_NUMBER = "777777777";
    public static final String INCOMING_MESSAGE = "AAAAAA";
    private Settings settings;
    private LocationUtility locationUtility;
    private ResponsePreparator responsePreparator;
    private ContactsUtility contactsUtility;

    @Before
    public void setUp() {
        this.settings = mock(Settings.class);
        this.locationUtility = mock(LocationUtility.class);
        this.contactsUtility = mock(ContactsUtility.class);

        when(this.settings.getAutoResponseToCallTemplate()).thenReturn(RESPONSE_TEXT);
        when(this.settings.getAutoResponseToSmsWithGeolocationTemplate()).thenReturn(RESPONSE_TEXT_LOCATION);
        when(this.settings.getAutoResponseToSmsTemplate()).thenReturn(RESPONSE_TEXT);
        when(this.settings.getGeolocationRequestPatterns()).thenReturn(new String[]{"Where are you"});
        when(this.settings.isRespondingWithGeolocationEnabled()).thenReturn(true);
        when(this.settings.isRespondingWithGeolocationAlwaysEnabled()).thenReturn(false);

        SettableFuture<Location> future = SettableFuture.create();
        Location location = mock(Location.class);
        when(location.getLatitude()).thenReturn(LATITUDE);
        when(location.getLongitude()).thenReturn(LONGITUDE);
        future.set(location);

        when(this.locationUtility.getLastRequestedLocation()).thenReturn(future);

        this.responsePreparator = new ResponsePreparator(this.settings, this.locationUtility, this.contactsUtility);
    }


    @Test
    public void testPrepareNormalResponse() throws Exception {
        String result = this.responsePreparator.prepareResponse(new SMSRespondingSubject(FAKE_PHONE_NUMBER, INCOMING_MESSAGE));
        assertTrue(result.equals(RESPONSE_TEXT));
    }

    @Test
    public void testPrepareGeolocationResponse() throws Exception {
        String result = this.responsePreparator.prepareResponse(new GeolocationRequestRespondingSubject(FAKE_PHONE_NUMBER, GEOLOCATION_REQUEST_INCOMING_MESSAGE));
        assertTrue(result.indexOf(RESPONSE_TEXT_LOCATION_KEYWORD) != -1);
        assertTrue(isGeolocationResponse(result));
        assertTrue(result.indexOf(Double.toString(LATITUDE)) != -1);
        assertTrue(result.indexOf(Double.toString(LONGITUDE)) != -1);
    }

    @Test
    public void testPrepareResponseForCall() {
        String result = this.responsePreparator.prepareResponse(new CallRespondingSubject(FAKE_PHONE_NUMBER));
        assertTrue(result.equals(RESPONSE_TEXT));
    }

    @Test
    public void testAlwaysGeolocation() {
        when(this.settings.isRespondingWithGeolocationAlwaysEnabled()).thenReturn(true);
        when(this.settings.isRespondingWithGeolocationEnabled()).thenReturn(false);

        String result = this.responsePreparator.prepareResponse(new GeolocationRequestRespondingSubject(FAKE_PHONE_NUMBER, GEOLOCATION_REQUEST_INCOMING_MESSAGE));

        assertFalse(isGeolocationResponse(result));

        when(this.settings.isRespondingWithGeolocationEnabled()).thenReturn(true);
        String enabledResult = this.responsePreparator.prepareResponse(new GeolocationRequestRespondingSubject(FAKE_PHONE_NUMBER, GEOLOCATION_REQUEST_INCOMING_MESSAGE));

        assertTrue(isGeolocationResponse(enabledResult));

    }

    @Test
    public void testDisabledGeoresponse() {
        when(this.settings.isRespondingWithGeolocationEnabled()).thenReturn(false);
        String result = this.responsePreparator.prepareResponse(new SMSRespondingSubject(FAKE_PHONE_NUMBER, GEOLOCATION_REQUEST_INCOMING_MESSAGE));
        assertTrue(result.equals(RESPONSE_TEXT));
    }

    @Test
    public void testNumberInGeolocationGroup() throws Exception {
        boolean isNumberInGroup = true;
        boolean shouldRespondWithGeolocation = true;

        this.testGeolocationGroup(isNumberInGroup, shouldRespondWithGeolocation);
    }


    @Test
    public void testNumberOutsideGeolocationGroup() throws Exception {
        boolean isNumberInGroup = false;
        boolean shouldRespondWithGeolocation = false;

        this.testGeolocationGroup(isNumberInGroup, shouldRespondWithGeolocation);
    }

    private void testGeolocationGroup(boolean hasGroupNumber, boolean expectedResult) throws Exception {
        when(this.settings.getGeolocationWhitelistGroupName()).thenReturn("doesnt matter");
        when(this.contactsUtility.hasGroupNumberByGroupName(anyString(), anyString())).thenReturn(hasGroupNumber);

        String result = this.responsePreparator.prepareResponse(
                new GeolocationRequestRespondingSubject(FAKE_PHONE_NUMBER, GEOLOCATION_REQUEST_INCOMING_MESSAGE)
        );

        assertTrue(isGeolocationResponse(result) == expectedResult);
    }

    private boolean isGeolocationResponse(String responseText) {
        return responseText.indexOf(MAPS_URL) != -1;
    }
}
