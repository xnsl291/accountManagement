package zb.accountMangement.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // user
    INVALID_NAME_FORMAT("입력값의 형식이 올바르지 않습니다."),

    DUPLICATED_PHONE_NUMBER("이미 등록된 핸드폰 번호입니다.")
    ;
    private final String description;
}
