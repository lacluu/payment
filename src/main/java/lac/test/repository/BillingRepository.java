package lac.test.repository;

import lac.test.entity.Bill;
import org.hibernate.annotations.Where;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends CrudRepository<Bill, Long> {
    @Where(clause = "deleted=false")
    List<Bill> findAllByUserId(Long userId);

    @Where(clause = "deleted=false")
    Optional<Bill> findFirstByUserIdAndId(Long userId, Long id);

    @Where(clause = "deleted=false")
    List<Bill> findAllByUserIdAndIdIn(Long userId, List<Long> ids);
}
