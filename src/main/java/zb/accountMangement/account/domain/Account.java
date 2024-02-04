package zb.accountMangement.account.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zb.accountMangement.account.type.AccountStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private String nickname;

  private String password;

  @Column(unique = true)
  private String accountNumber;

  @Builder.Default
  private long amount =0;

  @Builder.Default
  private AccountStatus status = AccountStatus.PENDING;

  @CreatedDate
  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;
}
