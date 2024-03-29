package zb.accountMangement.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.common.service.ValidationService;
import zb.accountMangement.member.model.entity.Member;
import zb.accountMangement.member.dto.UpdateUserDto;
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
   * @param userId - id
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
   * @param userId - id
   * @param updateUserDto - 사용자 정보수정 dto (이름, 핸드폰번호, 로그인 PW)
   * @return "수정완료"
   */
  @PatchMapping("/{user_id}")
  public ResponseEntity<Member> updateUserInfo(
      @RequestHeader(value = "Authorization") String token,
      @PathVariable("user_id") @Min(1) long userId,
      @RequestBody @Valid UpdateUserDto updateUserDto){
    validationService.validTokenNUserId(token,userId);
    return ResponseEntity.ok().body(memberService.updateUserInfo(userId, updateUserDto));
  }
}