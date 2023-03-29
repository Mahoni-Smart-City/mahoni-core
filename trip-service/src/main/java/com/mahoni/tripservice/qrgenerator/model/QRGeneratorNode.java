package com.mahoni.tripservice.qrgenerator.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("QRGeneratorNode")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class QRGeneratorNode {
  @Id
  @GeneratedValue
  private Long id;
  private UUID qrGeneratorId;
  private String location;
  private String type;
}
