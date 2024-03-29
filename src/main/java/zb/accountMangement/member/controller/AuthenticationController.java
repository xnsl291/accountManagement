package zb.accountMangement.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.common.auth.JwtToken;
import zb.accountMangement.common.service.ValidationService;
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
    private final ValidationService validationService;

    /**
    * 회원가입
    * @param token - 토큰
    * @param signUpDto - 회원가입 dto (이름, 핸드폰번호, 로그인 PW, 초기계좌 PW)
    * @return 성공여부
    */
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(
            @RequestHeader(value = "Authorization") String token,
            @Valid @RequestBody SignUpDto signUpDto){
        authenticationService.signUp(token, signUpDto);
        return ResponseEntity.ok().body("회원가입 성공");
    }

  /**
   * 회원탈퇴
   * @param token - 토큰
   * @param userId - id
   * @return "회원탈퇴완료"
   */
    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> deleteUserInfo(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable("user_id") @Min(1) Long userId){
        validationService.validTokenNUserId(token,userId);
        return ResponseEntity.ok().body(authenticationService.deleteUser(userId));
    }

    /**
    * 핸드폰 인증 성공 여부
    * @param token - 토큰
    * @param smsVerificationDto - 문자인증 dto (인증번호, 핸드폰번호)
    * @return 성공여부 (T/F)
    */
    @PostMapping("/verify-phone")
    public ResponseEntity<Boolean> verifySMS(
            @RequestHeader(value = "Authorization") String token,
            @Valid @RequestBody SmsVerificationDto smsVerificationDto) {
        validationService.validTokenNUserPhoneNumber(token,smsVerificationDto.getPhoneNumber());
        return ResponseEntity.ok().body(sendMessageService.verifyCode(token, smsVerificationDto));
    }

    /**
    * 비밀번호 재설정 요청
    * @param token - 토큰
    * @param userId  사용자 ID
    * @param findUserInfoDto - 회원정보 조회 dto (이름, 핸드폰번호)
    * @return "인증 메세지 발송 완료"
    */
    @PostMapping("/find-pw/{user_id}")
    public ResponseEntity<String> requestResetPw(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable("user_id") @Min(1) Long userId,
            @Valid @RequestBody FindUserInfoDto findUserInfoDto) {
        validationService.validTokenNUserId(token,userId);
        return ResponseEntity.ok().body(authenticationService.requestResetPw(token,userId,findUserInfoDto));
    }

    /**
    * 비밀번호 재설정
    * @param token - 토큰
    * @param userId - 사용자 ID
    * @param resetPwDto - 비밀번호 재설정 dto (인증번호, 새로운 PW)
    * @return "비밀번호 재설정 완료"
    */
    @PatchMapping("/find-pw/{user_id}/confirm")
    public ResponseEntity<String> verifyResetPw(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable("user_id") @Min(1) Long userId,
            @Valid @RequestBody ResetPwDto resetPwDto) {
        validationService.validTokenNUserId(token,userId);
        return ResponseEntity.ok().body(authenticationService.verifyResetPw(token,userId,resetPwDto));
    }

    /**
    * 로그인
    * @param signInDto - 로그인 dto (핸드폰번호, 로그인 PW)
    * @return token
    */
    @PostMapping("/login")
    public ResponseEntity<JwtToken> signIn(@Valid @RequestBody SignInDto signInDto){
        return ResponseEntity.ok().body(authenticationService.signIn(signInDto));
    }

    /**
    * 로그아웃
    * @param token - 토큰
    * @return "로그아웃 완료"
    */
    @PostMapping("/logout")
    public ResponseEntity<String> signOut(@RequestHeader(value = "Authorization") String token){
        return ResponseEntity.ok().body(authenticationService.signOut(token));
    }
}
