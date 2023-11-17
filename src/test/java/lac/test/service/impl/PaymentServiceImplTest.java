package lac.test.service.impl;

import lac.test.Main;
import lac.test.entity.Payment;
import lac.test.entity.User;
import lac.test.exception.BadRequestException;
import lac.test.model.BillModel;
import lac.test.repository.PaymentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = Main.class)
public class PaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private BillingServiceImpl billingService;

    @Mock
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private final Long userId = 1L;

    @Test
    public void testGetByUserId() {
        // Mock data
        Payment payment1 = new Payment(userId, 1L, 175_000L, "PROCESSED");
        Payment payment2 = new Payment(userId, 2L, 200_000L, "PENDING");

        List<Payment> payments = new ArrayList<>();
        payments.add(payment1);
        payments.add(payment2);

        Mockito.when(paymentRepository.findAllByUserId(userId)).thenReturn(payments);

        //run function test
        List<Payment> result = paymentService.search(userId);

        //assert result with expected
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void testPay_SuccessAllBill() throws BadRequestException {
        // Mock data
        BillModel bill1 = new BillModel(1L, userId, "WATER", 175_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "SAVACO HCMC");
        BillModel bill2 = new BillModel(2L, userId, "ELECTRIC", 200_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "EVN HCMC");

        List<BillModel> bills = new ArrayList<>();
        bills.add(bill1);
        bills.add(bill2);

        List<Long> billIds = bills.stream().map(BillModel::getId).collect(Collectors.toList());

        Mockito.when(billingService.search(userId, billIds)).thenReturn(bills);
        Mockito.when(billingService.pay(Mockito.any(Long.class))).thenReturn(bill1.mapToBill());

        User user = new User("username", 500_000L);

        Mockito.when(userService.getByUserId(userId)).thenReturn(user);
        Mockito.when(userService.pay(userId, 175_000L)).thenReturn(user);
        Mockito.when(userService.pay(userId, 200_000L)).thenReturn(user);

        Mockito.when(paymentRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.<Payment>getArgument(0));

        //run function test
        List<Payment> payments = paymentService.pay(userId, billIds);

        //assert result with expected
        Assertions.assertNotNull(payments);
        Assertions.assertEquals(2, payments.size());
        Assertions.assertEquals("PROCESSED", payments.get(0).getStage());
        Assertions.assertEquals("PROCESSED", payments.get(1).getStage());
    }


    @Test
    public void testPay_SuccessSecondBill() throws BadRequestException {
        // Mock data
        BillModel bill1 = new BillModel(1L, userId, "WATER", 1_000_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "SAVACO HCMC");
        BillModel bill2 = new BillModel(2L, userId, "ELECTRIC", 200_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "EVN HCMC");

        List<BillModel> bills = new ArrayList<>();
        bills.add(bill1);
        bills.add(bill2);

        List<Long> billIds = bills.stream().map(BillModel::getId).collect(Collectors.toList());

        Mockito.when(billingService.search(userId, billIds)).thenReturn(bills);
        Mockito.when(billingService.pay(Mockito.any(Long.class))).thenReturn(bill1.mapToBill());

        User user = new User("username", 500_000L);

        Mockito.when(userService.getByUserId(userId)).thenReturn(user);
        Mockito.when(userService.pay(userId, 1_000_000L)).thenReturn(user);
        Mockito.when(userService.pay(userId, 200_000L)).thenReturn(user);

        Mockito.when(paymentRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.<Payment>getArgument(0));

        //run function test
        List<Payment> payments = paymentService.pay(userId, billIds);

        //assert result with expected
        Assertions.assertNotNull(payments);
        Assertions.assertEquals(2, payments.size());
        Assertions.assertEquals("PENDING", payments.get(0).getStage());
        Assertions.assertEquals("PROCESSED", payments.get(1).getStage());
    }

}
