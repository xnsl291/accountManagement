package zb.accountMangement.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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

  public Account openAccount(OpenAccountDto openAccountDto) {

      // 계좌번호 랜덤생성 - 중복 생성 x
      String accountNumber = createAccountNumber();

      while(accountRepository.findByAccountNumber(accountNumber).isPresent()) {
        accountNumber = createAccountNumber();
      }


      Account account = new Account();

      account.setAccountNumber(accountNumber);
      account.setNickname(openAccountDto.getNickname());
      account.setPassword(passwordEncoder.encode(openAccountDto.getPassword()));
      account.setStatus(AccountStatus.EXISTS);

      accountRepository.save(account);
      return account;

  }


}
