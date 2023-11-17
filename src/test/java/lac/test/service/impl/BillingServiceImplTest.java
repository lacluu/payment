package lac.test.service.impl;

import lac.test.Main;
import lac.test.entity.Bill;
import lac.test.exception.BadRequestException;
import lac.test.model.BillModel;
import lac.test.repository.BillingRepository;
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
import java.util.Optional;

@SpringBootTest(classes = Main.class)
public class BillingServiceImplTest {
    @Mock
    private BillingRepository billingRepository;

    @InjectMocks
    private BillingServiceImpl billingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private final Long userId = 1L;

    @Test
    public void testGetByUserId() {
        // Mock data
        Bill bill1 = new Bill(userId, "WATER", 175_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "SAVACO HCMC");
        Bill bill2 = new Bill(userId, "ELECTRIC", 200_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "EVN HCMC");

        List<Bill> bills = new ArrayList<>();
        bills.add(bill1);
        bills.add(bill2);

        Mockito.when(billingRepository.findAllByUserId(userId)).thenReturn(bills);

        //run function test
        List<BillModel> result = billingService.getByUserId(userId);

        //assert result with expected
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void testCreateBill_Success() throws BadRequestException {
        // Mock data
        BillModel billModel = new BillModel();
        billModel.setUserId(userId);
        billModel.setType("WATER");
        billModel.setAmount(100_000L);
        billModel.setDueDate(Timestamp.valueOf(LocalDateTime.now()));
        billModel.setProvider("SAVACO HCMC");

        Mockito.when(billingRepository.save(Mockito.any(Bill.class))).thenAnswer(invocation -> {
            Bill savedBill = invocation.getArgument(0);
            savedBill.setId(1L);
            return savedBill;
        });

        //run function test
        BillModel createdBill = billingService.create(billModel);

        //assert result with expected
        Assertions.assertNotNull(createdBill);
        Assertions.assertEquals(1L, createdBill.getId());
        Assertions.assertEquals("WATER", createdBill.getType());
        Assertions.assertEquals("NEW", createdBill.getStage());
    }

    @Test
    public void testCreateBill_FailWithUserIdZero() {
        // Mock data
        BillModel billModel = new BillModel();
        billModel.setUserId(0L);
        billModel.setType("WATER");
        billModel.setAmount(100_000L);
        billModel.setDueDate(Timestamp.valueOf(LocalDateTime.now()));
        billModel.setProvider("SAVACO HCMC");

        //assert result with expected
        Assertions.assertThrows(BadRequestException.class, () -> {
            billingService.create(billModel);
        });
    }


    @Test
    public void testCreateBill_FailWithAmountLessThanZero() {
        // Mock data
        BillModel billModel = new BillModel();
        billModel.setUserId(0L);
        billModel.setType("WATER");
        billModel.setAmount(-300_000L);
        billModel.setDueDate(Timestamp.valueOf(LocalDateTime.now()));
        billModel.setProvider("SAVACO HCMC");

        //assert result with expected
        Assertions.assertThrows(BadRequestException.class, () -> {
            billingService.create(billModel);
        });
    }

    @Test
    public void testUpdateBill_Success() throws BadRequestException {
        // Mock data
        Bill existingBill = new Bill(userId, "WATER", 175_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "SAVACO HCMC");

        Long billId = 1L;
        BillModel billModel = new BillModel();
        billModel.setId(billId);
        billModel.setUserId(userId);
        billModel.setType("ELECTRIC");
        billModel.setAmount(100_000L);
        billModel.setDueDate(Timestamp.valueOf(LocalDateTime.now()));
        billModel.setProvider("EVN HCMC");

        Mockito.when(billingRepository.findById(billId)).thenReturn(Optional.of(existingBill));

        Mockito.when(billingRepository.save(Mockito.any(Bill.class)))
                .thenAnswer(invocation -> invocation.<Bill>getArgument(0));

        //run function test
        BillModel updatedBill = billingService.update(userId, billModel);

        //assert result with expected
        Assertions.assertNotNull(updatedBill);
        Assertions.assertEquals(1L, updatedBill.getId());
        Assertions.assertEquals("ELECTRIC", updatedBill.getType());
        Assertions.assertEquals("EVN HCMC", updatedBill.getProvider());
        Assertions.assertEquals(100_000L, updatedBill.getAmount());
    }


    @Test
    public void testUpdateBill_FailWithCaseUserUpdatePaidBill() {
        // Mock data
        Bill existingBill = new Bill(userId, "WATER", 175_000L, Timestamp.valueOf(LocalDateTime.now()), "PAID", "SAVACO HCMC");

        Long billId = 1L;
        BillModel billModel = new BillModel();
        billModel.setId(billId);
        billModel.setUserId(userId);

        Mockito.when(billingRepository.findById(billId)).thenReturn(Optional.of(existingBill));

        Mockito.when(billingRepository.save(Mockito.any(Bill.class)))
                .thenAnswer(invocation -> invocation.<Bill>getArgument(0));

        //assert result with expected
        Assertions.assertThrows(BadRequestException.class, () -> {
            billingService.update(userId, billModel);
        });
    }

    @Test
    public void testDeleteBill_Success() throws BadRequestException {
        // Mock data
        Long billId = 1L;

        Bill existingBill = new Bill(userId, "WATER", 175_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "SAVACO HCMC");
        existingBill.setId(billId);

        Mockito.when(billingRepository.findFirstByUserIdAndId(billId, userId)).thenReturn(Optional.of(existingBill));

        Mockito.when(billingRepository.save(Mockito.any(Bill.class)))
                .thenAnswer(invocation -> invocation.<Bill>getArgument(0));

        //run function test
        BillModel deletedBill = billingService.delete(userId, billId);

        //assert result with expected
        Assertions.assertNotNull(deletedBill);
        Assertions.assertEquals(1L, deletedBill.getId());
    }

    @Test
    public void testDeleteBill_FailWhenDeleteBillNotExisted() {
        // Mock data
        Long billId = 19L;

        Bill existingBill = new Bill(userId, "WATER", 175_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "SAVACO HCMC");
        existingBill.setId(billId);

        Mockito.when(billingRepository.findFirstByUserIdAndId(billId, userId)).thenReturn(Optional.of(existingBill));

        Mockito.when(billingRepository.save(Mockito.any(Bill.class)))
                .thenAnswer(invocation -> invocation.<Bill>getArgument(0));

        //assert result with expected
        Assertions.assertThrows(BadRequestException.class, () -> {
            billingService.delete(userId, billId);
        });
    }

    @Test
    public void testPayBill_Success() throws BadRequestException {
        // Mock data
        Long billId = 1L;

        Bill existingBill = new Bill(userId, "WATER", 175_000L, Timestamp.valueOf(LocalDateTime.now()), "NOT_PAID", "SAVACO HCMC");
        existingBill.setId(billId);
        Mockito.when(billingRepository.findById(billId)).thenReturn(Optional.of(existingBill));

        Mockito.when(billingRepository.save(Mockito.any(Bill.class)))
                .thenAnswer(invocation -> invocation.<Bill>getArgument(0));

        //run function test
        Bill result = billingService.pay(billId);

        //assert result with expected
        Assertions.assertNotNull(result);
        Assertions.assertEquals("PAID", result.getStage());
    }

}
