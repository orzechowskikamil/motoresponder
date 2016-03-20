package com.medziku.motoresponder.pseudotesting.integration;

import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.Responder;
import com.medziku.motoresponder.logic.RespondingDecision;

/**
 * This class allow test Responder and it"s components integration, under controlled conditions - RespondingDecision is mocked,
 * because it"s impossible to control it in home (because it require GPS data, accelerometer, etc) - so test is divided into two
 * cases - one where RespondingDecision is permamently mocked to true, in another to false.
 * <p/>
 * It allow to check how Responding and it"s internal behaves in real environment in all possible casses, under control of the debugger
 */
//TODO extract it to separate file.
public class ResponderIntegrationTest {
    public static final String TAG = "RespondingProcessTest";
    private Context context;
    private ExposedResponder responder;

    public ResponderIntegrationTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.responder = new ExposedResponder(this.context);
    }

    public void testRespondingProcessWithMockedTrueDecision() {
        this.setUp();
        this.responder.mockedRespondingDecision.result = true;
        Log.d(TAG, "Responder right now will start responding process, assuming always respondingDecision as == true");
        this.responder.startResponding();
    }

    public void testRespondingProcessWithMockedFalseDecision() {
        this.setUp();
        Log.d(TAG, "Responder right now will start responding process, assuming always respondingDecision as == false");
        this.responder.mockedRespondingDecision.result = false;
        this.responder.startResponding();
    }

}

class ExposedResponder extends Responder {
    public MockedRespondingDecision mockedRespondingDecision;

    public ExposedResponder(Context context) {
        super(context);
    }

    protected RespondingDecision createRespondingDecision() {
        this.mockedRespondingDecision = new MockedRespondingDecision();
        return this.mockedRespondingDecision;
    }
}

class MockedRespondingDecision extends RespondingDecision {
    public boolean result;

    public MockedRespondingDecision() {
        super(null, null, null, null);
    }

    public boolean shouldRespond(String phoneNumber) {
        return this.result;
    }
}
