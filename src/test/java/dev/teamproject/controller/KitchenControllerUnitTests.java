package dev.teamproject.controller;

import static org.hamcrest.CoreMatchers.is;
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

import dev.teamproject.service.UserService;
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


  //TODO nearestKitchen and topRatedKitchen
  @Test
  @Order(5)
  public void getNearestKitchenTest() throws Exception {
    // precondition
    given(userService.getUserLocation("some place")).willReturn(new UserLocation(1.0, 1.0, "some place"));
    given(kitchenService.getAllKitchens()).willReturn(List.of(kitchen));

    // action
    ResultActions response = mockMvc.perform(get("/api/kitchens/nearest?address={address}&count={count}",
        "Columbia University", 1));

    // verify
    response.andExpect(status().isOk())
        .andDo(print());
  }
}
