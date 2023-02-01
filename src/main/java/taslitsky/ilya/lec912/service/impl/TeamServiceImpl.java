package taslitsky.ilya.lec912.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taslitsky.ilya.lec912.dto.PlayerDto;
import taslitsky.ilya.lec912.dto.TeamDto;
import taslitsky.ilya.lec912.entity.PlayerEntity;
import taslitsky.ilya.lec912.entity.TeamEntity;
import taslitsky.ilya.lec912.exception.NotFoundException;
import taslitsky.ilya.lec912.repo.TeamRepo;
import taslitsky.ilya.lec912.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService {

  @Autowired
  private TeamRepo teamRepo;

  @Override
  @Transactional
  public Long create(TeamDto teamDto) {
    return teamRepo.save(teamDtoToEntity(teamDto));
  }

  @Override
  @Transactional
  public TeamDto getById(Long id) {
     return teamRepo.findById(id).map(this::entityToDto)
        .orElseThrow(() -> new NotFoundException("Team with id %d not found".formatted(id)));
  }

  private TeamDto entityToDto(TeamEntity teamEntity) {
    return TeamDto.builder()
        .name(teamEntity.getName())
        .bank(teamEntity.getBank())
        .players(playerEntityListToDto(teamEntity.getPlayers()))
        .build();
  }

  private List<PlayerDto> playerEntityListToDto(List<PlayerEntity> playerEntities) {
    List<PlayerDto> playerDtos = new ArrayList<>();
    for (PlayerEntity playerEntity :
        playerEntities) {
      PlayerDto playerDto = PlayerDto.builder()
          .name(playerEntity.getName())
          .surname(playerEntity.getSurname())
          .carrierStart(playerEntity.getCarrierStart())
          .age(playerEntity.getAge())
          .teamName(playerEntity.getTeam().getName())
          .build();
      playerDtos.add(playerDto);
    }
    return playerDtos;
  }

  private List<TeamDto> teamEntityListToDto(List<TeamEntity> teamEntities) {
    List<TeamDto> teamDtos = new ArrayList<>();
    for (TeamEntity teamEntity :
        teamEntities) {
      TeamDto teamDto = TeamDto.builder()
          .name(teamEntity.getName())
          .bank(teamEntity.getBank())
          .players(playerEntityListToDto(teamEntity.getPlayers()))
          .build();
      teamDtos.add(teamDto);
    }
    return teamDtos;
  }

  private TeamEntity teamDtoToEntity(TeamDto teamDto) {
    return TeamEntity.builder()
        .name(teamDto.getName())
        .bank(teamDto.getBank())
        .build();
  }

  @Override
  @Transactional
  public List<TeamDto> getAll() {
    return teamEntityListToDto(teamRepo.findAll());
  }
}
