package zb.accountMangement.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.account.domain.Account;
import zb.accountMangement.account.domain.Transaction;
import zb.accountMangement.account.dto.TransactionDto;
import zb.accountMangement.account.repository.AccountRepository;
import zb.accountMangement.account.repository.TransactionRepository;
import zb.accountMangement.account.type.TransactionType;
import zb.accountMangement.common.exception.InvalidAccountException;
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
@Transactional(readOnly = true)
@Slf4j
public class TransactionService {
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;
    /**
     * 계좌 소유주 확인
     * @param accountNumber - 계좌번호
     * @return 사용자 이름
     */
    public String validateRecipient(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        if (!account.isDeletedAccount())
            return memberRepository.findById(account.getUserId()).orElseThrow(
                () -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST)).getName();
        throw new InvalidAccountException(ErrorCode.DELETED_ACCOUNT);
    }

    /**
     * 입금
     * @param depositDto - TransactionDto
     * @return "입금완료"
     */
    @Transactional
    public String deposit(TransactionDto depositDto) {
        Account account = accountRepository.findById(depositDto.getAccountId())
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));
        if (account.isExistsAccount()) {
            Transaction transaction = Transaction.builder()
                    .accountId(depositDto.getAccountId())
                    .type(TransactionType.DEPOSIT)
                    .amount(depositDto.getAmount())
                    .name("전자입금")
                    .memo(depositDto.getMemo())
                    .build();

            transactionRepository.save(transaction);
            account.setBalance(account.getBalance() + depositDto.getAmount());
            return "입금완료";
        }
        throw new InvalidAccountException(ErrorCode.INVALID_ACCOUNT);
    }

    /**
     * 출금
     * @param withdrawalDto - TransactionDto
     * @return "출금완료"
     */
    @Transactional
    public String withdrawal(TransactionDto withdrawalDto) {

        Account account = accountRepository.findById(withdrawalDto.getAccountId())
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        if (account.getBalance() < withdrawalDto.getAmount())
            throw new OverdrawException(ErrorCode.EXCEED_BALANCE);

        if (account.isExistsAccount()) {
            Transaction transaction = Transaction.builder()
                    .accountId(withdrawalDto.getAccountId())
                    .type(TransactionType.WITHDRAWN)
                    .amount(withdrawalDto.getAmount())
                    .name("전자출금")
                    .memo(withdrawalDto.getMemo())
                    .build();
            transactionRepository.save(transaction);
            account.setBalance(account.getBalance()-withdrawalDto.getAmount());
            return "출금완료";
        }
        throw new InvalidAccountException(ErrorCode.INVALID_ACCOUNT);
    }

    /**
     * 송금
     * @param transferDto - TransactionDto
     * @return "송금완료"
     */
    @Transactional
    public String transfer(Long senderAccountId, Long receiverAccountId, TransactionDto transferDto) {

        Account senderAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));
        Account recipientAccount = accountRepository.findById(receiverAccountId)
                        .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        Member sender = memberRepository.findById(senderAccount.getUserId())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
        Member receiver = memberRepository.findById(recipientAccount.getUserId())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));

        if (senderAccount.getBalance() < transferDto.getAmount())
            throw new OverdrawException(ErrorCode.EXCEED_BALANCE);

        if (senderAccount.isExistsAccount() && recipientAccount.isExistsAccount()){
            Transaction senderTransaction = Transaction.builder()
                    .accountId(senderAccountId)
                    .type(TransactionType.TRANSFER)
                    .amount(-transferDto.getAmount())
                    .name(receiver.getName()) // 수신자 이름
                    .memo(transferDto.getMemo())
                    .build();
            transactionRepository.save(senderTransaction);

            Transaction recipientTransaction = Transaction.builder()
                    .accountId(receiverAccountId)
                    .type(TransactionType.TRANSFER)
                    .amount(transferDto.getAmount())
                    .name(sender.getName()) // 발신자 이름
                    .memo(transferDto.getMemo())
                    .build();
            transactionRepository.save(recipientTransaction);

            senderAccount.setBalance(senderAccount.getBalance() - transferDto.getAmount());
            recipientAccount.setBalance(recipientAccount.getBalance() + transferDto.getAmount());
            return "송금완료";
        }
        throw new InvalidAccountException(ErrorCode.INVALID_ACCOUNT);
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
