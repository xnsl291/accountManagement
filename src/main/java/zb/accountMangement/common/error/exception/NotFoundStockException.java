package zb.accountMangement.common.error.exception;

import lombok.Getter;
import zb.accountMangement.common.type.ErrorCode;

@Getter
public class NotFoundStockException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundStockException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = getErrorCode();
    }
}