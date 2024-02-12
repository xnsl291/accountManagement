package zb.accountMangement.account.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zb.accountMangement.account.type.TransactionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long accountId;

  private String name;

  @Enumerated(EnumType.STRING)
  private TransactionType type;

  private Long amount;

  private String memo;

//  private Long Balance;  // 거래후잔액?

  @CreatedDate
  private LocalDateTime TransactedAt;
}
