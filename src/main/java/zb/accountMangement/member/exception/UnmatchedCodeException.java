package zb.accountMangement.member.exception;

import lombok.Getter;
import zb.accountMangement.common.type.ErrorCode;

@Getter
public class UnmatchedCodeException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnmatchedCodeException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = getErrorCode();
    }
}