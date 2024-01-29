package zb.accountMangement.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
public class SignUpDto {

  @NotNull
  private String name;

  @NotNull
  private String phoneNumber;

  @NotNull
  private String password; //로그인 패스워드

}
