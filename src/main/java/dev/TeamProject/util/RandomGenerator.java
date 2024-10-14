package dev.TeamProject.util;

import java.util.Random;

public class RandomGenerator {
  private static final String SOURCE = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

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