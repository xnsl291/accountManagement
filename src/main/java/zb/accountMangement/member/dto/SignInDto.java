package zb.accountMangement.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SignInDto {
  @NotNull
  private String phoneNumber;

  @NotNull
  private String password; //로그인 패스워드



}
