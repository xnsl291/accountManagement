package zb.accountMangement.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionDto {

  @NotNull
  private Long accountId;

  @NotBlank
  private String name;

  @NotBlank
  private Long amount;

  private String memo;

  @CreatedDate
  private LocalDateTime transactedAt;
}
