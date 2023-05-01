package com.mahoni.tripservice.qrgenerator.repository;

import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@DataNeo4jTest
public class QRGeneratorNodeRepositoryTest {

  @Autowired
  private QRGeneratorNodeRepository qrGeneratorNodeRepository;

  @Autowired
  Driver driver;

  @Container
  private static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.4")
    .withReuse(TestcontainersConfiguration.getInstance().environmentSupportsReuse());

  @DynamicPropertySource
  static void neo4jProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
    registry.add("spring.neo4j.authentication.username", () -> "neo4j");
    registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
  }

  static final String TEST_DATA = ""
    + " CREATE (a:QRGeneratorNode { qrGeneratorId: '8cc71d34-08dd-43d0-aa66-3206de9f2f65', location: 'Bogor', type: 'STASIUN'})"
    + " CREATE (b:QRGeneratorNode { qrGeneratorId: '9675ef3b-4a48-4316-96fb-90da047170c7', location: 'Cilebut', type: 'STASIUN'})"
    + " CREATE (c:QRGeneratorNode { qrGeneratorId: '06368f2c-1190-4143-836e-0da7b6611d93', location: 'Bojong', type: 'STASIUN'})"
    + " CREATE"
    + " (a)-[:OUTGOING_NODE]->(b),"
    + " (a)<-[:INCOMING_NODE]-(b),"
    + " (b)-[:OUTGOING_NODE]->(c),"
    + " (b)<-[:INCOMING_NODE]-(c)"
    + "";

  @Test
  public void testShortestPath() {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> tx.run(TEST_DATA).consume());

      List<QRGeneratorNode> qrGeneratorNodes = qrGeneratorNodeRepository.shortestPath(
        UUID.fromString("8cc71d34-08dd-43d0-aa66-3206de9f2f65"),
        UUID.fromString("06368f2c-1190-4143-836e-0da7b6611d93"));

      assertEquals(3, qrGeneratorNodes.size());
      session.run("MATCH (x) DETACH DELETE x");
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testFindAll() {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> tx.run(TEST_DATA).consume());

      List<QRGeneratorNode> qrGeneratorNodes = qrGeneratorNodeRepository.findAll();

      assertEquals(3, qrGeneratorNodes.size());
      session.run("MATCH (x) DETACH DELETE x");
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
