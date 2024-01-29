package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zb.accountMangement.common.exception.DuplicatedInfoException;
import zb.accountMangement.common.exception.InvalidInputException;
import zb.accountMangement.member.domain.Member;
import zb.accountMangement.member.dto.SignUpDto;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final MemberRepository memberRepository;
  private final SendMessageService sendMessageService;
//  private PasswordEncoder passwordEncoder;

  public long signUp(SignUpDto dto){

    // 이름 유효성 검사
    if (!dto.getName().matches("[가-힣a-zA-Z0-9]{2,10}")) {
      throw new InvalidInputException(ErrorCode.INVALID_NAME_FORMAT);
    }

    // 핸드폰번호 중복 검사
    this.memberRepository.findByPhoneNumber(dto.getPhoneNumber())
        .ifPresent(m -> {
          throw new DuplicatedInfoException(ErrorCode.DUPLICATED_PHONE_NUMBER);
        });

    // 비밀번호 암호화
//   String encodedPassword = passwordEncoder.encode(dto.getPassword());

    // 핸드폰 인증번호 발송
    sendMessageService.sendVerificationMessage(dto.getPhoneNumber());

    // 저장
    Member member = Member.builder()
        .name(dto.getName())
        .password(dto.getPassword())
//        .password(encodedPassword)
        .phoneNumber(dto.getName())
        .build();

    this.memberRepository.save(member);
    return member.getId();
  }

}
