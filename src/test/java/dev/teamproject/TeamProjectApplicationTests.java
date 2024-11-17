package dev.teamproject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
class TeamProjectApplicationTests {

  @Test
  void testMain() {
    assertDoesNotThrow(() -> TeamProjectApplication.main(new String[] {}),
        "Application failed to start.");
  }
}
