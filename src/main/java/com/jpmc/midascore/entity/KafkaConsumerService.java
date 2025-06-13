package com.jpmc.midascore.kafka;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KafkaConsumerService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public KafkaConsumerService(UserRepository userRepository,
                                TransactionRecordRepository transactionRecordRepository,
                                RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "transactions", groupId = "midas-core")
    public void listen(Transaction transaction) {
        // Convert source and destination IDs from String to Long
        Long senderId;
        Long recipientId;

        try {
            senderId = Long.parseLong(transaction.getSource());
            recipientId = Long.parseLong(transaction.getDestination());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format in transaction: discarding.");
            return;
        }

        User sender = userRepository.findById(senderId).orElse(null);
        User recipient = userRepository.findById(recipientId).orElse(null);

        if (sender == null || recipient == null) {
            System.out.println("Invalid sender or recipient: discarding transaction.");
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("Insufficient balance: discarding transaction.");
            return;
        }

        // Call Incentive API
        double incentiveAmount = 0.0;
        try {
            Incentive incentive = restTemplate.postForObject(
                    "http://localhost:8080/incentive",
                    transaction,
                    Incentive.class
            );
            if (incentive != null) {
                incentiveAmount = incentive.getAmount();
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch incentive: " + e.getMessage());
        }

        // Adjust balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction
        TransactionRecord record = new TransactionRecord();
        record.setAmount(transaction.getAmount());
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setTimestamp(transaction.getTimestamp());
        record.setIncentive(incentiveAmount); // Save incentive

        transactionRecordRepository.save(record);

        System.out.println("Transaction recorded successfully with incentive: " + incentiveAmount);
    }

    // DTO class to deserialize incentive response
    private static class Incentive {
        private double amount;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}
