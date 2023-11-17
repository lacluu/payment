package lac.test.service.impl;

import lac.test.entity.Bill;
import lac.test.exception.BadRequestException;
import lac.test.model.BillModel;
import lac.test.repository.BillingRepository;
import lac.test.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Override
    public List<BillModel> getByUserId(Long userId) {
        return billingRepository
                .findAllByUserId(userId).stream()
                .map(BillingServiceImpl::mapBillItemToBillModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillModel> search(Long userId, List<Long> billNumbers) {
        return billingRepository
                .findAllByUserIdAndIdIn(userId, billNumbers).stream()
                .map(BillingServiceImpl::mapBillItemToBillModel)
                .collect(Collectors.toList());
    }

    private static BillModel mapBillItemToBillModel(Bill item) {
        BillModel model = new BillModel();
        model.setId(item.getId());
        model.setUserId(item.getUserId());
        model.setType(item.getType());
        model.setAmount(item.getAmount());
        model.setDueDate(item.getDueDate());
        model.setStage(item.getStage());
        model.setProvider(item.getProvider());
        return model;
    }

    @Override
    @Transactional
    public BillModel create(BillModel model) throws BadRequestException {
        if (model.getUserId() == null || model.getUserId() == 0) {
            throw new BadRequestException("Bad request: Bill must belong to some use");
        }

        //build data insert to database
        Bill bill = new Bill();
        bill.setUserId(model.getUserId());
        bill.setType(model.getType());
        bill.setAmount(model.getAmount());
        bill.setDueDate(model.getDueDate());
        bill.setStage("NEW");
        bill.setProvider(model.getProvider());
        billingRepository.save(bill);

        //update model return to client
        model.setId(bill.getId());
        model.setStage(bill.getStage());

        return model;
    }

    @Override
    @Transactional
    public BillModel update(Long billId, BillModel model) throws BadRequestException {
        Bill bill = getBill(billId);

        validatePaidStatus(bill);

        //update data
        bill.setType(model.getType());
        bill.setAmount(model.getAmount());
        bill.setDueDate(model.getDueDate());
        bill.setProvider(model.getProvider());

        billingRepository.save(bill);

        return model;
    }

    @Override
    @Transactional
    public Bill pay(Long billId) throws BadRequestException {
        Bill bill = getBill(billId);
        bill.setStage("PAID");
        billingRepository.save(bill);
        return bill;
    }

    private static void validatePaidStatus(Bill bill) throws BadRequestException {
        if (!bill.getStage().equals("NOT_PAID")) {
            throw new BadRequestException("Cannot touch bill in stage PAID");
        }
    }

    private Bill getBill(Long billId) throws BadRequestException {
        if (billId == null || billId == 0) {
            throw new BadRequestException("Bad request: Bill with ID null or equal zero");
        }

        return billingRepository.findById(billId)
                .orElseThrow(() -> new BadRequestException("Bad request: Bill not existed"));
    }

    private Bill getBill(Long billId, Long userId) throws BadRequestException {
        if (billId == null || billId == 0) {
            throw new BadRequestException("Bad request: Bill with ID null or equal zero");
        }

        return billingRepository.findFirstByUserIdAndId(userId, billId)
                .orElseThrow(() -> new BadRequestException("Bad request: Bill not existed"));
    }

    @Override
    public BillModel delete(Long userId, Long billId) throws BadRequestException {
        Bill bill = getBill(billId, userId);

        validatePaidStatus(bill);
        bill.setDeleted(true);

        billingRepository.save(bill);

        return mapBillItemToBillModel(bill);
    }

}
