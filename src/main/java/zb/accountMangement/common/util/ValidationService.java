package zb.accountMangement.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zb.accountMangement.account.model.entity.Account;
import zb.accountMangement.account.service.AccountService;
import zb.accountMangement.common.auth.JwtTokenProvider;
import zb.accountMangement.common.error.exception.NotFoundUserException;
import zb.accountMangement.common.error.exception.UnauthorizedAccessException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor

public class ValidationService {
    private final AccountService accountService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;

    public void validTokenNAccountOwner(String token, Long accountId){
        Account account = accountService.getAccountInfo(accountId);
        Long userId = tokenProvider.getId(token);
        if (!account.getUserId().equals(userId))
            throw new UnauthorizedAccessException(ErrorCode.MISMATCH_ACCOUNT_OWNER);
    }
    public void validTokenNUserId(String token, Long userId){
        Long tokenProviderId = tokenProvider.getId(token);
        if (!tokenProviderId.equals(userId))
            throw new UnauthorizedAccessException(ErrorCode.MISMATCHED_USER_ID);
    }

    public void validTokenNUserPhoneNumber(String token, String phoneNum){
        Long userId = memberRepository.findByPhoneNumber(phoneNum)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST)).getId();
        validTokenNUserId(token, userId);
    }
}
