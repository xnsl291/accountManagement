package zb.accountMangement.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ResetPwDto {
  private String token;

  @NotBlank
  private String inputCode;

  @NotBlank
  private String newPassword;
}
