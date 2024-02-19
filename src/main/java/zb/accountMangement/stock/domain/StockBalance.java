package zb.accountMangement.stock.domain;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockBalance {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//  @ManyToOne
//  @JoinColumn(name = "account_id")
//  private Account account;
//
//  @ManyToOne
//  @JoinColumn(name = "stock_id")
//  private Stock stock;

  private Long accountId;

  private Long stockId;

  private Double avgPrice; // 평균 매수가

//  private Long profitNLoss; // 평가손익

  private int quantity; // 수량
}
