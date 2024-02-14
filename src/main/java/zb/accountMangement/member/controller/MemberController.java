package zb.accountMangement.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.member.domain.Member;
import zb.accountMangement.member.dto.UpdateUserDto;
import zb.accountMangement.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
  private final MemberService memberService;

  // TODO : 토큰 발급 이후 회원을 토큰으로 확인하는 방식으로 변경
  /**
   * 회원 정보 열람 기능
   * @param userId - id
   * @return Member
   */
  @GetMapping("/{user_id}")
  public ResponseEntity<Member> getUserInfo(@PathVariable("user_id") long userId  ){
    return ResponseEntity.ok().body(memberService.getUserInfo(userId));
  }

  /**
   * 회원 정보 수정
   * @param userId - id
   * @param updateUserDto - 수정할 정보
   * @return "수정완료"
   */
  @PatchMapping("/{user_id}")
  public ResponseEntity<Member> updateUserInfo(
      @PathVariable("user_id") long userId,
      @RequestBody UpdateUserDto updateUserDto){
    return ResponseEntity.ok().body(memberService.updateUserInfo(userId, updateUserDto));
  }
}
