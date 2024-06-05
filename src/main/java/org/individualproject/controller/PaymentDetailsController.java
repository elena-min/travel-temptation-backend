package org.individualproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<PaymentDetails> getPaymentDetails(@PathVariable(value = "id")@NotNull final Long id)
    {
        final Optional<PaymentDetails> paymentDetailsOptional = paymentDetailsService.getPaymentDetails(id);
        return paymentDetailsOptional.map(booking -> ResponseEntity.ok().body(booking))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping()
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<List<PaymentDetails>> getAllPaymentDetails()
    {
        List<PaymentDetails> paymentDetails = paymentDetailsService.getAllPaymentDetails();
        return ResponseEntity.ok().body(paymentDetails);
    }

    @PostMapping()
    @RolesAllowed({"USER"})
    public ResponseEntity<PaymentDetails> create(@RequestBody @Valid CreatePaymentDetailsRequest request) {
        PaymentDetails response = paymentDetailsService.createPaymentDetails(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<Long> deletePaymentDetails(@PathVariable(value = "id")@NotNull final Long id)
    {
        if (paymentDetailsService.deletePaymentDetails(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<Void> updatePaymentDetails(@PathVariable(value = "id")@NotNull final long id, @RequestBody @Valid UpdatePaymentDetailsRequest request){

        request.setId(id);
        paymentDetailsService.updatePaymentDetails(request);
        return ResponseEntity.noContent().build();
    }

}
