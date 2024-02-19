package zb.accountMangement.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zb.accountMangement.common.error.exception.*;
import zb.accountMangement.common.type.ErrorCode;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(DuplicatedInfoException.class)
    public ResponseEntity<ErrorCode> handleDuplicatedInfoException(DuplicatedInfoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorCode> handleInvalidInputException(InvalidInputException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorCode> handleInvalidTokenException(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getErrorCode());
    }

    @ExceptionHandler(NotFoundAccountException.class)
    public ResponseEntity<ErrorCode> handleNotFoundAccountException(NotFoundAccountException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus httpStatus;
        switch (errorCode) {
            case ACCOUNT_NOT_EXIST:
                httpStatus = HttpStatus.NOT_FOUND;
                break;
            case PENDING_ACCOUNT:
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            case DELETED_ACCOUNT:
                httpStatus = HttpStatus.FORBIDDEN;
                break;
            default:
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
        }
        return ResponseEntity.status(httpStatus).body(errorCode);
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ErrorCode> handleNotFoundUserException(NotFoundUserException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus httpStatus;
        switch (errorCode) {
            case USER_NOT_EXIST:
                httpStatus = HttpStatus.NOT_FOUND;
                break;
            case PENDING_USER:
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            case WITHDRAWN_USER:
                httpStatus = HttpStatus.FORBIDDEN;
                break;
            default:
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
        }
        return ResponseEntity.status(httpStatus).body(errorCode);
    }

    @ExceptionHandler(NotFoundStockException.class)
    public ResponseEntity<ErrorCode> handleNotFoundStockException(NotFoundStockException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getErrorCode());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorCode> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorCode> handleInsufficientStockException(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode());
    }

    @ExceptionHandler(UnmatchedCodeException.class)
    public ResponseEntity<ErrorCode> handleUnmatchedCodeException(UnmatchedCodeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode());
    }

    @ExceptionHandler(UnmatchedPasswordException.class)
    public ResponseEntity<ErrorCode> handleUnmatchedPasswordException(UnmatchedPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode());
    }

    @ExceptionHandler(UnmatchedUserException.class)
    public ResponseEntity<ErrorCode> handleUnmatchedUserException(UnmatchedUserException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getErrorCode());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorCode> handleUnauthorizedMemberAccessException(UnauthorizedAccessException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getErrorCode());
    }
}
