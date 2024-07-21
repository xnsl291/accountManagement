package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.common.auth.JwtTokenProvider;
import zb.accountMangement.common.exception.CustomException;
import zb.accountMangement.common.service.RedisService;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.dto.*;
import zb.accountMangement.member.model.entity.Member;
import zb.accountMangement.member.repository.MemberRepository;
import zb.accountMangement.member.model.RoleType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;
	private final SendMessageService sendMessageService;
	private final RedisService redisService;
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
	 * 비밀번호 재설정 요청
	 *
	 * @param token           - 토큰
	 * @param findUserInfoDto - 회원정보 조회 dto (이름, 핸드폰번호)
	 * @return "인증 메세지 발송 완료"
	 */
	public String requestResetPw(String token, Long userId, FindUserInfoDto findUserInfoDto) {
		checkMemberPermission(token, userId);
		return sendMessageService.sendVerificationMessage(token, findUserInfoDto.getPhoneNumber());
	}

	/**
	 * 비밀번호 재설정
	 *
	 * @param resetPwDto - 비밀번호 재설정 dto (인증번호, 새로운 PW)
	 * @return "비밀번호 재설정 완료"
	 */
	@Transactional
	public String verifyResetPw(String token, Long userId, ResetPwDto resetPwDto) {
		Member member = getUserById(userId);
		SmsVerificationDto info = redisService.getMsgVerificationInfo(token);

		if (info == null)
			throw new CustomException(ErrorCode.USER_NOT_EXIST);

		if (!member.getPhoneNumber().equals(info.getPhoneNumber()))
			throw new CustomException(ErrorCode.UNMATCHED_USER);

		// 핸드폰 인증 번호가 같으면
		if (info.getVerificationCode().equals(resetPwDto.getInputCode())) {
			member.setPassword(resetPwDto.getNewPassword());

			// 인증 정보 삭제
			redisService.deleteMsgVerificationInfo(token);
		} else
			throw new CustomException(ErrorCode.UNMATCHED_VERIFICATION_CODE);

		return "비밀번호 재설정 완료";
	}


	private boolean isMemberTokenMatch(String token, Member member){
		return jwtTokenProvider.getId(token).equals(member.getId());
	}

	public Member checkMemberPermission(String token, long memberId){
		Member member = getUserById(memberId);

		if(!isMemberTokenMatch(token, member))
			throw new CustomException(ErrorCode.UNMATCHED_USER);

		if (member.getRole().equals(RoleType.WITHDRAWN))
			throw new CustomException(ErrorCode.WITHDRAWN_USER);

		if (member.getRole().equals(RoleType.PENDING))
			throw new CustomException(ErrorCode.PENDING_USER);

		return member;
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
}
