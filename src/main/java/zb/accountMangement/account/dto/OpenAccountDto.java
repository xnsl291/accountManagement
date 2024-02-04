package zb.accountMangement.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import zb.accountMangement.account.type.AccountStatus;

@Getter
@Setter
@Builder
public class OpenAccountDto {

  private String nickname;

  @NotNull
  private String password; //계좌비밀번호

}
