package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.account.dto.AccountManagementDto;
import zb.accountMangement.account.service.AccountService;
import zb.accountMangement.common.auth.JwtToken;
import zb.accountMangement.common.auth.JwtTokenProvider;
import zb.accountMangement.common.error.exception.*;
import zb.accountMangement.common.util.RedisUtil;
import zb.accountMangement.member.domain.Member;
import zb.accountMangement.member.dto.*;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.repository.MemberRepository;
import zb.accountMangement.member.type.RoleType;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final SendMessageService sendMessageService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final AccountService accountService;
  private final RedisUtil redisUtil;

  /**
   * 회원가입
   * @param signUpDto
   */
  @Transactional
  public void signUp(SignUpDto signUpDto){
    String phoneNumber = convert2NumericString(signUpDto.getPhoneNumber());

    // 이름 유효성 검사
    if (!signUpDto.getName().matches("[가-힣a-zA-Z0-9]{2,10}")) {
      throw new InvalidInputException(ErrorCode.INVALID_NAME_FORMAT);
    }

    // 핸드폰번호 중복 검사
    memberRepository.findByPhoneNumber(phoneNumber)
        .ifPresent(m -> {
          throw new DuplicatedInfoException(ErrorCode.DUPLICATED_PHONE_NUMBER);
        });

    // 핸드폰 인증번호 발송
    sendMessageService.sendVerificationMessage(phoneNumber);

    // 저장
    Member member = Member.builder()
        .name(signUpDto.getName())
        .password(passwordEncoder.encode(signUpDto.getPassword()))
        .phoneNumber(phoneNumber)
        .build();

    // 초기 계좌 생성
    AccountManagementDto accountManagementDto = AccountManagementDto.builder()
            .nickname(null)
            .password(passwordEncoder.encode(signUpDto.getInitialAccountPassword()))
            .build();

    accountService.openAccount(member.getId(), accountManagementDto);
    memberRepository.save(member);
  }

  /**
   * 회원탈퇴
   * @param userId - id
   */
  public String deleteUser(long userId){
    Member member = memberRepository.findById(userId).orElseThrow(
            () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

    member.setRole(RoleType.WITHDRAWN);
    member.setDeletedAt(LocalDateTime.now());
    return "회원탈퇴완료";
  }

  /**
   * 비밀번호 재설정 요청
   * @param userId - id
   * @param findUserInfoDto
   * @return "인증 메세지 발송 완료"
   */
  public String requestResetPw(Long userId, FindUserInfoDto findUserInfoDto) {
      Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

      Member dtoMember = memberRepository.findByPhoneNumber(findUserInfoDto.getPhone())
          .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

      if (!member.getId().equals(dtoMember.getId())) {
        throw new UnmatchedUserException(ErrorCode.UNMATCHED_USER);
      }

      return sendMessageService.sendVerificationMessage(findUserInfoDto.getPhone());
  }

  /**
   * 비밀번호 재설정
   * @param userId - id
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
      throw new UnmatchedUserException(ErrorCode.UNMATCHED_USER);


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

  /**
   * 로그인 기능
   * @param signInDto
   * @return token - 토큰
   */
  public JwtToken signIn(SignInDto signInDto) {
    Member member = memberRepository.findByPhoneNumber(convert2NumericString(signInDto.getPhoneNumber()))
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

    // 비밀번호 일치여부 확인
    if (!passwordEncoder.matches(signInDto.getPassword(), member.getPassword()))
      throw new UnmatchedPasswordException(ErrorCode.UNMATCHED_PASSWORD);

    if(member.getRole().equals(RoleType.WITHDRAWN))
      throw new NotFoundUserException(ErrorCode.WITHDRAWN_USER);
    if(member.getRole().equals(RoleType.PENDING))
      throw new NotFoundUserException(ErrorCode.PENDING_USER);

    String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getPhoneNumber(), member.getRole());
    String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId(),member.getPhoneNumber(), member.getRole());

    jwtTokenProvider.saveRefreshToken(member.getPhoneNumber(), refreshToken);
    return new JwtToken(accessToken,refreshToken);
  }

  /**
   * 로그아웃
   * @param token - 토큰
   * return "로그아웃 완료"
   */
  public String signOut(String token) {

    if (jwtTokenProvider.validateToken(token)) {
      String phoneNumber = jwtTokenProvider.getPhoneNumber(token);

      if (redisUtil.getData(phoneNumber) != null) {
        redisUtil.deleteData(phoneNumber);
      }
      jwtTokenProvider.deleteToken(phoneNumber);
    }
    return "로그아웃 완료";
  }

  /**
   * 문자와 숫자가 혼용된 문자열에서 숫자만 추출
   * @param string - 변환하고자 하는 문자열
   * @return 변환된 문자열
   */
  public String convert2NumericString(String string){
    String pattern = "[^0-9]";
    return string.replaceAll(pattern,"");
  }
}
