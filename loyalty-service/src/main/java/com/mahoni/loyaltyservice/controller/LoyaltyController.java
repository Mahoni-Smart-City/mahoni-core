package com.mahoni.loyaltyservice.controller;

import com.mahoni.loyaltyservice.model.Loyalty;
import com.mahoni.loyaltyservice.service.LoyaltyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class LoyaltyController {

  @Autowired
  LoyaltyService loyaltyService;

  @GetMapping
  private ResponseEntity<List<Loyalty>> getAll() {
    List<Loyalty> allLoyalty = loyaltyService.getAll();
    return ResponseEntity.ok(allLoyalty);
  }
}
