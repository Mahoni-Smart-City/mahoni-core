package com.mahoni.loyaltyservice.repository;

import com.mahoni.loyaltyservice.model.Loyalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoyaltyRepository extends JpaRepository<Loyalty, UUID> {
}
