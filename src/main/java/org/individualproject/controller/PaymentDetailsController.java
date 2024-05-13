package org.individualproject.controller;

import jakarta.validation.Valid;
import org.individualproject.business.PaymentDetailsService;
import org.individualproject.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payment-details")
public class PaymentDetailsController {
    private PaymentDetailsService paymentDetailsService;

    public PaymentDetailsController(PaymentDetailsService pService){
        this.paymentDetailsService = pService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDetails> getPaymentDetails(@PathVariable(value = "id") final Long id)
    {
        final Optional<PaymentDetails> paymentDetailsOptional = paymentDetailsService.getPaymentDetails(id);
        return paymentDetailsOptional.map(booking -> ResponseEntity.ok().body(booking))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping()
    public ResponseEntity<List<PaymentDetails>> getAllPaymentDetails()
    {
        List<PaymentDetails> paymentDetails = paymentDetailsService.getAllPaymentDetails();
        return ResponseEntity.ok().body(paymentDetails);
    }

    @PostMapping()
    public ResponseEntity<PaymentDetails> create(@RequestBody @Valid CreatePaymentDetailsRequest request) {
        PaymentDetails response = paymentDetailsService.createPaymentDetails(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deletePaymentDetails(@PathVariable(value = "id") final Long id)
    {
        if (paymentDetailsService.deletePaymentDetails(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePaymentDetails(@PathVariable(value = "id") final long id, @RequestBody @Valid UpdatePaymentDetailsRequest request){

        request.setId(id);
        paymentDetailsService.updatePaymentDetails(request);
        return ResponseEntity.noContent().build();
    }

}
