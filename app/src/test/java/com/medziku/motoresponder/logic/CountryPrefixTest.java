package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CountryPrefixTest {
    private ContactsUtility contactsUtility;
    private CountryPrefix countryPrefix;

    @Before
    public void setUp() {
        this.contactsUtility = Mockito.mock(ContactsUtility.class);
        this.countryPrefix = new CountryPrefix(this.contactsUtility);
    }

    @Test
    public void testGetCountryPrefix() throws Exception {
        when(this.contactsUtility.readCurrentMobileCountryCode()).thenReturn(new Integer(260));
        assertTrue(this.countryPrefix.getCountryPrefix().equals("48"));

        when(this.contactsUtility.readCurrentMobileCountryCode()).thenReturn(null);
        assertTrue(this.countryPrefix.getCountryPrefix() == null);

        when(this.contactsUtility.readCurrentMobileCountryCode()).thenReturn(311);
        assertTrue(this.countryPrefix.getCountryPrefix().equals("1"));

        when(this.contactsUtility.readCurrentMobileCountryCode()).thenReturn(310);
        assertTrue(this.countryPrefix.getCountryPrefix().equals("1"));

        when(this.contactsUtility.readCurrentMobileCountryCode()).thenReturn(9999);
        assertTrue(this.countryPrefix.getCountryPrefix() == null);
    }
}