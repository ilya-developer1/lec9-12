package taslitsky.ilya.lec912.service;

import java.util.List;
import taslitsky.ilya.lec912.dto.PlayerDto;

public interface PlayerService {
  Long create(PlayerDto playerDto);
  void deleteAll();
  PlayerDto getById(Long id);
  List<PlayerDto> getByName(String name, int start, int finish);
  List<PlayerDto> getBySurname(String surname,int start, int finish);
  void update(Long id, PlayerDto playerDto);
  void deleteById(Long id);
  List<PlayerDto> getAll();
}
