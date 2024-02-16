package zb.accountMangement.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.common.auth.JwtToken;
import zb.accountMangement.member.dto.*;
import zb.accountMangement.member.service.AuthenticationService;
import zb.accountMangement.member.service.SendMessageService;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final SendMessageService sendMessageService;

  /**
   * 회원가입
   * @param signUpDto
   * @return 성공여부
   */
  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@Valid @RequestBody SignUpDto signUpDto){
    authenticationService.signUp(signUpDto);
    return ResponseEntity.ok().body("회원가입 성공");
  }

  /**
   * 회원탈퇴
   * @param userId - id
   * @return "회원탈퇴완료"
   */
  @DeleteMapping("/{user_id}")
  public ResponseEntity<String> deleteUserInfo(
          @PathVariable("user_id") @Min(1) Long userId){
    return ResponseEntity.ok().body(authenticationService.deleteUser(userId));
  }

  /**
   * 핸드폰 인증 성공 여부
   * @param smsVerificationDto
   * @return 성공여부 (T/F)
   */
  @PostMapping("/verify-phone")
  public ResponseEntity<Boolean> verifySMS(
          @Valid @RequestBody SmsVerificationDto smsVerificationDto) {
    return ResponseEntity.ok().body(sendMessageService.verifyCode(smsVerificationDto));
  }

  /**
   * 비밀번호 재설정 요청
   * @param userId  유저id
   * @param findUserInfoDto
   * @return "인증 메세지 발송 완료"
   */
  @PostMapping("/find-pw/{user_id}")
  public ResponseEntity<String> requestResetPw(
      @PathVariable("user_id") @Min(1) Long userId,
      @Valid @RequestBody FindUserInfoDto findUserInfoDto) {
    return ResponseEntity.ok().body(authenticationService.requestResetPw(userId,findUserInfoDto));
  }

  /**
   * 비밀번호 재설정
   * @param userId
   * @param resetPwDto
   * @return "비밀번호 재설정 완료"
   */
  @PatchMapping("/find-pw/{user_id}/confirm")
  public ResponseEntity<String> verifyResetPw(
      @PathVariable("user_id") @Min(1) Long userId,
      @Valid @RequestBody ResetPwDto resetPwDto) {

    return ResponseEntity.ok().body(authenticationService.verifyResetPw(userId,resetPwDto));
  }

  @PostMapping("/login")
  public ResponseEntity<JwtToken> signIn(@Valid @RequestBody SignInDto signInDto){
    return ResponseEntity.ok().body(authenticationService.signIn(signInDto));
  }

  @PostMapping("/logout")
  public ResponseEntity<String> signOut(@RequestHeader(value = "Authorization") String token){
    return ResponseEntity.ok().body(authenticationService.signOut(token));
  }
}
