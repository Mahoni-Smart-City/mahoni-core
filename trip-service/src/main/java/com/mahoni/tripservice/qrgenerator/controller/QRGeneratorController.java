package com.mahoni.tripservice.qrgenerator.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.mahoni.tripservice.qrgenerator.dto.QRGeneratorRequest;
import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import com.mahoni.tripservice.qrgenerator.service.QRGeneratorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/qr-generators")
@Slf4j
public class QRGeneratorController {

  @Autowired
  QRGeneratorService qrGeneratorService;

  @PostMapping
  public ResponseEntity<QRGenerator> post(@Valid @RequestBody QRGeneratorRequest request) {
    try {
      QRGenerator newQrGenerator = qrGeneratorService.create(request);
      return ResponseEntity.ok(newQrGenerator);
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  private ResponseEntity<List<QRGenerator>> getAll() {
    List<QRGenerator> allQrGenerators = qrGeneratorService.getAll();
    return ResponseEntity.ok(allQrGenerators);
  }

  @GetMapping("/{id}")
  public ResponseEntity<QRGenerator> get(@PathVariable("id") UUID id ) {
    try {
      QRGenerator QRGenerator = qrGeneratorService.getById(id);
      return ResponseEntity.ok(QRGenerator);
    } catch (QRGeneratorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<QRGenerator> delete(@PathVariable("id") UUID id) {
    try {
      QRGenerator deletedQrGenerator = qrGeneratorService.deleteById(id);
      return ResponseEntity.ok(deletedQrGenerator);
    } catch (QRGeneratorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable("id") UUID id, @Valid @RequestBody QRGeneratorRequest request) {
    try {
      QRGenerator updatedQrGenerator = qrGeneratorService.updateById(id, request);
      return ResponseEntity.ok(updatedQrGenerator);
    } catch (QRGeneratorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{id}/generate-qr")
  public ResponseEntity<String> generateQR(@PathVariable("id") UUID qrGeneratorId) {
    try {
      String token = qrGeneratorService.generateQRToken(qrGeneratorId);
      return ResponseEntity.ok(QRGeneratorService.generateQRCodeImage(token));
    } catch (QRGeneratorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @GetMapping("/validate-qr")
  public Boolean validateQR(@RequestParam("token") String token, @RequestParam("qr-generator-id") UUID id) {
    try {
      return qrGeneratorService.validateQRToken(token, id);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @GetMapping("/nodes")
  public ResponseEntity<List<QRGeneratorNode>> getNode() {
    return ResponseEntity.ok(qrGeneratorService.getAllNode());
  }

  @GetMapping("/shortest-path")
  public ResponseEntity<List<QRGeneratorNode>> shortestPath(@RequestParam("node1") UUID node1, @RequestParam("node2") UUID node2 ) {
    return ResponseEntity.ok(qrGeneratorService.shortestPathBetweenNodes(node1, node2));
  }
}
