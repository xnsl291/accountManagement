package zb.accountMangement.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class AccountManagementDto {

  private String nickname;

  @NotBlank
  private String password; //계좌비밀번호

}
