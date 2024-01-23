package zb.accountMangement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationInfo {
  private String phoneNumber;
  private String verificationCode;
  private String token;

}
