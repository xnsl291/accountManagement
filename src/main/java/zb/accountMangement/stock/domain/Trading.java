package zb.accountMangement.stock.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zb.accountMangement.stock.type.TradeType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trading {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long accountId; // account 가 필요해./..?

  private Long stockId;

  private Double price;   // 매수가

  private int quantity; // 수량

  @Enumerated
  private TradeType type; // 매매종류

  @Builder.Default
  private boolean isConcluded = false; // 체결되었는지 유무

  @CreatedDate
  private LocalDateTime createdAt;

  private LocalDateTime tradeAt;
}
