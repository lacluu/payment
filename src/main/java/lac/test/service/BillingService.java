package lac.test.service;

import lac.test.entity.Bill;
import lac.test.exception.BadRequestException;
import lac.test.model.BillModel;

import java.util.List;

public interface BillingService {

    List<BillModel> getByUserId(Long userId);

    List<BillModel> search(Long userId, List<Long> billNumbers);

    BillModel create(BillModel model) throws BadRequestException;

    BillModel update(Long billId, BillModel model) throws BadRequestException;

    BillModel delete(Long userId, Long billId) throws BadRequestException;

    Bill pay(Long billId) throws BadRequestException;

}
