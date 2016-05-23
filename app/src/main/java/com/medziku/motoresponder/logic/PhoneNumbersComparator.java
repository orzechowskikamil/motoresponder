package com.medziku.motoresponder.logic;

/**
 * Compares phone numbers in intelligent way, as their notations can differ.
 */
public class PhoneNumbersComparator {

    /**
     * We assume that 9 digits is a root for normal number
     */
    private static int MINIMAL_LENGTH_OF_NORMAL_NUMBER = 9;

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
        if (isNumberNormal(shorterNumber)) {
            cutLongerNumber = longerNumber.substring(longerNumber.length() - shorterNumber.length());
        } else {
            cutLongerNumber = longerNumber;
        }

        boolean result = cutLongerNumber.equals(shorterNumber);
        return result;
    }

    /**
     * it will:
     * cut all chars which aren't number or +
     * so number like +48-663-664-665 will become +48663664665
     * change 00 to +
     * so number like 00 48 663 664 665 will become +48663664665
     * cut first zero
     * so number like 0663664665 will become 663664665
     *
     * @param phoneNumber
     * @return
     */
    public static String normalizeNumber(String phoneNumber) {
        phoneNumber = phoneNumber.trim().replaceAll("[^0-9\\+]", "");

        if (phoneNumber.substring(0, 2).equals("00")) {
            phoneNumber = "+" + phoneNumber.substring(2);
        } else if (phoneNumber.substring(0, 1).equals("0")) {
            phoneNumber = phoneNumber.substring(1);
        }

        return phoneNumber;
    }

    /**
     * This will check if number is "normal"
     * "Normal" means normal abonent number, no premium SMS or something.
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isNumberNormal(String phoneNumber) {
        String normalizedPhoneNumber = normalizeNumber(phoneNumber);
        return normalizedPhoneNumber.length() >= MINIMAL_LENGTH_OF_NORMAL_NUMBER;
    }

    public static boolean isNumberForeign(String phoneNumber, String currentCountryCode) {
        String normalizedNumber = normalizeNumber(phoneNumber);

        boolean isGlobalNumber = normalizedNumber.indexOf("+") >= 0;

        // number which is not in global format can't be number from outside country
        if (!isGlobalNumber) {
            return false;
        }

        // normalized number from current country should have currentCountryCode on first digit.
        if (normalizedNumber.indexOf(currentCountryCode) == 1) {
            return false;
        }
        // then it is from outside country
        return true;
    }
}
