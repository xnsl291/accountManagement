package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import zb.accountMangement.common.exception.DuplicatedInfoException;
import zb.accountMangement.common.exception.InvalidInputException;
import zb.accountMangement.common.util.RedisUtil;
import zb.accountMangement.member.domain.Member;
import zb.accountMangement.member.dto.FindUserInfoDto;
import zb.accountMangement.member.dto.ResetPwDto;
import zb.accountMangement.member.dto.SignUpDto;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.dto.SmsVerificationDto;
import zb.accountMangement.member.exception.NotFoundUserException;
import zb.accountMangement.member.exception.UnmatchedCodeException;
import zb.accountMangement.member.exception.UnmatchedUserException;
import zb.accountMangement.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final MemberRepository memberRepository;
  private final SendMessageService sendMessageService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final RedisUtil redisUtil;

  /**
   * 회원가입
   * @param signUpDto
   */
  public void signUp(SignUpDto signUpDto){

    // 이름 유효성 검사
    if (!signUpDto.getName().matches("[가-힣a-zA-Z0-9]{2,10}")) {
      throw new InvalidInputException(ErrorCode.INVALID_NAME_FORMAT);
    }

    // 핸드폰번호 중복 검사
    memberRepository.findByPhoneNumber(signUpDto.getPhoneNumber())
        .ifPresent(m -> {
          throw new DuplicatedInfoException(ErrorCode.DUPLICATED_PHONE_NUMBER);
        });

    // 핸드폰 인증번호 발송
    sendMessageService.sendVerificationMessage(signUpDto.getPhoneNumber());

    // 저장
    Member member = Member.builder()
        .name(signUpDto.getName())
        .password(passwordEncoder.encode(signUpDto.getPassword()))
        .phoneNumber(signUpDto.getPhoneNumber())
        .build();

    memberRepository.save(member);
  }

  /**
   * 비밀번호 재설정 요청
   * @param userId
   * @param findUserInfoDto
   * @return "인증 메세지 발송 완료"
   */
  public String requestResetPw(Long userId, FindUserInfoDto findUserInfoDto) {
      Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

      Member dtoMember = memberRepository.findByPhoneNumber(findUserInfoDto.getPhone())
          .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

      if (!member.getId().equals(dtoMember.getId())) {
        throw new UnmatchedUserException(ErrorCode.USER_UNMATCHED);
      }

      return sendMessageService.sendVerificationMessage(findUserInfoDto.getPhone());
  }

  /**
   * 비밀번호 재설정
   * @param userId
   * @param resetPwDto
   * @return "비밀번호 재설정 완료"
   */
  public String verifyResetPw(Long userId, ResetPwDto resetPwDto) {
    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

    SmsVerificationDto info = redisUtil.getMsgVerificationInfo(resetPwDto.getToken());

    if (info == null)
      throw new NotFoundUserException(ErrorCode.USER_NOT_EXIST);

    if (!member.getPhoneNumber().equals(info.getPhoneNumber()))
      throw new UnmatchedUserException(ErrorCode.USER_UNMATCHED);


    // 핸드폰 인증 번호가 같으면
    if (info.getVerificationCode().equals(resetPwDto.getInputCode())) {
      member.setPassword(resetPwDto.getNewPassword());
      memberRepository.save(member);

      // 인증 정보 삭제
      redisUtil.deleteMsgVerificationInfo(resetPwDto.getToken());
    } else
      throw new UnmatchedCodeException(ErrorCode.UNMATCHED_VERIFICATION_CODE);

    return "비밀번호 재설정 완료";
  }
}
