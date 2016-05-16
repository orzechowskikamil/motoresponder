package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class NumberRulesTest {


    private NumberRules numberRules;
    private ContactsUtility contactsUtility;
    private String FAKE_INCOMING_PHONE_NUMBER = "777777777";
    private String FAKE_CURRENT_DEVICE_PHONE_NUMBER = "123456789";
    private Settings settings;

    @Before
    public void setUp() throws Exception {
        this.contactsUtility = Mockito.mock(ContactsUtility.class);
        this.settings = Mockito.mock(Settings.class);
        this.numberRules = new NumberRules(contactsUtility, settings);


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
    public void numberRulesPreventRespondingToOurOwnNumber() {
        this.setContactBookContainsContactReturnValue(true);
        this.expectNumberRulesAllowRespondingToBe(this.FAKE_CURRENT_DEVICE_PHONE_NUMBER, false);
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
        when(this.contactsUtility.contactBookContainsContact(Matchers.anyString())).thenReturn(returnValue);
    }

    private void expectNumberRulesAllowRespondingToBe(String phoneNumber, boolean expectedResult) {
        assertTrue(this.numberRules.numberRulesAllowResponding(phoneNumber) == expectedResult);
    }

    // endregion
}