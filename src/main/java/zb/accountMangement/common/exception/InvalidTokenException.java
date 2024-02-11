package zb.accountMangement.common.exception;

import lombok.Getter;
import zb.accountMangement.common.type.ErrorCode;

@Getter
public class InvalidTokenException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = getErrorCode();
    }
}