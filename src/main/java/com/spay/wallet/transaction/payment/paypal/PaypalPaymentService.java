package com.spay.wallet.transaction.payment.paypal;


import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.payment.PaymentPayload;
import com.spay.wallet.transaction.payment.PaymentResponse;
import com.spay.wallet.transaction.payment.PaymentService;
import com.spay.wallet.transaction.payment.ReceiverInfoResponse;

import com.twilio.rest.api.v2010.account.call.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("PAYPAL")
@RequiredArgsConstructor
public class PaypalPaymentService  implements PaymentService {
    @Override
    public PaymentResponse createPayment(PaymentPayload payload) {
        return null;
    }

    @Override
    public PaymentResponse payout(PaymentPayload payload) {
        return null;
    }

    @Override
    public ReceiverInfoResponse getReceiverInfo(String receiverAccount, CurrencyCode currencyCode, BigDecimal amount) {
        return null;
    }
//    private final PaypalPaymentApi paypalPaymentApi;
//
//
//    @Override
//    public PaymentResponse createPayment(PaymentPayload payload) {
//        try{
//            String paypalPayment = paypalPaymentApi.createPayment(payload.getCurrencyCode(),payload.getAmount().doubleValue());
//            // TODO create paypal payment
//            return new PaymentResponse(null,null,payload.getAmount(),null);
//        }catch (RuntimeException e){
//            throw new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @Override
//    public PaymentResponse payout(PaymentPayload payload) {
//        // TODO paypal payout
//        return null;
//    }
//
//    @Override
//    public ReceiverInfoResponse getReceiverInfo(String receiverAccount, CurrencyCode currencyCode, BigDecimal amount) {
//        return null;
//    }
//
//
//    public Payment completePayment(String paymentId, String payerId){
//        try {
//            var payment = paypalPaymentApi.executePayment(paymentId, payerId);
//            // TODO complete paypal payment
//
//
//            return (Payment) payment;
//        }catch (RuntimeException e){
//            throw new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
//

}
