package lac.test.service;

import lac.test.entity.Payment;
import lac.test.exception.BadRequestException;

import java.util.List;

public interface PaymentService {

    List<Payment> pay(Long userId, List<Long> billNumbers) throws BadRequestException;

    List<Payment> search(Long userId);
}
