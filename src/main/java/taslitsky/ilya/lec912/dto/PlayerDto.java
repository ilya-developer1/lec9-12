package taslitsky.ilya.lec912.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@ToString
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlayerDto {

  @NotBlank(message = "name is required")
  private String name;
  @NotBlank(message = "surname is required")
  private String surname;
  private LocalDate age;
  private LocalDate carrierStart;
  private String teamName;
}
