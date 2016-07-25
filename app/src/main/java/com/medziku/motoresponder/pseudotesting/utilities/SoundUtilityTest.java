package com.medziku.motoresponder.pseudotesting.utilities;

import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.SoundUtility;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SoundUtilityTest {

    private static final String TAG = "SoundUtilityTest";
    private SoundUtility soundUtility;
    private Context context;

    public SoundUtilityTest(Context context) {
        this.context = context;
    }

    public void testMeasurementAverageSound() {
        this.setUp();

        Future<Float> loudness = this.soundUtility.getLoudnessOfEnvironment();
        try {
            Log.d(TAG, "Loudness is = " + loudness.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void setUp() {
        this.soundUtility = new SoundUtility(this.context);
    }
}
