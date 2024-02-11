package zb.accountMangement.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionDto {

  @NotNull
  private Long accountId;

  @NotNull
  private String name;

  @NotNull
  private Long amount;

  private String memo;

  private LocalDateTime TransactedAt;
}
