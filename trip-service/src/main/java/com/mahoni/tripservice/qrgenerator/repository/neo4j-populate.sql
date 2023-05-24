CREATE (a:QRGeneratorNode { qrGeneratorId: "3d76d166-29a8-4435-967c-92f7b06d92c8", location: "Bogor", type: "COMMUTER" })
CREATE (b:QRGeneratorNode { qrGeneratorId: "fca9d857-edec-467c-ae11-e69e26eca1c6", location: "Cilebut", type: "COMMUTER" })
CREATE (c:QRGeneratorNode { qrGeneratorId: "70e012a9-b080-42b0-9245-28251ea71006", location: "Bojong", type: "COMMUTER" })
CREATE (d:QRGeneratorNode { qrGeneratorId: "52147e18-82e5-4c90-866a-09122d32e4ea", location: "Citayam", type: "COMMUTER" })
CREATE (e:QRGeneratorNode { qrGeneratorId: "4042c87d-737f-44bf-9ed0-0a6614522203", location: "Depok", type: "COMMUTER" })
CREATE (f:QRGeneratorNode { qrGeneratorId: "8c974303-1ec4-47ba-a17c-ef1ac22933ae", location: "Depok Baru", type: "COMMUTER" })
CREATE (g:QRGeneratorNode { qrGeneratorId: "62830ca8-2b41-4074-8a60-c8a9346faba0", location: "Pocin", type: "COMMUTER" })
CREATE (h:QRGeneratorNode { qrGeneratorId: "6fdb8368-13f1-4946-9bf9-96c78e2f2648", location: "UI", type: "COMMUTER" })
CREATE (i:QRGeneratorNode { qrGeneratorId: "e7d43461-9451-411a-8af6-762c1975b882", location: "UP", type: "COMMUTER" })
CREATE (j:QRGeneratorNode { qrGeneratorId: "2c1567fa-768e-4ba8-8801-ad15ba3cea68", location: "Lenteng Agung", type: "COMMUTER" })
CREATE (k:QRGeneratorNode { qrGeneratorId: "6be4b957-1ecc-4b58-980b-5fcc2b8d016e", location: "Tanjung Barat", type: "COMMUTER" })
CREATE (l:QRGeneratorNode { qrGeneratorId: "04a784da-fdc1-4afc-b99e-b7ba0861acf7", location: "Pasar Minggu", type: "COMMUTER" })
CREATE (m:QRGeneratorNode { qrGeneratorId: "3de669fd-5ec2-49d7-b992-e19a34dbae6d", location: "Pasar Minggu Baru", type: "COMMUTER" })
CREATE (n:QRGeneratorNode { qrGeneratorId: "8d980cc8-3b7d-4c5a-af7f-cae1c82b015a", location: "Duren Kalibata", type: "COMMUTER" })
CREATE (o:QRGeneratorNode { qrGeneratorId: "354b5d24-7f5b-4623-86bd-e43324843ce4", location: "Cawang", type: "COMMUTER" })
CREATE (p:QRGeneratorNode { qrGeneratorId: "6bec4acc-83e2-4ea0-8a8b-34fe1603c4f8", location: "Tebet", type: "COMMUTER" })
CREATE (q:QRGeneratorNode { qrGeneratorId: "5e3a6acb-4732-4d2f-923d-6091daa78c69", location: "Manggarai", type: "COMMUTER" })
CREATE (r:QRGeneratorNode { qrGeneratorId: "f5c841cb-2ec2-4d6f-b57c-73171ad4960b", location: "Cikini", type: "COMMUTER" })
CREATE (s:QRGeneratorNode { qrGeneratorId: "4ca9fa97-70a0-4b36-ba57-451bf7909008", location: "Gondangdia", type: "COMMUTER" })
CREATE (t:QRGeneratorNode { qrGeneratorId: "5fedd2f9-9807-45fa-aab6-8efd74710400", location: "Juanda", type: "COMMUTER" })
CREATE (u:QRGeneratorNode { qrGeneratorId: "5fedd2f9-9807-45fa-aab6-8efd74710400", location: "Sawah Besar", type: "COMMUTER" })
CREATE (v:QRGeneratorNode { qrGeneratorId: "49b9c86e-928e-4cb5-aa8f-649169be51fd", location: "Mangga Besar", type: "COMMUTER" })
CREATE (w:QRGeneratorNode { qrGeneratorId: "52d49e42-fedb-4454-a66c-ae224f2dcf34", location: "Jayakarta", type: "COMMUTER" })
CREATE (x:QRGeneratorNode { qrGeneratorId: "57a7e9b5-ee6d-422f-b86f-c5a1130ab78d", location: "Jakarta Kota", type: "COMMUTER" })

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
