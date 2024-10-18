import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.teamproject.util.RandomGenerator;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the RandomGenerator class.
 * This test suite verifies the behavior of the generate method in various scenarios.
 */
public class RandomGeneratorTest {

  @Test
  public void testGenerateStringLength() {
    // Test if the generated string has the correct length
    int length = 10;
    String randomString = RandomGenerator.generate(length);
    assertEquals(length, randomString.length(), 
        "Generated string length should match the requested length.");
  }

  @Test
  public void testGenerateStringCharacters() {
    // Test if the generated string only contains characters from the predefined source
    String randomString = RandomGenerator.generate(20);
    String source = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

    for (char c : randomString.toCharArray()) {
      assertTrue(source.indexOf(c) >= 0, "Generated string contains an invalid character: " + c);
    }
  }

  @Test
  public void testGenerateEmptyString() {
    // Test if the generate method returns an empty string when the requested length is 0
    String randomString = RandomGenerator.generate(0);
    assertEquals(0, randomString.length(), "Generated string should be empty when length is 0.");
  }

  @Test
  public void testGenerateConsistency() {
    // Test if generate produces different strings across calls (low chance of being identical)
    String string1 = RandomGenerator.generate(10);
    String string2 = RandomGenerator.generate(10);

    assertNotEquals(string1, string2, "Generated strings should be different.");
  }
}
