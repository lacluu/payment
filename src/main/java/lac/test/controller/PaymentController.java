package lac.test.controller;

import lac.test.entity.Payment;
import lac.test.exception.BadRequestException;
import lac.test.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/user/{userId}")
    public List<Payment> search(@PathVariable(name = "userId") Long userId) {
        return paymentService.search(userId);
    }

    @PostMapping("/user/{userId}/")
    public List<Payment> pay(@PathVariable(name = "userId") Long userId, @RequestBody List<Long> billIds) {
        try {
            return paymentService.pay(userId, billIds);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
