package zb.accountMangement.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeeRate {
    TRANSFER_FEE_RATE(0.01),

    ;
    private final double rate;
}
