package taslitsky.ilya.lec912.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import taslitsky.ilya.lec912.Lec912Application;
import taslitsky.ilya.lec912.dto.PlayerDto;
import taslitsky.ilya.lec912.dto.TeamDto;
import taslitsky.ilya.lec912.exception.NotFoundException;
import taslitsky.ilya.lec912.service.PlayerService;
import taslitsky.ilya.lec912.service.TeamService;


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = Lec912Application.class)
@AutoConfigureMockMvc
class PlayerControllerTest {
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private MockMvc mvc;

  @Autowired
  private PlayerService playerService;

  @Autowired
  private TeamService teamService;

  @AfterEach
  public void deleteAll() {
    playerService.deleteAll();
  }


  @Test
  void whenDeletePlayerById_PlayerDeletedFromDatabaseAndTeamNotDeleted() throws Exception {
    // GIVEN
    String teamName = "TestTeam";
    PlayerDto playerDto = createPlayerDto();
    playerDto.setTeamName(teamName);
    TeamDto teamDto = TeamDto.builder()
        .name(teamName)
        .bank(BigDecimal.valueOf(1.11))
        .build();
    Long teamId = teamService.create(teamDto);
    Long playerId = playerService.create(playerDto);
    JSONObject createdPlayerJSONObject = playerDtoToJsonObject(playerDto);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(delete("/players/" + playerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createdPlayerJSONObject.toString())
        )
        .andExpect(status().isOk())
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject jsonObjectResponse = new JSONObject(response);
    String expectedResult = "OK";
    String actualResult = jsonObjectResponse.getString("result");
    TeamDto teamShouldNotBeDelete = teamService.getById(teamId);
    teamDto.setPlayers(new ArrayList<>());
    String notFoundExceptionMessageExpected = "Player with id %d not found".formatted(playerId);

    Assertions.assertEquals(teamDto, teamShouldNotBeDelete);
    Assertions.assertEquals(expectedResult, actualResult);
    NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () ->
        playerService.getById(playerId));
    Assertions.assertEquals(notFoundExceptionMessageExpected, notFoundException.getMessage());


  }

  @Test
  void whenGetPlayerByNameWithCount_ThenGetPlayerList() throws Exception {
    // GIVEN
    List<PlayerDto> playerDtoList = new ArrayList<>();
    String name = "TestName";
    int start = 2;
    int finish = 3;

    for (int i = 0; i < 20; i++) {
      PlayerDto playerDto = createPlayerDto(name);
      playerDtoList.add(playerDto);
      playerService.create(playerDto);
    }

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(
             get("/players/name?name=%s&start=%d&finish=%d".formatted(name,start,finish))
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONArray actual = new JSONArray(response);
    JSONArray expected = new JSONArray();

    for (int i = start-1; i < finish; i++) {
      JSONObject jsonObject = playerDtoToJsonObject(playerDtoList.get(i));
      expected.put(jsonObject);
    }

    Assertions.assertEquals(expected.toString(), actual.toString());
  }

  @Test
  void whenGetPlayerBySurnameWithCount_ThenGetPlayerList() throws Exception {
    // GIVEN
    List<PlayerDto> playerDtoList = new ArrayList<>();
    int start = 4;
    int finish = 10;

    for (int i = 0; i < 20; i++) {
      PlayerDto playerDto = createPlayerDto("Name" + (i+1));
      playerDtoList.add(playerDto);
      playerService.create(playerDto);
    }

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(
            get("/players/surname?surname=%s&start=%d&finish=%d".formatted("TestSurname",start,finish))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONArray actual = new JSONArray(response);
    JSONArray expected = new JSONArray();

    for (int i = start-1; i < finish; i++) {
      JSONObject jsonObject = playerDtoToJsonObject(playerDtoList.get(i));
      expected.put(jsonObject);
    }

    Assertions.assertEquals(expected.toString(), actual.toString());
  }

  @Test
  void whenGetAllPlayers_ThenGetList() throws Exception {
    // GIVEN
    List<PlayerDto> playerDtoList = new ArrayList<>();
    String name = "TestName";


    for (int i = 0; i < 20; i++) {
      PlayerDto playerDto = createPlayerDto(name + i);
      playerDtoList.add(playerDto);
      playerService.create(playerDto);
    }

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(get("/players")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONArray actual = new JSONArray(response);
    JSONArray expected = new JSONArray();

    for (int i = 0; i < playerDtoList.size(); i++) {
      JSONObject jsonObject = playerDtoToJsonObject(playerDtoList.get(i));
      expected.put(jsonObject);
    }

    Assertions.assertEquals(expected.toString(), actual.toString());
  }

  @Test
  void whenGetPlayerById_ThenGetDto() throws Exception {
    // GIVEN
    PlayerDto playerDto = createPlayerDto();
    Long id = playerService.create(playerDto);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(get("/players/" + id))
        .andExpect(status().is(200))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject expected = playerDtoToJsonObject(playerDto);
    JSONObject actual = new JSONObject(response);
    Assertions.assertEquals(expected.toString(), actual.toString());
  }

  @Test
  void whenGetPlayerByIdWithWrongId_ThenGetNotFoundExceptionMessage() throws Exception {
    // GIVEN
    PlayerDto playerDto = createPlayerDto();
    playerService.create(playerDto);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(get("/players/" + Long.MAX_VALUE))
        .andExpect(status().is(404))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject actual = new JSONObject(response);
    String errorMessageActual = (String) actual.get("message");
    String errorMessageExpected = "Player with id %d not found".formatted(Long.MAX_VALUE);
    Assertions.assertEquals(errorMessageExpected, errorMessageActual);
  }


  @Test
  void updatePlayerById() throws Exception {
    // GIVEN
    PlayerDto createdPlayer = createPlayerDto();
    Long id = playerService.create(createdPlayer);

    PlayerDto updatedPlayer = createPlayerDto();
    updatedPlayer.setName("UpdatedName");
    updatedPlayer.setSurname("UpdatedSurname");

    JSONObject updatePlayerJSONObject = playerDtoToJsonObject(updatedPlayer);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(put("/players/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePlayerJSONObject.toString())
        )
        .andExpect(status().isOk())
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject jsonObjectResponse = new JSONObject(response);
    String expectedResult = "OK";
    String actualResult = jsonObjectResponse.getString("result");

    PlayerDto actualUpdatedPlayer = playerService.getById(id);

    Assertions.assertEquals(expectedResult, actualResult);
    Assertions.assertEquals(updatedPlayer, actualUpdatedPlayer);
  }

  @Test
  void whenCreatePlayer_ThenGetPlayerId() throws Exception {
    // GIVEN
    String jsonPlayerString = createPlayerJson();

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(post("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonPlayerString)
        )
        .andExpect(status().isCreated())
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject jsonResponse = new JSONObject(response);
    String id = (String) jsonResponse.get("result");
    PlayerDto createdPlayerDto = playerService.getById(Long.valueOf(id));
    JSONObject createdPlayerJsonObject = playerDtoToJsonObject(createdPlayerDto);

    String expected = playerDtoToJsonObject(createPlayerDto()).toString();
    String actual = createdPlayerJsonObject.toString();

    Assertions.assertTrue(id.matches("\\d+"));
    Assertions.assertEquals(expected, actual);

  }

  @Test
  void whenCreatePlayerWithTeamName_ThenGetPlayerId() throws Exception {
    // GIVEN
    String teamName = "TestTeam";
    PlayerDto playerDto = createPlayerDto();
    playerDto.setTeamName(teamName);
    TeamDto teamDto = TeamDto.builder()
        .name(teamName)
        .bank(BigDecimal.valueOf(1.11))
        .build();

    teamService.create(teamDto);

    JSONObject createdPlayerJSONObject = playerDtoToJsonObject(playerDto);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(post("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createdPlayerJSONObject.toString())
        )
        .andExpect(status().isCreated())
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject jsonResponse = new JSONObject(response);
    String id = (String) jsonResponse.get("result");
    PlayerDto createdPlayerDto = playerService.getById(Long.valueOf(id));
    JSONObject createdPlayerJsonObject = playerDtoToJsonObject(createdPlayerDto);

    String expected = playerDtoToJsonObject(playerDto).toString();
    String actual = createdPlayerJsonObject.toString();

    Assertions.assertTrue(id.matches("\\d+"));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void whenCreatePlayerWithNonExistentTeamName_ThenGetNotFoundErrorMessage() throws Exception {
    // GIVEN
    String teamName = "TestTeam";
    PlayerDto playerDto = createPlayerDto();
    playerDto.setTeamName(teamName);

    JSONObject createdPlayerJSONObject = playerDtoToJsonObject(playerDto);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(post("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createdPlayerJSONObject.toString())
        )
        .andExpect(status().is(404))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject actual = new JSONObject(response);
    String errorMessageActual = (String) actual.get("message");
    String errorMessageExpected = "Team with name %s not found".formatted(teamName);
    Assertions.assertEquals(errorMessageExpected, errorMessageActual);
  }


  @Test
  public void whenCreatePlayerWithoutName_ThenGetValidationErrorMessage() throws Exception {
    // GIVEN
    String surname = "Taslitsky";
    LocalDate age = LocalDate.of(2002, 8, 12);
    LocalDate carrierStart = LocalDate.of(2020, 3, 15);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("surname", surname);
    jsonObject.put("age", age);
    jsonObject.put("carrierStart", carrierStart);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(post("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString())
        )
        .andExpect(status().isBadRequest())
        .andReturn();
    String response = mvcResult.getResponse().getContentAsString();
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray messages = jsonResponse.getJSONArray("message");

    Assertions.assertEquals("name is required", messages.get(0));
  }


  @Test
  public void whenCreatePlayerWithoutSurname_ThenGetValidationErrorMessage() throws Exception {
    // GIVEN
    String name = "Ilya";
    LocalDate age = LocalDate.of(2002, 8, 12);
    LocalDate carrierStart = LocalDate.of(2020, 3, 15);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", name);
    jsonObject.put("age", age);
    jsonObject.put("carrierStart", carrierStart);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(post("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString())
        )
        .andExpect(status().isBadRequest())
        .andReturn();
    String response = mvcResult.getResponse().getContentAsString();
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray messages = jsonResponse.getJSONArray("message");
    String actual = (String) messages.get(0);
    String expected = "surname is required";
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void whenCreatePlayerWithoutNameAndSurname_ThenGetValidationErrorsMessage() throws Exception {
    // GIVEN
    LocalDate age = LocalDate.of(2002, 8, 12);
    LocalDate carrierStart = LocalDate.of(2020, 3, 15);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("age", age);
    jsonObject.put("carrierStart", carrierStart);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(post("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString())
        )
        .andExpect(status().isBadRequest())
        .andReturn();
    String response = mvcResult.getResponse().getContentAsString();
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray messages = jsonResponse.getJSONArray("message");

    List<Object> actual = messages.toList();

    Assertions.assertTrue(actual.contains("name is required"));
    Assertions.assertTrue(actual.contains("surname is required"));
  }


  private String createPlayerJson(String name) throws JSONException {
    if(name == null) {
      name = "TestName";
    }
    String surname = "TestSurname";
    LocalDate age = LocalDate.of(2002, 8, 12);
    LocalDate carrierStart = LocalDate.of(2020, 3, 15);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", name);
    jsonObject.put("surname", surname);
    jsonObject.put("age", age);
    jsonObject.put("carrierStart", carrierStart);
    return jsonObject.toString();
  }

  private String createPlayerJson() {
    return createPlayerJson(null);
  }

  private PlayerDto createPlayerDto() {
    return createPlayerDto(null);
  }

  private PlayerDto createPlayerDto(String name) {
    if(name == null) {
      name = "TestName";
    }
    return PlayerDto.builder()
        .name(name)
        .surname("TestSurname")
        .carrierStart(LocalDate.of(2020, 3, 15))
        .age(LocalDate.of(2002, 8,12))
        .build();
  }

  private JSONObject playerDtoToJsonObject(PlayerDto playerDto) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", playerDto.getName());
    jsonObject.put("surname", playerDto.getSurname());
    jsonObject.put("age", playerDto.getAge());
    jsonObject.put("carrierStart", playerDto.getCarrierStart());
    if(playerDto.getTeamName() == null) {
      jsonObject.put("teamName", JSONObject.NULL);
    } else {
      jsonObject.put("teamName", playerDto.getTeamName());
    }
    return jsonObject;
  }
}