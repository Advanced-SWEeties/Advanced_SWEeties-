package dev.teamproject.util;

import java.util.Random;

/**
 * A utility class for generating random strings.
 * The generated strings are composed of characters from a predefined source.
 */
public class RandomGenerator {
  private static final String SOURCE = 
      "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

  /**
   * Generates a random string of the specified length.
   *
   * @param length the length of the random string to generate
   * @return a randomly generated string of the specified length
   */
  public static String generate(int length) {
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(SOURCE.length());
      sb.append(SOURCE.charAt(number));
    }

    return sb.toString();
  }
}