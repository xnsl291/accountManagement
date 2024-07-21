package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.account.dto.AccountManagementDto;
import zb.accountMangement.account.service.AccountService;
import zb.accountMangement.common.auth.JwtToken;
import zb.accountMangement.common.auth.JwtTokenProvider;
import zb.accountMangement.common.exception.CustomException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.dto.SignInDto;
import zb.accountMangement.member.dto.SignUpDto;
import zb.accountMangement.member.model.entity.Member;
import zb.accountMangement.member.dto.UpdateUserDto;
import zb.accountMangement.member.repository.MemberRepository;
import zb.accountMangement.member.model.RoleType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final AccountService accountService;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * id를 이용한 회원 정보 열람
	 *
	 * @param userId - id
	 * @return Member
	 */
	public Member getUserById(long userId) {
		return memberRepository.findById(userId).orElseThrow(
				() -> new CustomException(ErrorCode.USER_NOT_EXIST));
	}

	/**
	 * 핸드폰번호를 이용한 회원 정보 열람
	 *
	 * @param phoneNumber - 핸드폰번호
	 * @return Member
	 */
	public Member getUserByPhoneNumber(String phoneNumber) {
		return memberRepository.findByPhoneNumber(phoneNumber).orElseThrow(
				() -> new CustomException(ErrorCode.USER_NOT_EXIST));
	}

	/**
	 * 회원 정보 수정
	 *
	 * @param updateUserDto - 사용자 정보수정 dto (이름, 핸드폰번호, 로그인 비밀번호)
	 * @return Member
	 */
	@Transactional
	public Member updateUserInfo(String token, long userId, UpdateUserDto updateUserDto) {
		Member member = checkMemberPermission(token, userId);
		member.update(updateUserDto);
		return member;
	}

	/**
	 * 회원 가입
	 * @param signUpDto - 회원 가입 dto (이름, 핸드폰 번호, 로그인 비밀번호, 초기 계좌 비밀번호)
	 */
	public Boolean signUp(SignUpDto signUpDto) {
		String phoneNumber = convert2NumericString(signUpDto.getPhoneNumber());

		// 이름 유효성 검사
		if (!signUpDto.getName().matches("[가-힣a-zA-Z0-9]{2,10}")) {
			throw new CustomException(ErrorCode.INVALID_NAME_FORMAT);
		}

		// 핸드폰번호 중복 검사
		if (getUserByPhoneNumber(phoneNumber) != null)
			throw new CustomException(ErrorCode.DUPLICATED_PHONE_NUMBER);

		// 저장
		Member member = Member.builder()
				.name(signUpDto.getName())
				.password(passwordEncoder.encode(signUpDto.getLoginPassword()))
				.phoneNumber(phoneNumber)
				.role(RoleType.USER)
				.build();

		// 초기 계좌 생성
		AccountManagementDto accountManagementDto = AccountManagementDto.builder()
				.nickname(null)
				.password(passwordEncoder.encode(signUpDto.getInitialAccountPassword()))
				.build();

		accountService.openAccount(member.getId(), accountManagementDto);
		memberRepository.save(member);
		return true;
	}

	/**
	 * 회원탈퇴
	 */
	@Transactional
	public Boolean deleteUser(String token) {
		Member member = checkMemberPermission(token, jwtTokenProvider.getId(token));
		member.delete();
		return true;
	}

	/**
	 * 로그인 기능
	 *
	 * @param signInDto - 로그인 dto (핸드폰번호, 로그인 PW)
	 * @return token - 토큰
	 */
	public JwtToken signIn(SignInDto signInDto) {
		Member member = getUserByPhoneNumber(convert2NumericString(signInDto.getPhoneNumber()));

		// 비밀번호 일치여부 확인
		if (!passwordEncoder.matches(signInDto.getPassword(), member.getPassword()))
			throw new CustomException(ErrorCode.UNMATCHED_PASSWORD);

		return jwtTokenProvider.generateToken(member.getId(), member.getPhoneNumber(), member.getRole());
	}

	/**
	 * 로그아웃
	 *
	 * @param token - 토큰
	 * @return true
	 */
	public Boolean signOut(String token) {
		boolean result = false;
		if (jwtTokenProvider.validateToken(token)) {
			String phoneNumber = jwtTokenProvider.getPhoneNumber(token);
			jwtTokenProvider.deleteToken(phoneNumber);
			result = true;
		}
		return result;
	}

	/**
	 * 문자와 숫자가 혼용된 문자열에서 숫자만 추출
	 *
	 * @param string - 변환하고자 하는 문자열
	 * @return 변환된 문자열
	 */
	private String convert2NumericString(String string) {
		String pattern = "[^0-9]";
		return string.replaceAll(pattern, "");
	}

	private boolean isMemberTokenMatch(String token, Member member){
		return jwtTokenProvider.getId(token).equals(member.getId());
	}

	private Member checkMemberPermission(String token, long memberId){
		Member member = getUserById(memberId);

		if(!isMemberTokenMatch(token, member))
			throw new CustomException(ErrorCode.UNMATCHED_USER);

		if (member.getRole().equals(RoleType.WITHDRAWN))
			throw new CustomException(ErrorCode.WITHDRAWN_USER);

		if (member.getRole().equals(RoleType.PENDING))
			throw new CustomException(ErrorCode.PENDING_USER);

		return member;
	}
}
