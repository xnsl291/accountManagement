package zb.accountMangement.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //jwt
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),
    FAILED_VALIDATION("인증에 실패하였습니다"),

    // user
    INVALID_NAME_FORMAT("입력값의 형식이 올바르지 않습니다."),
    USER_NOT_EXIST("해당 사용자가 존재하지 않습니다."),
    WITHDRAWN_USER("탈퇴한 사용자입니다."),
    PENDING_USER("사용이 정지된 계정입니다."),
    DUPLICATED_PHONE_NUMBER("이미 등록된 핸드폰 번호입니다."),

    UNMATCHED_USER("사용자 정보가 일치하지 않습니다."),
    UNMATCHED_PASSWORD("비밀번호가 일치하지 않습니다."),
    UNMATCHED_VERIFICATION_CODE("인증 코드가 일치하지 않습니다."),
    MISMATCHED_USER_ID("토큰정보와 사용자의 정보가 일치하지 않습니다"),

    //account
    ACCOUNT_NOT_EXIST("계좌 정보가 존재하지 않습니다."),
    DELETED_ACCOUNT("삭제된 계좌입니다."),
    PENDING_ACCOUNT("거래정지된 계좌입니다."),
    EXCEED_BALANCE("인출금액은 잔액보다 클 수 없습니다."),
    INVALID_ACCOUNT("거래할 수 없는 계좌 입니다."),

    MISMATCH_ACCOUNT_OWNER("계좌 소유자와 유저 정보가 일치하지 않습니다.")
    ;
    private final String description;
}
