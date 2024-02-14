package zb.accountMangement.account.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.account.dto.TransactionDto;
import zb.accountMangement.account.service.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
  private final TransactionService transactionService;

  /**
   * 계좌 소유주 확인
   * @param accountNumber - 계좌번호
   * @return 사용자이름
   */
  @GetMapping("/validation/{account_number}")
  public ResponseEntity<String> validateRecipient(@PathVariable("account_number")String accountNumber) {
    return ResponseEntity.ok().body(transactionService.validateRecipient(accountNumber));
  }

  /**
   * 입금
   * @param depositDto - TransactionDto
   * @return "입금완료"
   */
  @PostMapping("/deposit")
  public ResponseEntity<String> deposit(@RequestBody TransactionDto depositDto) {
    return ResponseEntity.ok().body(transactionService.deposit(depositDto));
  }

  /**
   * 출금
   * @param withdrawalDto - TransactionDto
   * @return "출금완료"
   */
  @PostMapping("/withdrawal")
  public ResponseEntity<String> withdrawal(@RequestBody TransactionDto withdrawalDto) {
    return ResponseEntity.ok().body(transactionService.withdrawal(withdrawalDto));
  }

  /**
   * 송금
   * @param transferDto - TransactionDto
   * @return "송금완료"
   */
  @PostMapping("{sender_account_id}/transfer/{receiver_account_id}")
  public ResponseEntity<String> transfer(
          @PathVariable("sender_account_id") Long senderAccountId ,
          @PathVariable("receiver_account_id") Long receiverAccountId,
          @RequestBody TransactionDto transferDto) {
    return ResponseEntity.ok().body(transactionService.transfer(senderAccountId, receiverAccountId,transferDto));
  }

  /**
   * 거래내역 조회
   * @param accountId - 계좌 ID
   * @return 거래내역 리스트
   */
  @GetMapping("/{accountId}/history")
  public ResponseEntity<List<TransactionDto>> getTransactionsByAccountId(@PathVariable Long accountId) {
    return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
  }
}
