package taslitsky.ilya.lec912.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import taslitsky.ilya.lec912.dto.PlayerDto;
import taslitsky.ilya.lec912.entity.PlayerEntity;
import taslitsky.ilya.lec912.entity.TeamEntity;
import taslitsky.ilya.lec912.exception.NotFoundException;
import taslitsky.ilya.lec912.repo.PlayerRepo;
import taslitsky.ilya.lec912.repo.TeamRepo;
import taslitsky.ilya.lec912.service.PlayerService;

@Service
public class PlayerServiceImpl implements PlayerService {
  @Autowired
  private PlayerRepo playerRepo;
  @Autowired
  private TeamRepo teamRepo;

  @Override
  @Transactional
  public Long create(PlayerDto playerDto) {
   return playerRepo.save(dtoToEntity(playerDto));
  }

  @Override
  @Transactional
  public PlayerDto getById(Long id)  {
    return playerRepo.findById(id).map(this::entityToDto)
        .orElseThrow(() -> new NotFoundException("Player with id %d not found".formatted(id)));
  }

  @Override
  @Transactional
  public List<PlayerDto> getByName(String name, int start, int finish) {
    return playerEntityListToDto(playerRepo.findByName(name, start, finish));
  }

  @Override
  @Transactional
  public List<PlayerDto> getBySurname(String surname, int start, int finish) {
    return playerEntityListToDto(playerRepo.findBySurname(surname, start, finish));
  }

  @Override
  @Transactional
  public void update(Long id, PlayerDto playerDto) {
    PlayerEntity playerEntity = dtoToEntity(playerDto);
    playerEntity.setId(id);
    playerRepo.update(playerEntity);
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    PlayerEntity playerEntity = playerRepo.findById(id).orElseThrow(() ->
        new NotFoundException("Player with id %d not found".formatted(id)));
    playerRepo.delete(playerEntity);
  }

  @Override
  @Transactional
  public void deleteAll() {
    playerRepo.deleteAll();
  }

  @Override
  @Transactional
  public List<PlayerDto> getAll() {
    return playerEntityListToDto(playerRepo.findAll());
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
          .build();
      if(playerEntity.getTeam() != null) {
        playerDto.setTeamName(playerEntity.getTeam().getName());
      }

      playerDtos.add(playerDto);
    }
    return playerDtos;
  }

  private PlayerEntity dtoToEntity(PlayerDto playerDto) {
    PlayerEntity player = PlayerEntity.builder()
        .age(playerDto.getAge())
        .carrierStart(playerDto.getCarrierStart())
        .name(playerDto.getName())
        .surname(playerDto.getSurname())
        .build();
    if(playerDto.getTeamName() != null) {
      Optional<TeamEntity> teamEntity = teamRepo.findByName(playerDto.getTeamName());
      teamEntity.ifPresent(player::setTeam);
      if(teamEntity.isPresent()) {
        player.setTeam(teamEntity.get());
      } else {
        throw new NotFoundException("Team with name %s not found".formatted(playerDto.getTeamName()));
      }
    }
    return player;
  }


  private PlayerDto entityToDto(PlayerEntity playerEntity) {
    PlayerDto playerDto = PlayerDto.builder()
        .name(playerEntity.getName())
        .surname(playerEntity.getSurname())
        .carrierStart(playerEntity.getCarrierStart())
        .age(playerEntity.getAge())
        .build();
    if(playerEntity.getTeam() != null) {
      playerDto.setTeamName(playerEntity.getTeam().getName());
    }
    return playerDto;
  }
}
