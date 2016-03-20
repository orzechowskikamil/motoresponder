package com.medziku.motoresponder.logic;

import junit.framework.Assert;
import junit.framework.TestCase;


public class PhoneNumbersComparatorTest extends TestCase {

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
}