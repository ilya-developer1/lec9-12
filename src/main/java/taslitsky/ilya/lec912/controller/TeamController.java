package taslitsky.ilya.lec912.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taslitsky.ilya.lec912.dto.TeamDto;
import taslitsky.ilya.lec912.service.TeamService;

@RestController
@RequestMapping("/teams")
public class TeamController {
  @Autowired
  private TeamService teamService;

  @GetMapping
  public List<TeamDto> getAllTeams() {
    return teamService.getAll();
  }

  @GetMapping("/{id}")
  public TeamDto getTeamById(@PathVariable Long id) {
    return teamService.getById(id);
  }
}
