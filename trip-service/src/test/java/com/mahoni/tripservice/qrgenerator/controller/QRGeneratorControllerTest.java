package com.mahoni.tripservice.qrgenerator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.tripservice.qrgenerator.dto.QRGeneratorRequest;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorType;
import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import com.mahoni.tripservice.qrgenerator.service.QRGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QRGeneratorController.class)
public class QRGeneratorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private QRGeneratorService qrGeneratorService;

  @Test
  public void testPost_thenReturnNewQrGenerator() throws Exception {
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.COMMUTER, 1L, 1L);
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.COMMUTER, "1", "1");

    when(qrGeneratorService.create(any())).thenReturn(qrGenerator);

    MvcResult result = this.mockMvc.perform(post("/api/v1/qr-generators")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(qrGenerator));
    verify(qrGeneratorService).create(any());
  }

  @Test
  public void testPost_thenThrowRuntimeException() throws Exception {
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.COMMUTER, "1", "1");

    when(qrGeneratorService.create(any())).thenThrow(RuntimeException.class);

    this.mockMvc.perform(post("/api/v1/qr-generators")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testGetAll_thenReturnQrGenerators() throws Exception {
    List<QRGenerator> qrGenerators = new ArrayList<>();

    when(qrGeneratorService.getAll()).thenReturn(qrGenerators);

    MvcResult result = this.mockMvc.perform(get("/api/v1/qr-generators"))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(qrGenerators));
    verify(qrGeneratorService).getAll();
  }

  @Test
  public void testGetById_thenReturnQrGenerator() throws Exception {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.COMMUTER, 1L);

    when(qrGeneratorService.getById(any())).thenReturn(qrGenerator);

    MvcResult result = this.mockMvc.perform(get("/api/v1/qr-generators/{id}", id))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(qrGenerator));
    verify(qrGeneratorService).getById(any());
  }

  @Test
  public void testGetById_thenThrowQRGeneratorNotFound() throws Exception {
    when(qrGeneratorService.getById(any())).thenThrow(QRGeneratorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/qr-generators/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenReturnDeletedQrGenerator() throws Exception {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.COMMUTER, 1L);

    when(qrGeneratorService.deleteById(any())).thenReturn(qrGenerator);

    MvcResult result = this.mockMvc.perform(delete("/api/v1/qr-generators/{id}", id))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(qrGenerator));
    verify(qrGeneratorService).deleteById(any());
  }

  @Test
  public void testDelete_thenThrowQRGeneratorNotFound() throws Exception {
    when(qrGeneratorService.deleteById(any())).thenThrow(QRGeneratorNotFoundException.class);

    this.mockMvc.perform(delete("/api/v1/qr-generators/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testUpdate_thenReturnUpdatedQrGenerator() throws Exception {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator();
    qrGenerator.setId(id);
    qrGenerator.setLocation("Test");
    qrGenerator.setType(QRGeneratorType.TRANSJAKARTA);
    qrGenerator.setSensorId1(1L);
    qrGenerator.setSensorId2(1L);
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.TRANSJAKARTA, "1", "1");

    when(qrGeneratorService.updateById(any(), any())).thenReturn(qrGenerator);

    MvcResult result = this.mockMvc.perform(put("/api/v1/qr-generators/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(qrGenerator));
    verify(qrGeneratorService).updateById(any(), any());
  }

  @Test
  public void testUpdate_thenThrowQRGeneratorNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.TRANSJAKARTA, "1", "1");

    when(qrGeneratorService.updateById(any(), any())).thenThrow(QRGeneratorNotFoundException.class);

    this.mockMvc.perform(put("/api/v1/qr-generators/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testGenerateQR_thenReturnString() throws Exception {
    UUID id = UUID.randomUUID();
    String token = "TestToken";
    String generatedQr = QRGeneratorService.generateQRCodeImage(token);

    when(qrGeneratorService.generateQRToken(any())).thenReturn(token);

    MvcResult result = this.mockMvc.perform(get("/api/v1/qr-generators/{id}/generate-qr", id))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), generatedQr);
    verify(qrGeneratorService).generateQRToken(any());
  }

  @Test
  public void testGeneratedQr_thenThrowQRGeneratorNotFound() throws Exception {
    when(qrGeneratorService.generateQRToken(any())).thenThrow(QRGeneratorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/qr-generators/{id}/generate-qr", UUID.randomUUID()))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testGeneratedQr_thenThrowException() throws Exception {
    when(qrGeneratorService.generateQRToken(any())).thenThrow(JsonProcessingException.class);

    this.mockMvc.perform(get("/api/v1/qr-generators/{id}/generate-qr", UUID.randomUUID()))
      .andExpect(status().isInternalServerError())
      .andReturn();
  }

  @Test
  public void testValidateQr_thenReturnBoolean() throws Exception {
    String token = "Test";
    UUID id = UUID.randomUUID();

    when(qrGeneratorService.validateQRToken(any(), any())).thenReturn(Boolean.TRUE);

    this.mockMvc.perform(get("/api/v1/qr-generators/validate-qr")
        .param("token", token)
        .param("qr-generator-id", String.valueOf(id)))
      .andExpect(content().string("true"))
      .andReturn();
  }

  @Test
  public void testValidateQr_thenThrowJsonProcessingException() throws Exception {
    String token = "Test";
    UUID id = UUID.randomUUID();

    when(qrGeneratorService.validateQRToken(any(), any())).thenThrow(JsonProcessingException.class);

    this.mockMvc.perform(get("/api/v1/qr-generators/validate-qr")
        .param("token", token)
        .param("qr-generator-id", String.valueOf(id)))
      .andExpect(status().isInternalServerError())
      .andReturn();
  }

  @Test
  public void testGetNode_thenReturnQrGeneratorNodes() throws Exception {
    List<QRGeneratorNode> qrGeneratorNodes = new ArrayList<>();

    when(qrGeneratorService.getAllNode()).thenReturn(qrGeneratorNodes);

    MvcResult result = this.mockMvc.perform(get("/api/v1/qr-generators/nodes"))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(qrGeneratorNodes));
    verify(qrGeneratorService).getAllNode();
  }

  @Test
  public void testShortestPath_thenReturnQrGeneratorNodes() throws Exception {
    List<QRGeneratorNode> qrGeneratorNodes = new ArrayList<>();

    when(qrGeneratorService.shortestPathBetweenNodes(any(), any())).thenReturn(qrGeneratorNodes);

    MvcResult result = this.mockMvc.perform(get("/api/v1/qr-generators/shortest-path")
        .param("node1", String.valueOf(UUID.randomUUID()))
        .param("node2", String.valueOf(UUID.randomUUID())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(qrGeneratorNodes));
    verify(qrGeneratorService).shortestPathBetweenNodes(any(), any());
  }

  @Test
  public void testDecodeQR_thenReturnString() throws Exception {
    String token = "TestToken";
    String generatedQr = QRGeneratorService.generateQRCodeImage(token);
    String decodedQr = QRGeneratorService.decodeQRCodeImage(generatedQr);

    this.mockMvc.perform(get("/api/v1/qr-generators/decode-qr")
        .param("data", generatedQr))
      .andExpect(content().string(decodedQr))
      .andReturn();
  }

  @Test
  public void testDecodedQr_thenThrowException() throws Exception {
    this.mockMvc.perform(get("/api/v1/qr-generators/decode-qr")
        .param("data", "Test"))
      .andExpect(status().isInternalServerError())
      .andReturn();
  }
}
