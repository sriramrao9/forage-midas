package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class TestKafkaConsumer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @KafkaListener(topics = "transactions", groupId = "midas-core")
    public void listen(Transaction transaction) {
        String senderName = transaction.getSource();
        String recipientName = transaction.getDestination();
        double amount = transaction.getAmount(); // Use double instead of float

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
                record.setTimestamp(Instant.now().toString());
                record.setSender(sender);
                record.setRecipient(recipient);

                transactionRecordRepository.save(record);
            }
        }
    }
}
