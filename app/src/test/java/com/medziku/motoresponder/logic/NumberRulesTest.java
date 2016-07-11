package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


public class NumberRulesTest {


    private NumberRules numberRules;
    private ContactsUtility contactsUtility;
    private String FAKE_INCOMING_PHONE_NUMBER = "777777777";
    private String FAKE_CURRENT_DEVICE_PHONE_NUMBER = "123456789";
    private String FAKE_FOREIGN_INCOMING_NUMBER = "+44-664-663-662";
    private String FAKE_LOCAL_INCOMING_NUMBER = "+48-664-663-662";
    private Settings settings;
    private CountryPrefix countryPrefix;

    @Before
    public void setUp() throws Exception {
        this.contactsUtility = Mockito.mock(ContactsUtility.class);
        this.settings = Mockito.mock(Settings.class);
        this.countryPrefix = Mockito.mock(CountryPrefix.class);
        CustomLog log = Mockito.mock(CustomLog.class);
        this.numberRules = new NumberRules(contactsUtility, countryPrefix, settings, log);

        when(this.settings.isRespondingRestrictedToContactList()).thenReturn(true);
        this.setContactBookContainsContactReturnValue(true);

        when(this.countryPrefix.getCountryPrefix()).thenReturn("48");
        when(this.contactsUtility.readCurrentDevicePhoneNumber()).thenReturn(this.FAKE_CURRENT_DEVICE_PHONE_NUMBER);

    }

    @Test
    public void testNumberRulesAllowResponding() throws Exception {
        this.setContactBookContainsContactReturnValue(true);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_INCOMING_PHONE_NUMBER, true);
    }

    @Test
    public void numberRulesPreventRespondingIfNotInContactBook() {
        this.setContactBookContainsContactReturnValue(false);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_INCOMING_PHONE_NUMBER, false);
    }

    @Test
    public void numberRulesNotPreventRespondingIfNotInContactBook() {
        this.setContactBookContainsContactReturnValue(false);
        when(this.settings.isRespondingRestrictedToContactList()).thenReturn(false);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_INCOMING_PHONE_NUMBER, true);
    }

    @Test
    public void numberRulesPreventRespondingToOurOwnNumber() {
        this.setContactBookContainsContactReturnValue(true);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_CURRENT_DEVICE_PHONE_NUMBER, false);
    }


    @Test
    public void numberRulesPreventRespondingForeignNumber() {
        when(this.settings.isRespondingRestrictedToCurrentCountry()).thenReturn(true);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_INCOMING_PHONE_NUMBER, true);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_LOCAL_INCOMING_NUMBER, true);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_FOREIGN_INCOMING_NUMBER, false);

        when(this.settings.isRespondingRestrictedToCurrentCountry()).thenReturn(false);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_INCOMING_PHONE_NUMBER, true);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_FOREIGN_INCOMING_NUMBER, true);
    }

    @Test
    public void numberRulesWorkCorrectlyWithWeirdDigitsPrefixCountry() {
        when(this.settings.isRespondingRestrictedToCurrentCountry()).thenReturn(true);

        when(this.countryPrefix.getCountryPrefix()).thenReturn("1"); // US
        this.expectNumberRulesAllowRespondingToBe("+1-800-892-5234", true); // microsoft number
        this.expectNumberRulesAllowRespondingToBe("800-892-5234", true); // microsoft number
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_LOCAL_INCOMING_NUMBER, false); // Polish number

        when(this.countryPrefix.getCountryPrefix()).thenReturn("974");   // Qatar
        this.expectNumberRulesAllowRespondingToBe("+974 4 411 9420", true); // Qatar microsoft support number
        this.expectNumberRulesAllowRespondingToBe("4 411 9420", true); // Qatar microsoft support number
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_LOCAL_INCOMING_NUMBER, false); // Polish number

    }

    @Test
    public void numberRulesDoesntPreventRespondingWhenCountryIsUnknown() {
        when(this.settings.isRespondingRestrictedToCurrentCountry()).thenReturn(true);
        when(this.countryPrefix.getCountryPrefix()).thenReturn(null);

        this.expectNumberRulesAllowRespondingToBe("+974 4 411 9420", true); // Qatar microsoft support number
        this.expectNumberRulesAllowRespondingToBe("4 411 9420", true); // Qatar microsoft support number
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_LOCAL_INCOMING_NUMBER, true); // Polish number
    }

    @Test
    public void whiteListCorrectlyFilterNumber() throws Exception {
        when(this.settings.getWhiteListGroupName()).thenReturn("doesn't matter");

        when(this.contactsUtility.hasGroupNumberByGroupName(anyString(), anyString())).thenReturn(false);
        assertFalse(this.numberRules.numberRulesAllowResponding(FAKE_INCOMING_PHONE_NUMBER));

        when(this.contactsUtility.hasGroupNumberByGroupName(anyString(), anyString())).thenReturn(true);
        assertTrue(this.numberRules.numberRulesAllowResponding(FAKE_INCOMING_PHONE_NUMBER));

    }


    @Test
    public void blackListCorrectlyFilterNumber() throws Exception {
        when(this.settings.getBlackListGroupName()).thenReturn("doesn't matter");

        when(this.contactsUtility.hasGroupNumberByGroupName(anyString(), anyString())).thenReturn(false);
        assertTrue(this.numberRules.numberRulesAllowResponding(FAKE_INCOMING_PHONE_NUMBER));

        when(this.contactsUtility.hasGroupNumberByGroupName(anyString(), anyString())).thenReturn(true);
        assertFalse(this.numberRules.numberRulesAllowResponding(FAKE_INCOMING_PHONE_NUMBER));
    }

    @Test
    public void testNumberRulesReadPhoneNumberFromSettingsIfNotAvailable() {
        String FAKE_STORED_PHONE_NUMBER = "111222333";
        String ANOTHER_PHONE_NUMBER = "173849404";

        when(this.settings.getStoredDevicePhoneNumber()).thenReturn(FAKE_STORED_PHONE_NUMBER);
        when(this.contactsUtility.readCurrentDevicePhoneNumber()).thenThrow(new UnsupportedOperationException());
        this.setContactBookContainsContactReturnValue(true);

        // this number should be recognized as stored phone number and do not allow responding
        assertFalse(this.numberRules.numberRulesAllowResponding(FAKE_STORED_PHONE_NUMBER));

        // this number should be recognized as another number and allow responding
        assertTrue(this.numberRules.numberRulesAllowResponding(ANOTHER_PHONE_NUMBER));
    }

    // region helper methods

    private void setContactBookContainsContactReturnValue(boolean returnValue) {
        when(this.contactsUtility.contactBookContainsNumber(Matchers.anyString())).thenReturn(returnValue);
    }

    private void expectNumberRulesAllowRespondingToBe(String phoneNumber, boolean expectedResult) {
        assertTrue(this.numberRules.numberRulesAllowResponding(phoneNumber) == expectedResult);
    }

    // endregion
}