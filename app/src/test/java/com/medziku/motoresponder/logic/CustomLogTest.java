package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CustomLogTest extends TestCase {

    private CustomLog customLog;
    private Settings settings;

    @Before
    public void setUp() {
        this.settings = new MockedSettings(Mockito.mock(SharedPreferencesUtility.class));

        this.customLog = new CustomLog(this.settings);
    }

    @Test
    public void testAddingSingleLine() {
        String DUMMY_LINE = "hellou!";
        this.customLog.add(DUMMY_LINE);

        String logStr = this.customLog.getLogStr();

        assertTrue(logStr.indexOf(DUMMY_LINE) != -1);
    }

    @Test
    public void testAddingManyLines() {
        int EXPECTED_MAX_LENGTH_OF_LOG = 5500;
        int EXPECTED_MIN_LENGTH_OF_LOG = 1000;
        String DUMMY_LINE = "hellou!";

        for (int i = 0; i < 1000; i++) {
            this.customLog.add(DUMMY_LINE);
        }

        String logStr = this.customLog.getLogStr();

        // expect to not crash and not be unlimited.
        int logLength = logStr.length();

        // must be between those if correctly trimmed to the limit and correctly gathered all lines.
        assertTrue(logLength < EXPECTED_MAX_LENGTH_OF_LOG);
        assertTrue(logLength > EXPECTED_MIN_LENGTH_OF_LOG);
    }

}


/**
 * Its simplified implementation of settings which has only setStringValue/getStringValue really working.
 * It store and return last value.
 */
class MockedSettings extends Settings {


    protected String lastValue;

    public MockedSettings(SharedPreferencesUtility sharedPreferencesUtility) {
        super(sharedPreferencesUtility);
    }


    @Override
    protected void setStringValue(int resID, String value) {
        this.lastValue = value;
    }

    @Override
    protected String getStringValue(int resID) {
        return this.lastValue;
    }
}