package taslitsky.ilya.lec912.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import taslitsky.ilya.lec912.Lec912Application;
import taslitsky.ilya.lec912.dto.TeamDto;
import taslitsky.ilya.lec912.service.TeamService;


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = Lec912Application.class)
@AutoConfigureMockMvc
class TeamControllerTest {

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private MockMvc mvc;

  @Autowired
  private TeamService teamService;

  @Test
  void whenGetAllTeams_ThenGetListWithTeams() throws Exception {
    // GIVEN
    List<TeamDto> teamDtos = new ArrayList<>();


    for (int i = 0; i < 20; i++) {
      TeamDto teamDto = createTeamDto();
      teamDtos.add(teamDto);
      teamService.create(teamDto);
    }

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(get("/teams")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONArray actual = new JSONArray(response);
    JSONArray expected = new JSONArray();

    for (int i = 0; i < teamDtos.size(); i++) {
      JSONObject jsonObject = teamDtoToJSONObject(teamDtos.get(i));
      expected.put(jsonObject);
    }

    Assertions.assertEquals(expected.toString(), actual.toString());
  }

  @Test
  void whenGetTeamById_ThenGetTeamDto() throws Exception {
    // GIVEN
    TeamDto teamDto = createTeamDto();
    Long id = teamService.create(teamDto);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(get("/teams/" + id))
        .andExpect(status().is(200))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject expected = teamDtoToJSONObject(teamDto);
    JSONObject actual = new JSONObject(response);
    Assertions.assertEquals(expected.toString(), actual.toString());
  }

  @Test
  void whenGetTeamByWrongId_ThenGetNotFoundExceptionMessage() throws Exception {
    // GIVEN
    TeamDto teamDto = createTeamDto();
    teamService.create(teamDto);

    // WHEN AND THEN
    MvcResult mvcResult = mvc.perform(get("/teams/" + Long.MAX_VALUE))
        .andExpect(status().is(404))
        .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    JSONObject actual = new JSONObject(response);
    String errorMessageActual = (String) actual.get("message");
    String errorMessageExpected = "Team with id %d not found".formatted(Long.MAX_VALUE);
    Assertions.assertEquals(errorMessageExpected, errorMessageActual);
  }


  private TeamDto createTeamDto() {
    return TeamDto.builder()
        .name("TestName")
        .bank(BigDecimal.valueOf(1.11))
        .players(new ArrayList<>())
        .build();
  }

  private JSONObject teamDtoToJSONObject(TeamDto teamDto) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", teamDto.getName());
    jsonObject.put("bank", teamDto.getBank());
    jsonObject.put("players", teamDto.getPlayers());

    return jsonObject;
  }

}