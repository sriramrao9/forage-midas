package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumerService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public KafkaConsumerService(UserRepository userRepository,
                                TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "transactions", groupId = "midas-core")
    public void listen(Transaction transaction) {
        String senderName = transaction.getSource();
        String recipientName = transaction.getDestination();
        float amount = transaction.getAmount();

        Optional<User> senderOpt = userRepository.findByName(senderName);
        Optional<User> recipientOpt = userRepository.findByName(recipientName);

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            User sender = senderOpt.get();
            User recipient = recipientOpt.get();

            if (sender.getBalance() >= amount) {
                // Adjust balances
                sender.setBalance(sender.getBalance() - amount);
                recipient.setBalance(recipient.getBalance() + amount);

                // Save updated users
                userRepository.save(sender);
                userRepository.save(recipient);

                // Record transaction
                TransactionRecord record = new TransactionRecord();
                record.setAmount(amount);
                record.setSender(sender);
                record.setRecipient(recipient);
                record.setTimestamp(transaction.getTimestamp());

                transactionRecordRepository.save(record);

                System.out.println("✅ Transaction recorded successfully.");
            } else {
                System.out.println("❌ Insufficient balance. Skipping transaction.");
            }
        } else {
            System.out.println("❌ Invalid sender or recipient. Skipping transaction.");
        }
    }
}
