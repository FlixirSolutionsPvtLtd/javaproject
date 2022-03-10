package com.alinote.api.utility;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

  private CheckUtil() {}

  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

  private static final String ALPHANUMERIC_REGEX = "^[a-zA-Z0-9]*$";
  private static final Pattern ALPHANUMERIC_PATTERN =
      Pattern.compile(ALPHANUMERIC_REGEX, Pattern.CASE_INSENSITIVE);

  private static final String CONTACT_NUMBER_REGEX = "^[- +()]*[0-9][- +()0-9]*";
//private static final String CONTACT_NUMBER_REGEX = "^[- +()]*[0-9]?[7-9][0-9]{9}";
  private static final Pattern CONTACT_NUMBER_PATTERN =
      Pattern.compile(CONTACT_NUMBER_REGEX, Pattern.CASE_INSENSITIVE);

  public static Boolean hasValue(Object object) {
    return null != object;
  }

  public static <T> Boolean hasValue(Set<T> set) {
    return null != set && !set.isEmpty();
  }

  public static Boolean hasValue(Integer integer) {
    return null != integer;
  }
  
  public static Boolean hasValue(Long longValue) {
    return null != longValue;
  }

  public static Boolean hasValue(String[] stringArray) {
    return null != stringArray && stringArray.length > 0;
  }

  public static Boolean hasValue(Double number) {
    return null != number && !number.isNaN() && !number.isInfinite();
  }

  public static boolean hasValue(Map<?, ?> map) {
    return map != null && !map.isEmpty();
  }

  public static boolean hasValue(List<?> list) {
    return list != null && !list.isEmpty();
  }

  public static boolean hasValue(String string) {
    return string != null && !string.trim().isEmpty();
  }

  public static boolean isValidEmail(String emailAddress) {
    Matcher matcher = EMAIL_PATTERN.matcher(emailAddress);
    return matcher.matches();
  }

  public static boolean isAlphaNumeric(String input) {
    Matcher matcher = ALPHANUMERIC_PATTERN.matcher(input);
    return matcher.matches();
  }

  public static boolean isValidPhoneNumber(String contactNumber) {
    Matcher matcher = CONTACT_NUMBER_PATTERN.matcher(contactNumber);
    //return contactNumber.matches(CONTACT_NUMBER_REGEX);
    return matcher.matches();
  }
}
