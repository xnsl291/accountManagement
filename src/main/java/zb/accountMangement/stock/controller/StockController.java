package zb.accountMangement.stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zb.accountMangement.stock.dto.*;
import zb.accountMangement.stock.service.StockService;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    /**
    * 주식 매수
    * @param buyStockDto - 매수 dto (계좌 ID, 주식 ID, 매수희망가, 매수수량)
    * @return 주문 체결 여부
    */
    @PostMapping("/buy")
    public ResponseEntity<Boolean> buyStock(@Valid @RequestBody TradeStockDto buyStockDto){
        return ResponseEntity.ok().body(stockService.buyStock(buyStockDto));
    }

    /**
     * 주식 매도
     * @param sellStockDto - 매도 dto (계좌 ID, 주식 ID, 매수희망가, 매수수량)
     * @return 주문 체결 여부
     */
    @PostMapping("/sell")
    public ResponseEntity<Boolean> sellStock(@Valid @RequestBody TradeStockDto sellStockDto){
        return ResponseEntity.ok().body(stockService.sellStock(sellStockDto));
    }

    @GetMapping("/{account_id}/balance")
    public ResponseEntity<String> getStockBalance(@Validated @PathVariable("account_id")Long accountId){
        return ResponseEntity.ok().body(stockService.getStockBalance(accountId));
    }

    @GetMapping("/{account_id}/history")
    public ResponseEntity<String> getTradeHistory(@Validated @PathVariable("account_id")Long accountId){
        return ResponseEntity.ok().body(stockService.getTradeHistory(accountId));
    }
}
