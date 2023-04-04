package com.mahoni.tripservice.qrgenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRToken {
  LocalDateTime startAt;
  LocalDateTime expiredAt;
  UUID QRGeneratorId;
}
