package zb.accountMangement.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.common.auth.JwtToken;
import zb.accountMangement.common.service.ValidationService;
import zb.accountMangement.member.dto.*;
import zb.accountMangement.member.model.entity.Member;
import zb.accountMangement.member.service.MemberService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/member")
public class MemberController {
  private final MemberService memberService;
  private final ValidationService validationService;

  /**
   * 회원 정보 열람 기능
   * @param token - 토큰
   * @return Member
   */
  @GetMapping("/{user_id}")
  public ResponseEntity<Member> getUserInfo(
          @RequestHeader(value = "Authorization") String token,
          @PathVariable("user_id") @Min(1) Long userId  ){
    validationService.validTokenNUserId(token,userId);
    return ResponseEntity.ok().body(memberService.getUserById(userId));
  }

  /**
   * 회원 정보 수정
   * @param token - 토큰
   * @param updateUserDto - 사용자 정보수정 dto (이름, 핸드폰번호, 로그인 PW)
   * @return true
   */
  @PatchMapping("/{user_id}")
  public ResponseEntity<Member> updateUserInfo(
      @RequestHeader(value = "Authorization") String token,
      @PathVariable("user_id") @Min(1) long userId,
      @RequestBody @Valid UpdateUserDto updateUserDto){
    validationService.validTokenNUserId(token,userId);
    return ResponseEntity.ok().body(memberService.updateUserInfo(token, userId, updateUserDto));
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
    return ResponseEntity.ok().body(memberService.requestResetPw(token,userId,findUserInfoDto));
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
    return ResponseEntity.ok().body(memberService.verifyResetPw(token,userId,resetPwDto));
  }
}