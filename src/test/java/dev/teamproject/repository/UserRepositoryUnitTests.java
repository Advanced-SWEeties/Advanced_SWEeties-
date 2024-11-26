package dev.teamproject.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.teamproject.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserRepositoryUnitTests {
  @Mock
  private UserRepository mockUserRepository;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new User();
    user.setUserId(1L);
    user.setUsername("testuser");
  }

  @Test
  void testFindByUsername_UserExists() {
    when(mockUserRepository.findByUsername("testuser")).thenReturn(user);

    User foundUser = mockUserRepository.findByUsername("testuser");

    assertNotNull(foundUser);
    assertEquals("testuser", foundUser.getUsername());
    verify(mockUserRepository).findByUsername("testuser");
  } 

  @Test
  void testFindByUsername_UserNotFound() {
    when(mockUserRepository.findByUsername("nonexistentuser")).thenReturn(null);

    User foundUser = mockUserRepository.findByUsername("nonexistentuser");

    assertNull(foundUser);
    verify(mockUserRepository).findByUsername("nonexistentuser");
  }

  @Test
  void testSaveUser() {
    User newUser = new User();
    newUser.setUsername("newuser");
    newUser.setPassword("password123");

    when(mockUserRepository.save(newUser)).thenReturn(newUser);

    User savedUser = mockUserRepository.save(newUser);

    assertNotNull(savedUser);
    assertEquals("newuser", savedUser.getUsername());
    verify(mockUserRepository).save(newUser);
  }

  @Test
  void testDeleteUser() {
    Long userId = 1L;
    doNothing().when(mockUserRepository).deleteById(userId);

    mockUserRepository.deleteById(userId);

    verify(mockUserRepository).deleteById(userId);
  }
}