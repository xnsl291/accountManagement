package zb.accountMangement.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.common.exception.CustomException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.model.entity.Member;
import zb.accountMangement.member.dto.UpdateUserDto;
import zb.accountMangement.member.repository.MemberRepository;
import zb.accountMangement.member.model.RoleType;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;

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
	 * @param userId        - id
	 * @param updateUserDto - 사용자 정보수정 dto (이름, 핸드폰번호, 로그인 PW)
	 * @return Member
	 */
	@Transactional
	public Member updateUserInfo(long userId, UpdateUserDto updateUserDto) {
		Member member = getUserById(userId);
		member.setName(updateUserDto.getName());
		member.setPassword(updateUserDto.getPassword());
		member.setPhoneNumber(updateUserDto.getPhoneNumber());
		return member;
	}

	/**
	 * 회원탈퇴
	 *
	 * @param userId - id
	 */
	@Transactional
	public String deleteUser(long userId) {
		Member member = memberRepository.findById(userId).orElseThrow(
				() -> new CustomException(ErrorCode.USER_NOT_EXIST));

		member.setRole(RoleType.WITHDRAWN);
		member.setDeletedAt(LocalDateTime.now());
		return "회원탈퇴완료";
	}
}
