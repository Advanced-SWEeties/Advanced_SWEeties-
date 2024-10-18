package dev.teamproject.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.UserLocation;
import dev.teamproject.service.KitchenService;
import dev.teamproject.service.UserService;
import java.util.List;
import java.util.Optional;
import org.glassfish.jaxb.core.v2.TODO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;




/**
 * Unit testing for kitchen Controller.
 */
@WebMvcTest(KitchenController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KitchenControllerUnitTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private KitchenService kitchenService;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  Kitchen kitchen;
  Kitchen kitchen2;

  private static final String ADDRESS = "Columbia University";

  /**
   *  set up a kitchen object before each test.
   */
  @BeforeEach
  public void setup() {
    kitchen = Kitchen.builder()
            .kitchenId(1L)
            .name("Kitchen1")
            .address("some place")
            .contactPhone("1234567890").build();

    kitchen2 = Kitchen.builder()
            .kitchenId(2L)
            .name("Kitchen2")
            .address("116 street")
            .contactPhone("987654321").build();
  }

  //Post Controller
  @Test
  @Order(1)
  public void saveKitchenTest() throws Exception {
    // precondition
    given(kitchenService.saveKitchen(any(Kitchen.class))).willReturn(kitchen);

    // action
    ResultActions response = mockMvc.perform(post("/api/kitchens/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(kitchen)));

    // verify
    response.andExpect(status().isCreated())
            .andExpect(content().string("New kitchen added successfully."));
  }

  //get by Id controller
  @Test
  @Order(2)
  public void getByIdKitchenTest() throws Exception {
    // precondition
    given(kitchenService.getKitchenById(kitchen.getKitchenId())).willReturn(Optional.of(kitchen));

    // action
    ResultActions response = mockMvc.perform(get("/api/kitchens/details?kitchenId={id}",
            kitchen.getKitchenId()));

    // verify
    response.andExpect(status().isOk())
            .andExpect(jsonPath("$.name",
                    is(kitchen.getName())))
            .andExpect(jsonPath("$.address",
                    is(kitchen.getAddress())))
            .andExpect(jsonPath("$.contactPhone",
                    is(kitchen.getContactPhone())));

  }


  //Update kitchen
  @Test
  @Order(3)
  public void updateKitchenTest() throws Exception {
    // precondition
    given(kitchenService.getKitchenById(kitchen.getKitchenId())).willReturn(Optional.of(kitchen));
    kitchen.setContactPhone("34567898");
    kitchen.setAddress("nowhere");
    given(kitchenService.updateKitchen(kitchen, kitchen.getKitchenId())).willReturn(kitchen);

    // action
    ResultActions response = mockMvc.perform(put("/api/kitchens/update",
            kitchen.getKitchenId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(kitchen)));

    // verify
    response.andExpect(status().isOk())
            .andExpect(content().string("Kitchen information updated successfully."));
  }


  // delete kitchen
  @Test
  @Order(4)
  public void deleteKitchenTest() throws Exception {
    // precondition
    willDoNothing().given(kitchenService).deleteKitchen(kitchen.getKitchenId());

    // action
    ResultActions response = mockMvc.perform(delete("/api/kitchens/delete?kitchenId={id}",
            kitchen.getKitchenId()));

    // then - verify the output
    response.andExpect(status().isOk())
            .andExpect(content().string("Kitchen deleted successfully."));;
  }

  @Test
  @Order(5)
  public void getNearestKitchensInvalidAddressTest() throws Exception {
    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
        "", 1));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid parameters"));
  }

  @Test
  @Order(6)
  public void getNearestKitchensNegativeCountTest() throws Exception {
    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, -1));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid parameters"));
  }

  @Test
  @Order(7)
  public void getNearKitchensNoKitchensFoundTest() throws Exception {
    // precondition
    given(kitchenService.getAllKitchens()).willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, 1));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }

  @Test
  @Order(8)
  public void getNearKitchensNoKitchensFoundTest2() throws Exception {
    // precondition
    given(userService.getNearestKitchens(ADDRESS, kitchenService.getAllKitchens(), 1))
        .willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, 1));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("Invalid address"));
  }

  @Test
  @Order(9)
  public void getNearKitchensTest() throws Exception {
    // precondition
    given(kitchenService.getAllKitchens())
        .willReturn(List.of(kitchen, kitchen2));
    given(userService.getNearestKitchens(ADDRESS, List.of(kitchen, kitchen2), 2))
        .willReturn(List.of(kitchen, kitchen2));

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, 2));

    // verify
    response.andExpect(status().isOk())
        .andExpect(content().string(containsString("Kitchen1")))
        .andExpect(content().string(containsString("some place")))
        .andExpect(content().string(containsString("Kitchen2")))
        .andExpect(content().string(containsString("116 street")));
  }

  @Test
  @Order(10)
  public void getTopRatedKitchensInvalidCountTest() throws Exception {
    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/top-rated?count={count}",
            -1));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("invalid count: negative number"));
  }

  @Test
  @Order(11)
  public void getTopRatedKitchensNoKitchensFoundTest() throws Exception {
    // precondition
    given(kitchenService.fetchTopRatedKitchens(10)).willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/top-rated?count={count}",
            10));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }

  @Test
  @Order(12)
  public void getTopRatedKitchensTest() throws Exception {
    // precondition
    given(kitchenService.fetchTopRatedKitchens(2))
        .willReturn(List.of(kitchen, kitchen2));

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/top-rated?count={count}",
            2));

    // verify
    response.andExpect(status().isOk())
        .andExpect(content().string(containsString("Kitchen1")))
        .andExpect(content().string(containsString("some place")))
        .andExpect(content().string(containsString("Kitchen2")))
        .andExpect(content().string(containsString("116 street")));
  }


}
