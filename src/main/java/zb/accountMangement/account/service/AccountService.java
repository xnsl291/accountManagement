package zb.accountMangement.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.account.domain.Account;
import zb.accountMangement.account.dto.OpenAccountDto;
import zb.accountMangement.account.repository.AccountRepository;
import zb.accountMangement.account.type.AccountStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

  private final AccountRepository accountRepository;
  private final int ACCOUNT_NUMBER_LENGTH = 14;
  private final BCryptPasswordEncoder passwordEncoder;


    private String createAccountNumber() {
      return RandomStringUtils.random(ACCOUNT_NUMBER_LENGTH, false, true);
  }

  @Transactional
  public Account openAccount(Long userId, OpenAccountDto openAccountDto) {

      // 계좌번호 랜덤생성 - 중복 생성 x
      String accountNumber = createAccountNumber();

      while(accountRepository.findByAccountNumber(accountNumber).isPresent()) {
        accountNumber = createAccountNumber();
      }

      Account account = Account.builder()
              .accountNumber(accountNumber)
              .userId(userId)
              .nickname(openAccountDto.getNickname())
              .password(passwordEncoder.encode(openAccountDto.getPassword()))
              .status(AccountStatus.EXISTS)
              .build();

      accountRepository.save(account);
      return account;
  }
}
