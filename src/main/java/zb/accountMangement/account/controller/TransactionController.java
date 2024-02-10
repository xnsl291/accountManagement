package zb.accountMangement.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.account.service.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
  private final TransactionService transactionService;

///  계좌 소유주 확인
  @GetMapping("/validateRecipient/{account_number}")
  public ResponseEntity<String> validateRecipient(@PathVariable("account_number")String accountNumber) {
    return ResponseEntity.ok().body(transactionService.validateRecipient(accountNumber));
  }
  // 거래내역 조회
  // 입금
  // 출금
  // 송금



}
