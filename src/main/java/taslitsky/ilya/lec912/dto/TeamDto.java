package taslitsky.ilya.lec912.dto;


import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter @Setter
@ToString
@EqualsAndHashCode
public class TeamDto {
  private String name;
  private BigDecimal bank;
  private List<PlayerDto> players;
}
