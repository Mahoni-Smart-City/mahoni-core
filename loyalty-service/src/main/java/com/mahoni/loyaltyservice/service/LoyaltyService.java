package com.mahoni.loyaltyservice.service;

import com.mahoni.loyaltyservice.model.Loyalty;
import com.mahoni.loyaltyservice.repository.LoyaltyRepository;
import com.mahoni.schema.UserPointSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class LoyaltyService {

  @Autowired
  LoyaltyRepository loyaltyRepository;

  final Integer DEFAULT_USER_POINT = 0;
  final Double POINT_INCREASED_MULTIPLIER = 1.0;
  final  Double POINT_USAGE_MULTIPLIER = 1.5;

  public List<Loyalty> getAll() {
    return loyaltyRepository.findAll();
  }

  public Loyalty createOrUpdatePoint(UserPointSchema userPoint) {
    Optional<Loyalty> loyalty = loyaltyRepository.findById(UUID.fromString(userPoint.getUserId()));
    if (loyalty.isEmpty()) {
      return loyaltyRepository.save(new Loyalty(UUID.fromString(userPoint.getUserId()), calculateLoyaltyPoint(userPoint.getPrevPoint(), userPoint.getPoint())));
    } else {
      Loyalty updatedLoyalty = loyalty.get();
      updatedLoyalty.setLoyaltyPoint(updatedLoyalty.getLoyaltyPoint() + calculateLoyaltyPoint(userPoint.getPrevPoint(), userPoint.getPoint()));
      return loyaltyRepository.save(updatedLoyalty);
    }
  }

  private Integer calculateLoyaltyPoint(Integer prevPoint, Integer point) {
    if (prevPoint < point) {
      return (int) Math.ceil((POINT_INCREASED_MULTIPLIER * Math.abs(point - prevPoint)));
    } else if (prevPoint > point) {
      return (int) Math.ceil((POINT_USAGE_MULTIPLIER * Math.abs(point - prevPoint)));
    } else {
      return DEFAULT_USER_POINT;
    }
  }
}
