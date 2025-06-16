package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService {

    private final TransactionRecordRepository transactionRepository;

    public BalanceService(TransactionRecordRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public double getUserBalance(String userId) {
        List<TransactionRecord> records = transactionRepository.findByUserIdAndValid(userId, true);
        double balance = 0.0;

        for (TransactionRecord txn : records) {
            if ("CREDIT".equalsIgnoreCase(txn.getTransactionType())) {
                balance += txn.getAmount();
            } else if ("DEBIT".equalsIgnoreCase(txn.getTransactionType())) {
                balance -= txn.getAmount();
            }
        }

        return balance;
    }
}
