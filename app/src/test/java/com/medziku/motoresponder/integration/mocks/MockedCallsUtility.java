class MockedCallsUtility {

public CallsUtility mock;

public Predicate<String> unansweredCallCallback;

private mockedCallLog;

    public MockedCallsUtility(){
      this.mock = mock(CallsUtility.class);
      this.setUp();
 
    }
    
    public void setMockedCallLog(mockedCallLog){this.mockedCallLog = mockedCallLog;}
    
    
    
    
    
    private void setUp(){
                doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    MockedCallsUtility.this.unansweredCallCallback = (Predicate<String>) invocation.getArguments()[0];
                    return null;
                }
            }).when(this.mock).listenForUnansweredCalls(any(Predicate.class));

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    MockedCallsUtility.this.unansweredCallCallback = null;
                    return null;
                }
            }).when(this.mock).stopListeningForCalls();

            doAnswer(new Answer() {
                @Override
                public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    Date date = (Date) invocation.getArguments()[0];
                    String phoneNumber = (String) invocation.getArguments()[1];

                    for (MockedCallSMSLogEntry entry : MockedCallsUtility.this.mockedCallLog) {
                        if (entry.isCall == true && entry.date.getTime() > date.getTime() && entry.isOutgoing == true && entry.phoneNumber.equals(phoneNumber)) {
                            return true;
                        }
                    }


                    return false;
                }
            }).when(this.mock).wasOutgoingCallAfterDate(any(Date.class), anyString());
    }

}
