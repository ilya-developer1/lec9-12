package taslitsky.ilya.lec912.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@RequiredArgsConstructor
public class RestResponse {
  private final String result;
}
