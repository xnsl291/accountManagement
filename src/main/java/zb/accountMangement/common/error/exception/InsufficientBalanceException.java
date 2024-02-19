package zb.accountMangement.common.error.exception;

import lombok.Getter;
import zb.accountMangement.common.type.ErrorCode;

@Getter
public class InsufficientBalanceException extends RuntimeException {

    private final ErrorCode errorCode;

    public InsufficientBalanceException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = getErrorCode();
    }
}