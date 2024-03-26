package zb.accountMangement.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zb.accountMangement.account.domain.Account;
import zb.accountMangement.account.domain.Transaction;
import zb.accountMangement.account.dto.TransactionDto;
import zb.accountMangement.account.repository.AccountRepository;
import zb.accountMangement.account.repository.TransactionRepository;
import zb.accountMangement.account.type.TransactionType;
import zb.accountMangement.common.exception.OverdrawException;
import zb.accountMangement.common.exception.NotFoundAccountException;
import zb.accountMangement.common.exception.NotFoundUserException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.member.domain.Member;
import zb.accountMangement.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
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
                () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST)).getName();

        return "";
    }

    public String deposit(TransactionDto depositDto) {
        Account account = accountRepository.findById(depositDto.getAccountId())
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));
        if (accountService.isExistAccount(account.getId())) {
            Transaction transaction = Transaction.builder()
                    .accountId(depositDto.getAccountId())
                    .type(TransactionType.DEPOSIT)
                    .amount(depositDto.getAmount())
                    .name("전자입금")
                    .memo(depositDto.getMemo())
                    .build();

            transactionRepository.save(transaction);
            account.setAmount(account.getAmount() + depositDto.getAmount());
            return "입금완료";
        }
        return "입금실페";
    }

    public String withdrawal(TransactionDto withdrawalDto) {

        Account account = accountRepository.findById(withdrawalDto.getAccountId())
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        if (account.getAmount() < withdrawalDto.getAmount())
            throw new OverdrawException(ErrorCode.EXCEED_BALANCE);

        if (accountService.isExistAccount(account.getId())) {
            Transaction transaction = Transaction.builder()
                    .accountId(withdrawalDto.getAccountId())
                    .type(TransactionType.WITHDRAWN)
                    .amount(withdrawalDto.getAmount())
                    .name("전자출금")
                    .memo(withdrawalDto.getMemo())
                    .build();
            transactionRepository.save(transaction);
            account.setAmount(account.getAmount()-withdrawalDto.getAmount());
            return "출금완료";
        }
        return "출금실패";
    }

    public String transfer(TransactionDto transferDto) {

        Account senderAccount = accountRepository.findById(transferDto.getAccountId())
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));
        Account recipientAccount = accountRepository.findById(transferDto.getAccountId())
                        .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        Member sender = memberRepository.findById(senderAccount.getUserId())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        Member receiver = memberRepository.findById(recipientAccount.getUserId())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

        if (senderAccount.getAmount() < transferDto.getAmount())
            throw new OverdrawException(ErrorCode.EXCEED_BALANCE);

        if (accountService.isExistAccount(senderAccount.getId()) && accountService.isExistAccount(recipientAccount.getId()) ) {
            Transaction senderTransaction = Transaction.builder()
                    .accountId(transferDto.getAccountId())
                    .type(TransactionType.TRANSFER)
                    .amount(-transferDto.getAmount())
                    .name(receiver.getName()) // 수신자 이름
                    .memo(transferDto.getMemo())
                    .build();
            transactionRepository.save(senderTransaction);

            Transaction recipientTransaction = Transaction.builder()
                    .accountId(transferDto.getAccountId())
                    .type(TransactionType.TRANSFER)
                    .amount(transferDto.getAmount())
                    .name(sender.getName()) // 발신자 이름???
                    .memo(transferDto.getMemo())
                    .build();
            transactionRepository.save(recipientTransaction);

            senderAccount.setAmount(senderAccount.getAmount() - transferDto.getAmount());
            recipientAccount.setAmount(recipientAccount.getAmount() + transferDto.getAmount());
            return "송금완료";
        }
        return "송금실패";
    }

    /**
     * 거래내역 조회
     * @param accountId - 계좌 ID
     * @return 거래내역 리스트
     */
    public List<TransactionDto> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .map(this::mapToTransactionDto)
                .collect(Collectors.toList());
    }

    /**
     * transaction 을 transactionDto로 매핑
     * @param transaction - 거래
     * @return TransactionDto
     */
    private TransactionDto mapToTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .name(transaction.getName())
                .memo(transaction.getMemo())
                .TransactedAt(transaction.getTransactedAt())
                .build();
    }
}
