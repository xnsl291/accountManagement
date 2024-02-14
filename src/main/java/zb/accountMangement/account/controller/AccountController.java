package zb.accountMangement.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.account.domain.Account;
import zb.accountMangement.account.dto.AccountManagementDto;
import zb.accountMangement.account.service.AccountService;
import zb.accountMangement.common.auth.JwtTokenProvider;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
  private final AccountService accountService;
  private final JwtTokenProvider tokenProvider;

  /**
   * 계좌개설
   * @param token - 토큰
   * @param accountManagementDto
   * @return Account
   */
  @PostMapping("/open")
  public ResponseEntity<Account> openAccount(
          @RequestHeader(value = "Authorization") String token,
          @RequestBody AccountManagementDto accountManagementDto){
    return ResponseEntity.ok().body(accountService.openAccount(tokenProvider.getId(token), accountManagementDto));
  }

  /**
   * 계좌 정보 조회
   * @param accountId - 계좌 ID
   * @return Account
   */
  @GetMapping("/{account_id}")
  public ResponseEntity<Account> getAccountInfo(@PathVariable("account_id") Long accountId){
    return ResponseEntity.ok().body(accountService.getAccountInfo(accountId));
  }

  /**
   * 계좌 수정
   * @param accountId - 계좌 ID
   * @return Account
   */
    @PatchMapping("/{account_id}")
    public ResponseEntity<Account> updateAccountInfo(
              @PathVariable("account_id") Long accountId,
              @RequestBody AccountManagementDto accountManagementDto){
      return ResponseEntity.ok().body(accountService.updateAccount(accountId,accountManagementDto));
    }

  /**
   * 계좌 해지
   * @param accountId - 계좌 ID
   * @return 성공여부
   */
  @DeleteMapping("/{account_id}")
  public ResponseEntity<Boolean> deleteAccountInfo(@PathVariable("account_id")Long accountId){
    return ResponseEntity.ok().body(accountService.deleteAccount(accountId));
  }

  /**
   * 사용자가 소유한 전체계좌조회
   * @param userId - 사용자 ID
   * @return 사용자가 소유한 계좌 리스트
   */
  @GetMapping("/{user_id}")
  public ResponseEntity<List<Account>> getAllAccounts(@PathVariable("user_id")Long userId){
    return ResponseEntity.ok().body(accountService.getAllAccounts(userId));
  }
}
