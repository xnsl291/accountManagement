package zb.accountMangement.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zb.accountMangement.account.domain.Account;
import zb.accountMangement.account.repository.AccountRepository;
import zb.accountMangement.common.exception.NotFoundAccountException;
import zb.accountMangement.common.exception.NotFoundUserException;
import zb.accountMangement.common.type.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final AccountService accountService;

    /**
     * 계좌 소유주 확인
     * @param accountNumber - 계좌번호
     * @return 사용자 이름
     */
    public String validateRecipient(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        if (accountService.isExistAccount(account.getId()))
            return memberRepository.findById(account.getUserId()).orElseThrow(
                () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST)).getName;

        return "";
    }

    // 입금
    // 출금
    // 송금
    // 거래내역 조회
}
