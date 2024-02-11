package zb.accountMangement.common.exception;

import lombok.Getter;
import zb.accountMangement.common.type.ErrorCode;

@Getter
public class InvalidInputException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = getErrorCode();
    }
}