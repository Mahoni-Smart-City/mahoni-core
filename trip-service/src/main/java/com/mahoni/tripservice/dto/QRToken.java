package com.mahoni.tripservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRToken {
  LocalDateTime start_at;
  LocalDateTime expired_at;
  UUID QRGeneratorId;
}
