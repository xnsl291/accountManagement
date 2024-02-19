package zb.accountMangement.stock.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TradeStockDto {

    @NotBlank
    private Long accountId;

    @NotBlank
    private Long stockId;

    @NotBlank
    private Double price;

    @NotBlank
    private int quantity;
}
