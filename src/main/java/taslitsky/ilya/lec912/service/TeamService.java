package taslitsky.ilya.lec912.service;


import java.util.List;
import taslitsky.ilya.lec912.dto.PlayerDto;
import taslitsky.ilya.lec912.dto.TeamDto;

public interface TeamService {
  TeamDto getById(Long id);
  List<TeamDto> getAll();
  Long create(TeamDto teamDto);
}
