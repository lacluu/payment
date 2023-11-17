package lac.test.controller;

import lac.test.exception.BadRequestException;
import lac.test.model.BillModel;
import lac.test.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController()
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillingService billingService;

    @GetMapping("/user/{userId}/")
    public List<BillModel> getAllBillByUserId(@PathVariable(name = "userId") Long userId) {
        return billingService.getByUserId(userId);
    }

    @PostMapping
    public BillModel createNewBill(@RequestBody BillModel billModel) {
        try {
            return billingService.create(billModel);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{billId}")
    public BillModel updateBill(@RequestBody BillModel billModel, @PathVariable(name = "billId") Long billId) {
        try {
            return billingService.update(billId, billModel);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{billId}/user/{userId}")
    public BillModel deleteBill(@PathVariable(name = "billId") Long billId, @PathVariable(name = "userId") Long userId) {
        try {
            return billingService.delete(userId, billId);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
