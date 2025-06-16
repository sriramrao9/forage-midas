package com.jpmc.midascore.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.User; // ✅ Import the existing User class
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepository;

    public KafkaConsumerService(ObjectMapper objectMapper,
                                UserRepository userRepository,
                                TransactionRecordRepository transactionRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "transactions", groupId = "midas-core-group")
    public void consume(ConsumerRecord<String, String> record) throws Exception {
        String message = record.value();
        TransactionRecord transaction = objectMapper.readValue(message, TransactionRecord.class);

        if (isValidTransaction(transaction)) {
            transaction.setValid(true);
            transactionRepository.save(transaction);

            // Optional: update user balance or other logic
        } else {
            transaction.setValid(false);
            transactionRepository.save(transaction);
        }
    }

    private boolean isValidTransaction(TransactionRecord transaction) {
        // Validate based on rules (e.g., DEBIT must not overdraw)
        if ("DEBIT".equalsIgnoreCase(transaction.getTransactionType())) {
            double balance = calculateBalance(transaction.getUserId());
            return balance >= transaction.getAmount();
        }
        return true;
    }

    private double calculateBalance(String userId) {
        return transactionRepository.findByUserIdAndValid(userId, true)
                .stream()
                .mapToDouble(t -> "CREDIT".equalsIgnoreCase(t.getTransactionType())
                        ? t.getAmount()
                        : -t.getAmount())
                .sum();
    }
}
