package zb.accountMangement.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zb.accountMangement.stock.domain.Trading;

@Repository
public interface TradingRepository extends JpaRepository<Trading, Long> {

}
