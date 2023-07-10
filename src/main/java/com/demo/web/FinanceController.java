package com.demo.web;

import com.demo.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/dividend/{companyName}")//배당금 조회
    public ResponseEntity<?> searchFinance(@PathVariable String companyName){
        var result=this.financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(result);

    }
}
