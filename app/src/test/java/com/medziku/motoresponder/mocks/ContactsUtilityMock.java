package com.medziku.motoresponder.mocks;


import com.medziku.motoresponder.utils.ContactsUtility;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContactsUtilityMock {

    public ContactsUtility mock;
    private MockedContactsEntry[] contacts;

    public ContactsUtilityMock() throws Exception {
        this.mock = mock(ContactsUtility.class);

        this.setupMock();
    }

    public void setMockedContacts(MockedContactsEntry[] contacts) {
        this.contacts = contacts;
    }

    private void setupMock() throws Exception {
        when(this.mock.contactBookContainsNumber(anyString())).thenAnswer(new Answer() {
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                String phoneNumber = (String) invocation.getArguments()[0];

                for (MockedContactsEntry entry : ContactsUtilityMock.this.contacts) {
                    if (entry.phoneNumber.equals(phoneNumber)) {
                        return true;
                    }
                }
                return false;
            }
        });

        when(this.mock.hasGroupNumberByGroupName(anyString(), anyString())).thenAnswer(new Answer() {
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                String groupID = (String) invocation.getArguments()[0];
                String phoneNumber = (String) invocation.getArguments()[1];

                for (MockedContactsEntry entry : ContactsUtilityMock.this.contacts) {
                    if (entry.phoneNumber.equals(phoneNumber) && entry.groupID.equals(groupID)) {
                        return true;
                    }

                }
                return false;
            }
        });

        when(this.mock.isAbleToReadCurrentDeviceNumber()).thenReturn(false);
        when(this.mock.readCurrentMobileCountryCode()).thenReturn(260);
    }
}

class MockedContactsEntry {
    public String phoneNumber;
    public String groupID;
}
