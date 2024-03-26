package zb.accountMangement.stock.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransferStockDto {

    @NotBlank
    private Long senderAccountId;

    @NotBlank
    private Long receiverAccountId;

    @NotBlank
    private Long stockId;

    @NotBlank
    private Double price;

    @NotBlank
    private int quantity;

    private LocalDateTime transferAt;
}
