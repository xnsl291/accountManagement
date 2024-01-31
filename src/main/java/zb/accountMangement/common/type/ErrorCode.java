package zb.accountMangement.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // user
    INVALID_NAME_FORMAT("입력값의 형식이 올바르지 않습니다."),
    USER_NOT_EXIST("해당 사용자가 존재하지 않습니다"),
    USER_UNMATCHED("사용자 정보가 일치하지 않습니다"),
    DUPLICATED_PHONE_NUMBER("이미 등록된 핸드폰 번호입니다."),

    UNMATCHED_VERIFICATION_CODE("인증 코드가 일치하지 않습니다.")
    ;
    private final String description;
}
