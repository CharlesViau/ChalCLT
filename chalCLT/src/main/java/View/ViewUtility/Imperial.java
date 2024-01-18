package View.ViewUtility;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Imperial {

    //Algorithme d'Euclide pour trouver le plus grand diviseur commun
    private static long gcd(long a, long b) {

        a = Math.abs(a);
        b = Math.abs(b);

        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }

    private static String simplifyFraction(long numerator, long denominator) {

        numerator = (numerator * 128) / denominator;
        denominator = 128;

        return numerator + "/" + denominator;
    }

    private static String inchToFeet(int number) {
        if (number == 0) {
            return "";
        }
        if (number < 12) {
            return number + "\"";
        }
        int feet = number / 12;
        int inches = number %= 12;

        if (inches == 0) {
            return feet + "'";
        }

        return feet + "' " + inches + "\"";
    }

    public static String floatToImperial(float number) {
        //Creation of the integer part
        BigDecimal numberBD = new BigDecimal(String.valueOf(number));   //String.valueOf(number) utilisé pour éviter les erreurs de précision
        BigDecimal integerPart = numberBD.setScale(0, BigDecimal.ROUND_DOWN);
        BigDecimal decimalPart = numberBD.subtract(integerPart);

        String fraction = inchToFeet(integerPart.intValue());

        if (decimalPart.compareTo(BigDecimal.ZERO) == 0) {
            return fraction;
        }
        if (integerPart.intValue() != 0) {
            fraction += " ";
        }

        long decimalScale = decimalPart.scale();
        long denominator = (long) Math.pow(10, decimalScale);
        long numerator = decimalPart.unscaledValue().longValue();

        if (numerator == 0) return fraction;
        else if (denominator == 0) return "NaN";

        long gcdVal = gcd(numerator, denominator);

        numerator /= gcdVal;
        denominator /= gcdVal;

        if (denominator > 128) {
            fraction += simplifyFraction(numerator, denominator);
        }
        else if (denominator <= 0) {
            fraction += " NaN";
        }
        else {
            fraction += numerator + "/" + denominator;
        }

        return fraction;

    }

    private static float fractToFloat(String value){
        float floatValue = 0f;
        String[] splitArray = value.split("/", 2);


        if (splitArray.length == 1) {
            return (float) Float.parseFloat(splitArray[0]);
        }
        else if(splitArray.length == 2){
            int numerator = Integer.parseInt(splitArray[0]);
            int denominator = Integer.parseInt(splitArray[1]);
            return (float) numerator / denominator;
        }

        return floatValue;
    }

    public static float imperialToFloat(String value) {

        // Check if the input is a single number
        if (value.matches("\\d+")) {
            return Float.parseFloat(value);
        }


        int feet = 0;
        int inches = 0;
        float floatValue = 0f;
        String fraction = "";

        // Define the format for imperial values
        String patternString = "(?:(\\d+)'\\s*)?(?:(\\d+)\"\\s*)?(?:(\\d+/\\d+))?";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(value);

        // Find the pattern in the string
        if (matcher.find()) {
            if (matcher.group(1) != null)
                feet = Integer.parseInt(matcher.group(1));
            if (matcher.group(2) != null)
                inches = Integer.parseInt(matcher.group(2));
            fraction = matcher.group(3);
        }

        floatValue += (12 * feet) + inches;

        if (fraction != null && !fraction.isEmpty()) {
            floatValue += fractToFloat(fraction);
        }

        return floatValue;
    }

    public static boolean isConvertible(String value) {
        // Define the format for imperial values
        String patternString = "(?:(\\d+)'\\s*)?(?:(\\d+)\"\\s*)?(?:(\\d+/\\d+)?)?";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(value);

        // Find the pattern in the string
        if (matcher.find()) {
            int feet = parseGroup(matcher.group(1));
            int inches = parseGroup(matcher.group(2));
            String fraction = matcher.group(3);

            // If there's no feet and only a single number, consider it as inches
            if (feet == 0 && fraction == null && isValidSingleNumber(inches)) {
                return true;
            }

            // Perform validation for real imperial values
            if (isValidMeasurement(feet, inches) && isValidFraction(fraction)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isValidSingleNumber(int inches) {
        return inches >= 0;
    }

    private static int parseGroup(String group) {
        return group != null ? Integer.parseInt(group) : 0;
    }

    private static boolean isValidMeasurement(int feet, int inches) {
        return feet >= 0 && inches >= 0 && inches < 12;
    }

    private static boolean isValidFraction(String fraction) {
        if (fraction == null || fraction.isEmpty()) {
            return true; // No fraction is also considered valid
        }

        String[] parts = fraction.split("/");
        if (parts.length == 2) {
            try {
                int numerator = Integer.parseInt(parts[0]);
                int denominator = Integer.parseInt(parts[1]);
                return numerator >= 0 && denominator > 0;
            } catch (NumberFormatException e) {
                return false; // Unable to parse fraction
            }
        }

        return false; // Invalid fraction format
    }

}
