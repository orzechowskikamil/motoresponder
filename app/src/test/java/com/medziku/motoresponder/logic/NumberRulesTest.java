package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;


public class NumberRulesTest {


    private NumberRules numberRules;
    private ContactsUtility contactsUtility;
    private String FAKE_INCOMING_PHONE_NUMBER = "777777777";
    private String FAKE_CURRENT_DEVICE_PHONE_NUMBER = "123456789";

    @Before
    public void setUp() throws Exception {
        this.contactsUtility = Mockito.mock(ContactsUtility.class);
        this.numberRules = new NumberRules(contactsUtility);

        Mockito.when(this.contactsUtility.readCurrentDevicePhoneNumber()).thenReturn(this.FAKE_CURRENT_DEVICE_PHONE_NUMBER);
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

    // region helper methods

    private void setContactBookContainsContactReturnValue(boolean returnValue) {
        Mockito.when(this.contactsUtility.contactBookContainsContact(Matchers.anyString())).thenReturn(returnValue);
    }

    private void expectNumberRulesAllowRespondingToBe(String phoneNumber, boolean expectedResult) {
        Assert.assertTrue(this.numberRules.numberRulesAllowResponding(phoneNumber) == expectedResult);
    }

    // endregion
}