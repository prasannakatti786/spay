package com.spay.wallet.notifications.service;

import com.spay.wallet.account.services.AccountService;
import com.spay.wallet.messages.MessageChannel;
import com.spay.wallet.messages.MessagePayload;
import com.spay.wallet.messages.MessageSenderImplementer;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.reqRes.TransactionRecordResponse;
import com.spay.wallet.transaction.entities.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionRecordNotificationService {
    private final MessageSenderImplementer messageSenderImplementer;
    private final AccountService accountService;

    public void notifyCustomer(TransactionRecordResponse recordResponse, String senderAccount, String receiverAccount,
                               TransactionType transactionType, PaymentChannel paymentChannel){

        if(paymentChannel.equals(PaymentChannel.INTERSPAY)){
            sendMessageToSender(recordResponse, senderAccount);
            sendMessageToReceiver(recordResponse, receiverAccount);
            return;
        }

        if(TransactionType.WITHDRAWAL.equals(transactionType)){
            sendMessageToSender(recordResponse, senderAccount);
            return;
        }

        if(transactionType.equals(TransactionType.TOP_UP)){
            sendMessageToReceiver(recordResponse, receiverAccount);
            return;
        }

    }

    private void sendMessageToReceiver(TransactionRecordResponse recordResponse, String receiverAccount) {
        accountService.getAccountHolder(receiverAccount).forEach(customer -> {
            var messagePayloadEmail = MessagePayload.builder().channel(MessageChannel.EMAIL).subject("Payment").message(recordResponse.getReceiverMessage()).sendTo(customer.getEmail()).build();
            var messagePayloadPhone = MessagePayload.builder().channel(MessageChannel.SMS).subject("Payment").message(recordResponse.getReceiverMessage()).sendTo(customer.getPhoneNumber()).build();
            messageSenderImplementer.sendMessage(messagePayloadEmail);
            messageSenderImplementer.sendMessage(messagePayloadPhone);
        });
    }

    private void sendMessageToSender(TransactionRecordResponse recordResponse, String senderAccount) {
        accountService.getAccountHolder(senderAccount).forEach(customer -> {
            var messagePayloadEmail = MessagePayload.builder().channel(MessageChannel.EMAIL).subject("Payment").message(recordResponse.getSenderMessage()).sendTo(customer.getEmail()).build();
            var messagePayloadPhone = MessagePayload.builder().channel(MessageChannel.SMS).subject("Payment").message(recordResponse.getSenderMessage()).sendTo(customer.getPhoneNumber()).build();
            messageSenderImplementer.sendMessage(messagePayloadEmail);
            messageSenderImplementer.sendMessage(messagePayloadPhone);
        });
    }
}
