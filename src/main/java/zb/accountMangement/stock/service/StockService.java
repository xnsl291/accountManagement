package zb.accountMangement.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zb.accountMangement.account.domain.Account;
import zb.accountMangement.account.repository.AccountRepository;
import zb.accountMangement.common.error.exception.*;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.stock.domain.Stock;
import zb.accountMangement.stock.domain.StockBalance;
import zb.accountMangement.stock.domain.Trading;
import zb.accountMangement.stock.dto.DateDto;
import zb.accountMangement.stock.dto.TradeStockDto;
import zb.accountMangement.stock.repository.StockBalanceRepository;
import zb.accountMangement.stock.repository.StockRepository;
import zb.accountMangement.stock.repository.TradingRepository;
import zb.accountMangement.stock.type.TradeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final AccountRepository accountRepository;
    private final StockRepository stockRepository;
    private final StockBalanceRepository stockBalanceRepository;
    private final TradingRepository tradeHistoryRepository;

    /**
     * 총금액 계산
     * @param dto - TradeStockDto (계좌 ID, 주식 ID, 매수희망가, 매수수량)
     * @return 총 금액
     */
    private double calculateTotalPrice(TradeStockDto dto){
        return dto.getQuantity() * dto.getPrice();
    }

    /**
    * 주식 매수
    * @param buyStockDto - 매수 dto (계좌 ID, 주식 ID, 매수희망가, 매수수량)
    * @return 주문 체결 여부
    */
    @Transactional
    public Boolean buyStock(TradeStockDto buyStockDto) {
        Account account = accountRepository.findById(buyStockDto.getAccountId())
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        Stock stock = stockRepository.findById(buyStockDto.getStockId())
                .orElseThrow(() -> new NotFoundStockException(ErrorCode.STOCK_NOT_EXIST));

        double totalCost = calculateTotalPrice(buyStockDto);

        if (account.getBalance() < totalCost)
            throw new InsufficientBalanceException(ErrorCode.EXCEED_BALANCE);

        Trading tradeHistory = Trading.builder()
                .stockId(buyStockDto.getStockId())
                .price(buyStockDto.getPrice())
                .quantity(buyStockDto.getQuantity())
                .type(TradeType.BUY)
                .tradeAt(LocalDateTime.now())
                .isConcluded(false)
                .build();
        tradeHistoryRepository.save(tradeHistory);

        // 신청한 매수가가 현재가보다 같거나 높으면 체결
        if (stock.getCurrentPrice() <= buyStockDto.getPrice()) {
            account.setBalance(account.getBalance() - totalCost);
            Optional<StockBalance> optionalStockBalance =
                stockBalanceRepository.findByAccountIdAndStockId(buyStockDto.getAccountId(), buyStockDto.getStockId());
            StockBalance stockBalance;

            if (optionalStockBalance.isPresent()) {
                stockBalance = optionalStockBalance.get();

                // 평단가 변경
                stockBalance.setAvgPrice((stockBalance.getAvgPrice() * stockBalance.getQuantity() + totalCost) /
                                            (stockBalance.getQuantity() + buyStockDto.getQuantity()));
                stockBalance.setQuantity(stockBalance.getQuantity() + buyStockDto.getQuantity());

            } else {
                stockBalance = StockBalance.builder()
                    .accountId(buyStockDto.getAccountId())
                    .stockId(buyStockDto.getStockId())
                    .avgPrice(buyStockDto.getPrice())
                    .quantity(buyStockDto.getQuantity())
                    .build();
                stockBalanceRepository.save(stockBalance);
            }
            tradeHistory.setConcluded(true);
        }
        return tradeHistory.isConcluded();
    }

    /**
     * 주식 매도
     * @param sellStockDto - 매도 dto (계좌 ID, 주식 ID, 매수희망가, 매수수량)
     * @return 주문 체결 여부
     */
    public Boolean sellStock(TradeStockDto sellStockDto) {
        Account account = accountRepository.findById(sellStockDto.getAccountId())
                .orElseThrow(() -> new NotFoundAccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        Stock stock = stockRepository.findById(sellStockDto.getStockId())
                .orElseThrow(() -> new NotFoundStockException(ErrorCode.STOCK_NOT_EXIST));

        StockBalance stockBalance = stockBalanceRepository.findByAccountIdAndStockId(sellStockDto.getAccountId(),sellStockDto.getStockId())
                .orElseThrow(() -> new InsufficientStockException(ErrorCode.NO_STOCK_BALANCE));

        if (stockBalance.getQuantity() < sellStockDto.getQuantity())
            throw new InsufficientStockException(ErrorCode.INSUFFICIENT_STOCK);

        Trading tradeHistory = Trading.builder()
                .stockId(sellStockDto.getStockId())
                .price(sellStockDto.getPrice())
                .quantity(sellStockDto.getQuantity())
                .type(TradeType.SELL)
                .tradeAt(LocalDateTime.now())
                .isConcluded(false)
                .build();
        tradeHistoryRepository.save(tradeHistory);

        // 판매하고자 하는 금액이 현재가보다 같거나 낮으면 판매완료
        if (stock.getCurrentPrice() >= sellStockDto.getPrice()) {
            account.setBalance(account.getBalance() + calculateTotalPrice(sellStockDto) );  // 잔액 증액
            stockBalance.setQuantity(stockBalance.getQuantity() - sellStockDto.getQuantity());  // 주식 수량 증가
            tradeHistory.setConcluded(true); // 주문 체결
        }
        return tradeHistory.isConcluded();
    }

    /**
     * 거래내역 조회
     * @param accountId - 계좌 ID
     * @param dateDto - 조회할 날짜, 월
     * @return 거래내역
     */
    public List<Trading> getTradeHistory(DateDto dateDto, Long accountId) {
        LocalDate requestedDate = LocalDate.of(dateDto.getYear(), dateDto.getMonth(), 1);

        // 요청된 날짜가 오늘 이후인지
        if (requestedDate.isAfter(LocalDate.now()))
            throw new InvalidInputException(ErrorCode.INVALID_REQUEST_DATE);

        LocalDateTime startDate = LocalDateTime.of(dateDto.getYear(), dateDto.getMonth(), 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusNanos(1); // 월의 마지막 일시

        return tradeHistoryRepository.findByAccountIdAndTradeAtBetweenOrderByTradeAtDesc(accountId, startDate, endDate);
    }

    /**
     * 계좌 잔고 조회
     * @param accountId - 계좌  ID
     * @return 현재 보유중인 주식 종목 리스트
     */
    public List<StockBalance> getStockBalance(Long accountId) {
        List<StockBalance> stockBalances = stockBalanceRepository.findByAccountId(accountId);

        if (!stockBalances.isEmpty())
            // TODO: 스케줄러에 등록해서 STOCK 정보가 업데이트 될 떄 같이 업데이트 되게 변경
            for (StockBalance stockBalance : stockBalances)
                stockBalance.setProfitNLoss(calculateProfitLoss(stockBalance));  // 평가손익 업데이트
        return stockBalances;
    }

    /**
     * 평가손익 계산
     * @param stockBalance - 주식 잔고
     * @return 평가손익
     */
    private Double calculateProfitLoss(StockBalance stockBalance){
        Stock stock = stockRepository.findById(stockBalance.getId())
                .orElseThrow(() -> new InsufficientStockException(ErrorCode.INSUFFICIENT_STOCK));
        return (stock.getCurrentPrice() - stockBalance.getAvgPrice()) * stockBalance.getQuantity();
    }

    /**
     * 주식 현재 시세 조회
     * @param stockId - 주식 ID
     * @return 주식 가격
     */
    public Double getCurrentStockPrice(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new InsufficientStockException(ErrorCode.INSUFFICIENT_STOCK));
        return stock.getCurrentPrice();
    }
}
