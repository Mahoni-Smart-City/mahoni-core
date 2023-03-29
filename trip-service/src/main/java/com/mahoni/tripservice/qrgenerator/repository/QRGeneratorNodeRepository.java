package com.mahoni.tripservice.qrgenerator.repository;

import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QRGeneratorNodeRepository extends Neo4jRepository<QRGeneratorNode, Long> {

  @Query("MATCH p=shortestPath((a:QRGeneratorNode { qrGeneratorId: $node1 })-[*..]->(b:QRGeneratorNode { qrGeneratorId: $node2 })) RETURN p")
  List<QRGeneratorNode> shortestPath(@Param("node1") UUID node1, @Param("node2") UUID node2);

  @Query("MATCH (n) RETURN n")
  List<QRGeneratorNode> findAll();
}
