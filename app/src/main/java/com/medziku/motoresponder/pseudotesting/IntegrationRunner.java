package com.medziku.motoresponder.pseudotesting;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.medziku.motoresponder.pseudotesting.integration.ResponderIntegrationTest;
import com.medziku.motoresponder.pseudotesting.integration.RespondingDecisionIntegrationTest;

/**
 * This class allow to test integration of components of application in semi-isolation.
 * Because this app is hard to test as whole piece (because it requires a debugger, and it requires movement, gps, accelerometer data
 * and incoming messages) it"s split to part which is dependent on incoming message (Responder) which can be simulated by sending
 * SMS from other phone, and part which is dependent on GPS/Accelerometer/Phone status data (RespondingDecision) which can be simulated
 * by walking with phone, while it doesn"t need to get incoming message in isolated test.
 */
// TODO K. Orzechowski Rewrite pseudo integration test to something acceptable #Issue #78
public class IntegrationRunner {
    /**
     * This flag enables integration testing - it will be run instead of application.
     * Remember that UtilitiesRunner.ARE_PSEUDO_TESTS_ENABLED should be set to false
     */
    public static final boolean ARE_INTEGRATION_TESTS_ENABLED = true;
    public final static String TAG = "IntegrationRunner";

    private ResponderIntegrationTest responderIntegrationTest;
    private RespondingDecisionIntegrationTest respondingDecisionIntegrationTest;

    public IntegrationRunner(Context context) {

        this.responderIntegrationTest = new ResponderIntegrationTest(context);
        this.respondingDecisionIntegrationTest = new RespondingDecisionIntegrationTest(context);
    }

    public void run() {
        (new AsyncTask<Boolean, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... params) {
                // unfortunately I can"t show those errors in any different way  that throwing exception
                // and catching it here. Hope it will be enough to test.
                Log.d(TAG, "IntegrationRunner... starting...");
                IntegrationRunner.this.runTest();
                Log.d(TAG, "IntegrationRunner... finished...");

                return null;
            }
        }).execute();
    }

    private void runTest() {
        switch (10) {
            case 10:
                // this is working at 10.04.2016 on Android 5.1 Motog
                this.responderIntegrationTest.testRespondingProcessWithMockedTrueDecision();
                break;
            case 11:
                // this is working at 18.03.2016 on Android 5.1 Motog
                this.responderIntegrationTest.testRespondingProcessWithMockedFalseDecision();
                break;

            case 20:
                // this is working at 02.04.2016 on Android 5.1 motog
                this.respondingDecisionIntegrationTest.testRespondingDecisionInIsolation();
                break;
            case 21:
                // this is working at 20.03.2016 on Android 5.1 motog
                this.respondingDecisionIntegrationTest.testRespondingDecisionInIsolationContinously();
                break;
        }
    }
}


