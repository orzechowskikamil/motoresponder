package com.medziku.motoresponder.logic;

import java.util.regex.Pattern;

/**
 * Compares phone numbers in intelligent way, as their notations can differ.
 */
public class PhoneNumbersComparator {

    /**
     * We assume that 9 digits is a root for normal number
     */
    private static int minimalLenghtOfNormalNumber = 9;

    public static boolean areNumbersEqual(String firstPhoneNumber, String secondPhoneNumber) {
        String trimmed1stNumber = firstPhoneNumber.trim().replaceAll("[^0-9]", "");
        String trimmed2ndNumber = secondPhoneNumber.trim().replaceAll("[^0-9]", "");

        String longerNumber;
        String shorterNumber;

        if (trimmed1stNumber.length() > trimmed2ndNumber.length()) {
            longerNumber = trimmed1stNumber;
            shorterNumber = trimmed2ndNumber;

        } else {
            longerNumber = trimmed2ndNumber;
            shorterNumber = trimmed1stNumber;
        }

        String cutLongerNumber;
        if (shorterNumber.length() >= minimalLenghtOfNormalNumber) {
            cutLongerNumber = longerNumber.substring(longerNumber.length() - shorterNumber.length());
        } else {
            cutLongerNumber = longerNumber;
        }

        boolean result = cutLongerNumber.equals(shorterNumber);
        return result;
    }
}
