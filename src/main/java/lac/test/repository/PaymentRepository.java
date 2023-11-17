package lac.test.repository;

import lac.test.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    List<Payment> findAllByUserId(Long userId);

    Optional<Payment> findByUserIdAndBillId(Long userId, Long billId);
}
