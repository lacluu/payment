package lac.test.service.impl;

import lac.test.entity.Payment;
import lac.test.entity.User;
import lac.test.exception.BadRequestException;
import lac.test.model.BillModel;
import lac.test.repository.PaymentRepository;
import lac.test.service.BillingService;
import lac.test.service.PaymentService;
import lac.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillingService billingService;

    @Autowired
    private UserService userService;

    @Override
    public List<Payment> pay(Long userId, List<Long> billNumbers) throws BadRequestException {
        User user = userService.getByUserId(userId);
        final Long currentUserBalance = user.getBalance();
        List<BillModel> billModels = billingService.search(userId, billNumbers);

        //Payment would be prioritized for bill with early due dates.
        billModels.sort(Comparator.comparing(BillModel::getDueDate));

        long tmpAmountPaid = 0L;
        List<Payment> paidPaymentResult = new ArrayList<>();

        //loop in list billings and pay for each billing
        for (BillModel billModel : billModels) {
            String stage = "PENDING";

            //if user have enough money for pay this bill => then execute payment for that bill
            //else upsert PaymentHistory with stage "PENDING" try to pay with next bill
            Long amountPay = billModel.getAmount();
            if (currentUserBalance > tmpAmountPaid + amountPay) {
                billingService.pay(billModel.getId());

                stage = "PROCESSED";
                // increase value amount Paid
                tmpAmountPaid += amountPay;
            }
            Payment payment = upsert(userId, billModel.getId(), amountPay, stage);
            paidPaymentResult.add(payment);
        }

        //update balance user
        userService.pay(userId, tmpAmountPaid);
        return paidPaymentResult;
    }

    private Payment upsert(Long userId, Long billId, Long amountPay, String stage) {
        Optional<Payment> historyOptional = paymentRepository.findByUserIdAndBillId(userId, billId);

        //when not found history with userId, billId => create new history
        if (!historyOptional.isPresent()) {
            Payment payment = new Payment(userId, billId, amountPay, stage);
            paymentRepository.save(payment);
            return payment;
        }

        //else return current history
        return historyOptional.get();
    }

    @Override
    public List<Payment> search(Long userId) {
        return paymentRepository.findAllByUserId(userId);
    }
}
