package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import zb.accountMangement.common.exception.DuplicatedInfoException;
import zb.accountMangement.common.exception.InvalidInputException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.common.util.RedisUtil;
import zb.accountMangement.member.domain.Member;
import zb.accountMangement.member.dto.FindUserInfoDto;
import zb.accountMangement.member.dto.ResetPwDto;
import zb.accountMangement.member.dto.SignUpDto;
import zb.accountMangement.member.dto.SmsVerificationDto;
import zb.accountMangement.member.dto.UpdateUserDto;
import zb.accountMangement.member.exception.NotFoundUserException;
import zb.accountMangement.member.exception.UnmatchedCodeException;
import zb.accountMangement.member.exception.UnmatchedUserException;
import zb.accountMangement.member.repository.MemberRepository;
import zb.accountMangement.member.type.RoleType;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  /**
   * 회원 정보 열람
   * @param userId
   * @return Member
   */
  public Member getUserInfo(long userId) {
    return memberRepository.findById(userId).orElseThrow(
        () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
  }

  /**
   * 회원 정보 수정
   * @param userId
   * @param updateUserDto
   * @return Member
   */
  public Member updateUserInfo(long userId, UpdateUserDto updateUserDto) {
    Member member = memberRepository.findById(userId).orElseThrow(
        () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

    // TODO : 바뀐 부분만 UPDATE 하기
    member.setName(updateUserDto.getName());
    member.setPassword(updateUserDto.getPassword());
    member.setPhoneNumber((updateUserDto.getPhoneNumber()));

    memberRepository.save(member);

    return member;
  }

  // 회원 탈퇴
  public long deleteUserInfo(long userId){
    Member member = memberRepository.findById(userId).orElseThrow(
        () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

    member.setRole(RoleType.DELETED_USER);
    return userId;
  }
}
