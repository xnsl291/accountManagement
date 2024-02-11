package zb.accountMangement.common.exception;

import lombok.Getter;
import zb.accountMangement.common.type.ErrorCode;

@Getter
public class UnauthorizedMemberAccessException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedMemberAccessException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = getErrorCode();
    }
}