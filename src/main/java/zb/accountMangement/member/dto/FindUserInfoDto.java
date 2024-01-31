package zb.accountMangement.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindUserInfoDto {
  private String name;
  private String phone;
  private String token;
}
