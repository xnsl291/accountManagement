package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.domain.Member;
import zb.accountMangement.member.dto.UpdateUserDto;
import zb.accountMangement.common.exception.NotFoundUserException;
import zb.accountMangement.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  /**
   * 회원 정보 열람
   * @param userId - id
   * @return Member
   */
  public Member getUserInfo(long userId) {
    return memberRepository.findById(userId).orElseThrow(
        () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
  }

  /**
   * 회원 정보 수정
   * @param userId - id
   * @param updateUserDto
   * @return Member
   */
  public Member updateUserInfo(long userId, UpdateUserDto updateUserDto) {
    Member member = memberRepository.findById(userId).orElseThrow(
        () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

      member.setName(updateUserDto.getName());
      member.setPassword(updateUserDto.getPassword());
      member.setPhoneNumber(updateUserDto.getPhoneNumber());
      memberRepository.save(member);
      return member;
  }
}
