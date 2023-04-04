CREATE (a:QRGeneratorNode { qrGeneratorId: "8cc71d34-08dd-43d0-aa66-3206de9f2f65", location: "Bogor", type: "STASIUN" })
CREATE (b:QRGeneratorNode { qrGeneratorId: "9675ef3b-4a48-4316-96fb-90da047170c7", location: "Cilebut", type: "STASIUN" })
CREATE (c:QRGeneratorNode { qrGeneratorId: "06368f2c-1190-4143-836e-0da7b6611d93", location: "Bojong", type: "STASIUN" })
CREATE (d:QRGeneratorNode { qrGeneratorId: "80591c2c-7c10-4e20-b629-819de1b83b65", location: "Citayam", type: "STASIUN" })
CREATE (e:QRGeneratorNode { qrGeneratorId: "4af39cca-9f70-456b-945d-f1a72c99a0c5", location: "Depok", type: "STASIUN" })
CREATE (f:QRGeneratorNode { qrGeneratorId: "701c860c-2821-4d60-8ee1-735b651dee45", location: "Depok Baru", type: "STASIUN" })
CREATE (g:QRGeneratorNode { qrGeneratorId: "897ac791-4e0b-487d-b58a-9004983d52a6", location: "Pondok Cina", type: "STASIUN" })
CREATE (h:QRGeneratorNode { qrGeneratorId: "a2b7f435-2d52-47c7-b080-a11a5eefc0ba", location: "UI", type: "STASIUN" })
CREATE (i:QRGeneratorNode { qrGeneratorId: "8a8ce285-25e6-45cc-a903-e3e52c093cfc", location: "UP", type: "STASIUN" })
CREATE (j:QRGeneratorNode { qrGeneratorId: "17f1164e-f2e0-48b0-9fa0-b0261b52deb5", location: "Lenteng Agung", type: "STASIUN" })
CREATE (k:QRGeneratorNode { qrGeneratorId: "11c259fb-12bf-42c4-b24e-50c0ef9d52ee", location: "Tanjung Barat", type: "STASIUN" })
CREATE (l:QRGeneratorNode { qrGeneratorId: "5dfca0d9-1587-4108-8f8c-5b16a6d33317", location: "Pasar Minggu", type: "STASIUN" })
CREATE (m:QRGeneratorNode { qrGeneratorId: "3dd88133-fd53-4559-a009-2509df78f638", location: "Pasar Minggu Baru", type: "STASIUN" })
CREATE (n:QRGeneratorNode { qrGeneratorId: "4ab73125-7ec4-4a23-9f99-04d28c909948", location: "Duren Kalibata", type: "STASIUN" })
CREATE (o:QRGeneratorNode { qrGeneratorId: "123f4e83-7b92-498d-afd4-172ccb9edd37", location: "Cawang", type: "STASIUN" })
CREATE (p:QRGeneratorNode { qrGeneratorId: "8a32ac72-4954-484d-8d00-fd5dfb7daaf8", location: "Tebet", type: "STASIUN" })
CREATE (q:QRGeneratorNode { qrGeneratorId: "214d2097-ca45-4728-ae55-4150a3dc5145", location: "Manggarai", type: "STASIUN" })
CREATE (r:QRGeneratorNode { qrGeneratorId: "40c8b365-e9fe-45b3-93e9-5bfbdc1d1775", location: "Cikini", type: "STASIUN" })
CREATE (s:QRGeneratorNode { qrGeneratorId: "727733e9-d077-4159-99b5-a510b3c5920a", location: "Gondangdia", type: "STASIUN" })
CREATE (t:QRGeneratorNode { qrGeneratorId: "7991fbad-3de1-407d-9549-e1def846825e", location: "Juanda", type: "STASIUN" })
CREATE (u:QRGeneratorNode { qrGeneratorId: "08c09806-2d2a-40eb-bebf-4629b6ed2d4d", location: "Sawah Besar", type: "STASIUN" })
CREATE (v:QRGeneratorNode { qrGeneratorId: "92b4ebfa-2903-4bb3-8753-08936407361a", location: "Mangga Besar", type: "STASIUN" })
CREATE (w:QRGeneratorNode { qrGeneratorId: "dcf7ea82-d2d9-47cf-ab5b-c8325c298be2", location: "Jayakarta", type: "STASIUN" })
CREATE (x:QRGeneratorNode { qrGeneratorId: "17879845-c3ac-4839-b441-fef8f75eb91f", location: "Jakarta Kota", type: "STASIUN" })

CREATE
(a)-[:OUTGOING_NODE]->(b),
(a)<-[:INCOMING_NODE]-(b),
(b)-[:OUTGOING_NODE]->(c),
(b)<-[:INCOMING_NODE]-(c),
(c)-[:OUTGOING_NODE]->(d),
(c)<-[:INCOMING_NODE]-(d),
(d)-[:OUTGOING_NODE]->(e),
(d)<-[:INCOMING_NODE]-(e),
(e)-[:OUTGOING_NODE]->(f),
(e)<-[:INCOMING_NODE]-(f),
(f)-[:OUTGOING_NODE]->(g),
(f)<-[:INCOMING_NODE]-(g),
(g)-[:OUTGOING_NODE]->(h),
(g)<-[:INCOMING_NODE]-(h),
(h)-[:OUTGOING_NODE]->(i),
(h)<-[:INCOMING_NODE]-(i),
(i)-[:OUTGOING_NODE]->(j),
(i)<-[:INCOMING_NODE]-(j),
(j)-[:OUTGOING_NODE]->(k),
(j)<-[:INCOMING_NODE]-(k),
(k)-[:OUTGOING_NODE]->(l),
(k)<-[:INCOMING_NODE]-(l),
(l)-[:OUTGOING_NODE]->(m),
(l)<-[:INCOMING_NODE]-(m),
(m)-[:OUTGOING_NODE]->(n),
(m)<-[:INCOMING_NODE]-(n),
(n)-[:OUTGOING_NODE]->(o),
(n)<-[:INCOMING_NODE]-(o),
(o)-[:OUTGOING_NODE]->(p),
(o)<-[:INCOMING_NODE]-(p),
(p)-[:OUTGOING_NODE]->(q),
(p)<-[:INCOMING_NODE]-(q),
(q)-[:OUTGOING_NODE]->(r),
(q)<-[:INCOMING_NODE]-(r),
(r)-[:OUTGOING_NODE]->(s),
(r)<-[:INCOMING_NODE]-(s),
(s)-[:OUTGOING_NODE]->(t),
(s)<-[:INCOMING_NODE]-(t),
(t)-[:OUTGOING_NODE]->(u),
(t)<-[:INCOMING_NODE]-(u),
(u)-[:OUTGOING_NODE]->(v),
(u)<-[:INCOMING_NODE]-(v),
(v)-[:OUTGOING_NODE]->(w),
(v)<-[:INCOMING_NODE]-(w),
(w)-[:OUTGOING_NODE]->(x),
(w)<-[:INCOMING_NODE]-(x)