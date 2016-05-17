package com.medziku.motoresponder.logic;

/**
 * Compares phone numbers in intelligent way, as their notations can differ.
 */
public class PhoneNumbersComparator {

    /**
     * We assume that 9 digits is a root for normal number
     */
    private static int minimalLenghtOfNormalNumber = 9;

    public static boolean areNumbersEqual(String firstPhoneNumber, String secondPhoneNumber) {
        String normalized1stNumber = normalizeNumber(firstPhoneNumber);
        String normalized2ndNumber = normalizeNumber(secondPhoneNumber);

        String longerNumber;
        String shorterNumber;

        if (normalized1stNumber.length() > normalized2ndNumber.length()) {
            longerNumber = normalized1stNumber;
            shorterNumber = normalized2ndNumber;

        } else {
            longerNumber = normalized2ndNumber;
            shorterNumber = normalized1stNumber;
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

    public static String normalizeNumber(String phoneNumber) {
        phoneNumber = phoneNumber.trim().replaceAll("[^0-9\\+]", "");

        if (phoneNumber.substring(0, 2).equals("00")) {
            phoneNumber = "+" + phoneNumber.substring(2);
        } else if (phoneNumber.substring(0, 1).equals("0")) {
            phoneNumber = phoneNumber.substring(1);
        }

        return phoneNumber;
    }
}
