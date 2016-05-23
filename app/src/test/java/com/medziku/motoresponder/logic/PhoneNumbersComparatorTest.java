package com.medziku.motoresponder.logic;

import junit.framework.Assert;
import org.junit.Test;


public class PhoneNumbersComparatorTest {
    @Test
    public void testAreNumbersEqual() throws Exception {

        String[][] testcasesPositive = {
                {"791467855", "791467855"},
                {"791467855", "48791467855"},
                {"791467855", "+48791467855"},
                {"791467855", "048791467855"},
                {"791467855", "0048791467855"},
                {"791467855", "791467855"},
                {"791467855", "48 791 467 855"},
                {"791467855", "+48 791 467 855"},
                {"791467855", "0 48 791 467 855"},
                {"791467855", "00 48 791 467 855"},
                {"791467855", "+48 791 46 78 55"},
                {"+48 791 46 78 55", "+48 791 46 78 55"},
                {"7855", "7855"},
                {"791467855", "(79)146-78-55"},
                {"+48791467855", "(79)146-78-55"}


        };

        String[][] testcasesNegative = {
                {"791467855", "848382948"},
                {"48791467855", "33791467855"},
                {"791467855", "7855"}

        };

        for (String[] testcasePositive : testcasesPositive) {
            Assert.assertTrue(PhoneNumbersComparator.areNumbersEqual(testcasePositive[0], testcasePositive[1]));
        }

        for (String[] testcaseNegative : testcasesNegative) {
            Assert.assertFalse(PhoneNumbersComparator.areNumbersEqual(testcaseNegative[0], testcaseNegative[1]));
        }

    }

    @Test
    public void testIsNumberNormal() {
        String[] testcasesPositive = {
                "777888999",
                "0048777888999",
                "+48777888999"
        };
        String[] testcasesNegative = {
                "7122",
                "71727"
        };


        for (String phoneNumber : testcasesPositive) {
            Assert.assertTrue(PhoneNumbersComparator.isNumberNormal(phoneNumber));
        }

        for (String phoneNumber : testcasesNegative) {
            Assert.assertFalse(PhoneNumbersComparator.isNumberNormal(phoneNumber));
        }
    }

    @Test
    public void testIsNumberForeign() {
        String COUNTRY_CODE = "48";

        String[] testcasesPositive = {
                "+44-667-668-669",
                "+44-667-668-669-70",
                "+356-2034-1505"

        };

        String[] testcasesNegative = {
                "+48667668669",
                "667668669"
        };

        for (String phoneNumber : testcasesPositive) {
            Assert.assertTrue(PhoneNumbersComparator.isNumberForeign(phoneNumber, COUNTRY_CODE));
        }

        for (String phoneNumber : testcasesNegative) {
            Assert.assertFalse(PhoneNumbersComparator.isNumberForeign(phoneNumber, COUNTRY_CODE));
        }
    }


}