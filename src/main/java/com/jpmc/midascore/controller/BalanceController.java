package com.jpmc.midascore.controller;

import com.jpmc.midascore.external.Balance;
import com.jpmc.midascore.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance(@RequestParam String userId) {
        double amount = balanceService.getUserBalance(userId);
        return ResponseEntity.ok(new Balance(userId, amount));
    }
}
