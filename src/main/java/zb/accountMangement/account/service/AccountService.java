package zb.accountMangement.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.account.domain.Account;
import zb.accountMangement.account.dto.AccountManagementDto;
import zb.accountMangement.account.dto.SearchAccountDto;
import zb.accountMangement.account.repository.AccountRepository;
import zb.accountMangement.account.type.AccountStatus;
import zb.accountMangement.common.error.exception.NotFoundAccountException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.service.MemberService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Transactional(readOnly = true)
public class AccountService {

    private final int ACCOUNT_NUMBER_LENGTH = 14;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberService memberService;
    /**
     * 계좌번호 생성
     * @return 계좌번호
     */
    private String createAccountNumber() {
      return RandomStringUtils.random(ACCOUNT_NUMBER_LENGTH, false, true);
  }

    /**
     * id를 이용한 계좌정보 열람
     * @param accountId - 계좌 ID
     * @return Account
     */
    public Account getAccountById(Long accountId){
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));
    }

    /**
     * 계좌번호를 이용한 계좌정보 열람
     * @param accountNumber - 계좌번호
     * @return Account
     */
    public Account getAccountByNumber(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));
    }

    /**
     * 계좌 개설
     * @param userId - 사용자 ID
     * @param accountManagementDto - 계좌 정보 dto (계좌별명, 계좌 PW)
     * @return Account
     */
    @Transactional
    public Account openAccount(Long userId, AccountManagementDto accountManagementDto) {
        // 계좌번호 랜덤생성 - 중복 생성 x
        String accountNumber = createAccountNumber();

        while(accountRepository.findByAccountNumber(accountNumber).isPresent()) {
            accountNumber = createAccountNumber();
        }

        Account account = Account.builder()
              .accountNumber(accountNumber)
              .userId(userId)
              .nickname(accountManagementDto.getNickname())
              .password(passwordEncoder.encode(accountManagementDto.getPassword()))
              .status(AccountStatus.EXISTS)
              .build();

        accountRepository.save(account);
        return account;
    }

    /**
     * 계좌 정보 조회 - accountStatus가 EXISTS, PENDING인 계좌 조회 가능
     * @param accountId - 계좌 ID
     * @return Account
     */
    public Account getAccountInfo(Long accountId) {
        Account account = getAccountById(accountId);
        if (account.isDeletedAccount())
            throw new NotFoundAccountException(ErrorCode.DELETED_ACCOUNT);
        return account;
    }

    /**
     * 계좌 정보 수정
     * @param accountId - 계좌 ID
     * @param accountManagementDto - 계좌 정보 dto (계좌별명, 계좌 PW)
     * @return Account
     */
    @Transactional
    public Account updateAccount(Long accountId, AccountManagementDto accountManagementDto) {
        Account account = getAccountById(accountId);

        if (account.isExistsAccount()) {
            account.setNickname(accountManagementDto.getNickname());
            account.setPassword(accountManagementDto.getPassword());
        }
        else if (account.isDeletedAccount())
            throw new NotFoundAccountException(ErrorCode.DELETED_ACCOUNT);
        else
            throw new NotFoundAccountException(ErrorCode.PENDING_ACCOUNT);

        return account;
    }

    /**
     * 계좌 해지
     * @param accountId - 계좌 ID
     * @return 성공여부
     */
    @Transactional
    public Boolean deleteAccount(Long accountId) {
        boolean result = false;
        Account account = getAccountById(accountId);

        List<Account> userAccounts = getAllAccounts(account.getUserId());

        // 사용자가 2개 이상의 계좌를 가지고 있어야 삭제 가능
        if( account.isExistsAccount() && userAccounts.size() >= 2 ) {
            account.setStatus(AccountStatus.DELETED);
            account.setDeletedAt(LocalDateTime.now());
            result = true;
        }

        return result;
    }

    // 전체 계좌 조회
    public List<Account> getAllAccounts(Long userId){
        return accountRepository.findByUserId(userId);
    }

    /**
     * 계좌 검색
     * @param accountId - 계좌 Id
     * @param requesterId - 요구자 ID
     * @return 계좌정보
     */
    public SearchAccountDto searchAccount(Long accountId, Long requesterId) {
        Account account = getAccountById(accountId);
        String username = memberService.getUserById(account.getUserId()).getName();

        // 본인의 계좌
        if (account.getUserId().equals(requesterId)) {
            return SearchAccountDto.builder()
                    .accountId(account.getId())
                    .accountNumber(account.getAccountNumber())
                    .ownerName(username)
                    .accountNickname(account.getNickname())
                    .balance(account.getBalance())
                    .status(account.getStatus())
                    .build();
        }
        // 타인의 계좌
        else {
            return SearchAccountDto.builder()
                    .accountId(account.getId())
                    .accountNumber(account.getAccountNumber())
                    .ownerName(username)
                    .build();
        }
    }
}
