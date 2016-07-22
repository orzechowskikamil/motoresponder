package com.medziku.motoresponder.logic;

import android.content.Context;
import android.test.mock.MockContext;
import com.google.common.base.Predicate;

import com.medziku.motoresponder.utils.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class ResponderIntegrationTest {

    private MockedUtilitiesResponder responder;
    private MockContext context;

    @Before
    public void setUp() throws Exception {
        this.context = new MockContext();
        this.responder = new MockedUtilitiesResponder(this.context);
      
    }

    @Test
    public void testReactionOnCall() {
        this.responder.startResponding();
        this.responder.currentCallCallback.apply(this.FAKE_PHONE_NUMBER);

        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(any(CallRespondingSubject.class));
    }


}


class MockedUtilitiesResponder extends Responder {

    public MockedUtilitiesResponder(Context context) {
        super(context);
    }
    
       @Override
    protected Settings createSettings() {
        this.mockSettings = mock(Settings.class);
        return this.mockSettings;
    }
    
    

    @Override
    protected void createUtilities() {
        try {
             this.sharedPreferencesUtility = mock(SharedPreferencesUtility.class);
             this.mockSharedPreferencesUtility = this.sharedPreferencesUtility;
             
             
                 this.lockStateUtility = mock(LockStateUtility.class);
             this.mocklockStateUtility = this.lockStateUtility;
             
                 this.smsUtility = mock(SMSUtility.class);
             this.mockSmsUtility= this.smsUtility;
             

                   this.callsUtility = mock(CallsUtility.class);
             this.mockCallsUtility = this.callsUtility;
             
                      this.notificationUtility = mock(NotificationUtility.class);
             this.mockNotificationUtility = this.notificationUtility;
             
                      this.locationUtility = mock(LocationUtility.class);
             this.mockLocationUtility = this.locationUtility;
             
                      this.contactsUtility = mock(ContactsUtility.class);
             this.mockContactsUtility = this.contactsUtility;


                 this.motionUtility = mock(MotionUtility.class);
             this.mockMotionUtility = this.motionUtility;


                 this.sensorsUtility = mock(SensorsUtility.class);
             this.mockSensorsUtility = this.sensorsUtility;


                 this.wiFiUtility = mock(WiFiUtility.class);
             this.mockWiFiUtility = this.wiFiUtility;

            } catch (Exception e) {
            e.printStackTrace();
        }
    }

class Emulation{

  Predicate<String> unansweredCallCallback;
  MockedCallSMSLogEntry[] mockedCallSMSLog;

  public Emulation(MockedUtilitiesResponder responder){
  this.responder=responder;
  }
  
  public void emulateUnansweredCall(String phoneNumber){ if (this.unansweredCallCallback !=null){this.unansweredCallCallback.apply(phoneNumber);});

  public void emulateSMSCallLog(MockedCallSMSLogEntry[] entries){
    this.mockedCallSMSLog = entries;
  }
  
  public void emulateContacts(MockedContactEntry[] entries){
  this.contacts=entries;
  }
  
  public void startEmulation(){
  
    when(this.responder.callsUtility).listenForUnansweredCalls(any(Predicate.class)).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
               this.unansweredCallCallback = (Predicate<String>) invocation.getArguments()[0];
                return null;
            }
        });
        
          when(this.responder.callsUtility).stopListeningForCalls().thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
               this.unansweredCallCallback = null;
                return null;
            }
        });
        
          when(this.responder.callsUtility).wasOutgoingCallAfterDate(any(Date.class), anyString()).thenAnswer(new Answer() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
               Date date= (Date) invocation.getArguments()[0];
                  String phoneNumber= (String) invocation.getArguments()[1];
                  
                  for (MockedCallSMSLogEntry entry: this.mockedCallSMSLog){
                      if (entry.isCall==true && entry.date.getTime()> date.getTime() && entry.isOutgoing==true && entry.phoneNumber.equals(phoneNumber)){
                      return true;
                      }
                  }
                  
                  
                return false;
            }
        });
        
        
          when(this.responder.contactsUtility).contactBookContainsNumber(anyString()).thenAnswer(new Answer() {
          
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
               String phoneNumber= (Date) invocation.getArguments()[0];
                   for (MockedContactEntry entry: this.contacts){
                   if (entry.phoneNumber.equals(phoneNumber)){ return true;
                   }
                    return false;
                  });
        
        
        

  }
}
}

class MockedCallSMSLogEntry {
  public Date date;
  public String phoneNumber;
  public boolean isOutgoing;
  public boolean isCall;
}

class MockedContactEntry{
public String phoneNumber;public String name;
}

