package taslitsky.ilya.lec912.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import taslitsky.ilya.lec912.dto.PlayerDto;
import taslitsky.ilya.lec912.dto.RestResponse;
import taslitsky.ilya.lec912.service.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {
  @Autowired
  private PlayerService playerService;

  @GetMapping
  public List<PlayerDto> getAllPlayers() {
   return playerService.getAll();
  }

  @GetMapping("/{id}")
  public PlayerDto getPlayerById(@PathVariable Long id) {
    return playerService.getById(id);
  }

  @GetMapping("/name")
  public List<PlayerDto> getPlayerByName(@RequestParam String name, @RequestParam int start, @RequestParam int finish) {
    return playerService.getByName(name,start, finish);
  }

  @GetMapping("/surname")
  public List<PlayerDto> getPlayerBySurname(@RequestParam String surname, @RequestParam int start, @RequestParam int finish) {
    return playerService.getBySurname(surname,start, finish);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public RestResponse updatePlayerById(@PathVariable Long id, @Valid @RequestBody PlayerDto playerDto) {
    playerService.update(id, playerDto);
    return new RestResponse("OK");
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RestResponse createPlayer(@Valid @RequestBody PlayerDto playerDto) {
    Long id = playerService.create(playerDto);
    return new RestResponse(String.valueOf(id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public RestResponse deletePlayer(@PathVariable Long id) {
    playerService.deleteById(id);
    return new RestResponse("OK");
  }


}
