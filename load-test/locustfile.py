from locust import HttpUser, task
from urllib.parse import quote
import datetime
import random
import string
import time
 
BASE_URL = "/api/v1"
 
url_auth = f"{BASE_URL}/auth"
url_redeem = f"{BASE_URL}/redeem"
url_trip = f"{BASE_URL}/trips"
url_user = f"{BASE_URL}/users"
url_voucher = f"{BASE_URL}/vouchers"
url_qr_generators = f"{BASE_URL}/qr-generators"
 
# host_user = "http://localhost:8088"
# host_voucher = "http://localhost:8082"
# host_air = "http://localhost:8086"
# host_trip = "http://localhost:8085"

host_user = "http://34.101.221.17:8080"
host_voucher = "http://35.219.27.149:8080"
host_air = "http://34.101.57.187:8080"
host_trip = "http://35.219.13.108:8080"
 
voucher_type = ["FOOD_AND_BEVERAGES","GROCERIES","ENTERTAINMENT","TELECOMMUNICATION","HEALTH_AND_BEAUTY"]
sex = ["MALE","FEMALE","NOT_KNOWN","NOT_APPLICABLE"]
qrgenerator = [
    "57a7e9b5-ee6d-422f-b86f-c5a1130ab78d",
    "3d76d166-29a8-4435-967c-92f7b06d92c8",
    "fca9d857-edec-467c-ae11-e69e26eca1c6",
    "70e012a9-b080-42b0-9245-28251ea71006",
    "52147e18-82e5-4c90-866a-09122d32e4ea",
    "4042c87d-737f-44bf-9ed0-0a6614522203",
    "8c974303-1ec4-47ba-a17c-ef1ac22933ae",
    "62830ca8-2b41-4074-8a60-c8a9346faba0",
    "6fdb8368-13f1-4946-9bf9-96c78e2f2648",
    "e7d43461-9451-411a-8af6-762c1975b882",
    "2c1567fa-768e-4ba8-8801-ad15ba3cea68",
    "6be4b957-1ecc-4b58-980b-5fcc2b8d016e",
    "04a784da-fdc1-4afc-b99e-b7ba0861acf7",
    "3de669fd-5ec2-49d7-b992-e19a34dbae6d",
    "8d980cc8-3b7d-4c5a-af7f-cae1c82b015a",
    "354b5d24-7f5b-4623-86bd-e43324843ce4",
    "6bec4acc-83e2-4ea0-8a8b-34fe1603c4f8",
    "5e3a6acb-4732-4d2f-923d-6091daa78c69",
    "f5c841cb-2ec2-4d6f-b57c-73171ad4960b",
    "4ca9fa97-70a0-4b36-ba57-451bf7909008",
    "5fedd2f9-9807-45fa-aab6-8efd74710400",
    "c3cd9fb6-7971-4aba-833c-916eb3b02225",
    "49b9c86e-928e-4cb5-aa8f-649169be51fd",
    # "52d49e42-fedb-4454-a66c-ae224f2dcf34"
    # "0031c488-8b59-47c6-807a-95d0a64a5e3e",
    # "2fc6ff2d-548d-4fd9-b982-caf724cfb388"
    ]
qr_destination = "52d49e42-fedb-4454-a66c-ae224f2dcf34"
air_sensors = [
 100927,
 35787,
 18453,
 42975,
 35960,
 107276,
 18450,
 16692,
 40905,
 38596,
 87300,
 96571,
 18456,
 105476,
 14266,
 100041
 ]

headers = {
        'Accept': '*/*',
        "Accept-Encoding": "*",
        "Connection": "keep-alive"
}
 
class PerformanceTest(HttpUser):
    
   
    
    @task
    def air_quailty(self):
        response = self.client.get(f"{host_air}{BASE_URL}/air-sensors/{random.choice(air_sensors)}", name=f"/air-sensors/[sensor_id]", headers=headers)  
        assert response.ok, response.reason
        if not response.ok:
            print(response.text)
            response.raise_for_status()
        response = self.client.get(f"{host_air}{BASE_URL}/air-quality/loc/MENTENG", name=f"/air-quality/loc/[location]", headers=headers)
        if not response.ok:
            print(response.text)
            response.raise_for_status()  
        
    @task
    def scenario(self):
 
        # Register Merchant
        merchant = random.choices(string.ascii_lowercase, k=16) + random.choices(string.digits, k=5)
        merchant = "".join(merchant)
        data = {
            "username": f"{merchant}",
            "name": f"{merchant}",
            "email": f"{merchant}@mail.com",
            "password": f"{merchant}"
            }
        response = self.client.post(f"{host_voucher}{url_auth}/register", json=data, headers=headers)
        if not response.ok:
            print(response.text)
            response.raise_for_status()
        token = response.json()['token']
        merchant_id = response.json()['id']
 
        # # Authenticate Merchant
        # data = {
        #     "username": f"{merchant}",
        #     "password": f"{merchant}"
        #     }
        # response = self.client.post(f"{host_voucher}{url_auth}/authenticate", json=data)
        # token = response.json()['token']
        # merchant_id = response.json()['id']
 
        # Create Voucher
        name = "".join(random.choices(string.ascii_lowercase, k=16))
        data = {
            "name": f"The {name}",
            "description": f"This is {random.choices(string.ascii_lowercase, k=5)}",
            "type": f"{random.choice(voucher_type)}",
            "point": random.choice(string.digits),
            "startAt": f"{datetime.datetime.now().isoformat()}",
            "expiredAt": "2025-01-01T00:00:00"
        }
        response = self.client.post(f"{host_voucher}{url_voucher}", json=data, headers={"authorization": "Bearer " + token, 'Accept': '*/*', "Accept-Encoding": "*",
        "Connection": "keep-alive"})
        if not response.ok:
            print(response.text)
            response.raise_for_status()
        voucher_id = response.json()['id']
 
        # Create Redeem Voucher
        data = {
            "voucherId": f"{voucher_id}",
            "redeemCode": f"{random.choices(string.ascii_lowercase + string.digits, k=8)}",
            "expiredAt": "2025-01-01T00:00:00"
        }
        response = self.client.post(f"{host_voucher}{url_voucher}/detail", json=data, headers={"authorization": "Bearer " + token, 'Accept': '*/*', "Accept-Encoding": "*",
        "Connection": "keep-alive"})
        if not response.ok:
            print(response.text)
            response.raise_for_status()
        redeem_voucher_id = response.json()['id']
 
        # Create User
        username = random.choices(string.ascii_lowercase, k=16) + random.choices(string.digits, k=5)
        username = "".join(username)
        data = {
            "username": f"{username}",
            "name": f"{username}",
            "email": f"{username}@mail.com",
            "sex": f"{random.choice(sex)}",
            "yearOfBirth": "2000"
        }
        response = self.client.post(f"{host_user}{url_user}", json=data, headers=headers)
        if not response.ok:
            print(response.text)
            response.raise_for_status()
        user_id = response.json()['id']
 
        # Scan In and Scan Out three times
        for i in range(6):
            # Generate QR
            qrgenerator_id = random.choice(qrgenerator)
            if i % 2 != 0:
                qrgenerator_id = qr_destination
            response = self.client.get(f"{host_trip}{url_qr_generators}/{qrgenerator_id}/generate-qr", name=f"{url_qr_generators}/[qrgenerator_id]/generate-qr", headers=headers)
            if not response.ok:
                print(response.text)
                response.raise_for_status()   
            qrcode_image = quote(f"{response.text}", safe="!~*'()")
 
            # Scan QR
            response = self.client.get(f"{host_trip}{url_qr_generators}/decode-qr?data={qrcode_image}", name=f"{url_qr_generators}/decode-qr", headers=headers)
            if not response.ok:
                print("decode", qrcode_image, response.text)
                print(response.text)
                response.raise_for_status()
            qrcode_token = response.content.decode()
            time.sleep(0.1)
             
            # Create Trip
            data = {
                "qrToken": f"{qrcode_token}",
                "userId": f"{user_id}",
                "scanPlaceId": f"{qrgenerator_id}"
            }
            response = self.client.post(f"{host_trip}{url_trip}", json=data, headers=headers)
            if not response.ok:
                print("trips", data)
                print(response.text)
                response.raise_for_status()
            time.sleep(0.1)    
 
        ## ERROR Ktable not ready yet
        time.sleep(1)
 
        # Redeem Voucher
        data = {
            "voucherId": f"{voucher_id}",
            "userId": f"{user_id}"
        }
        response = self.client.post(f"{host_voucher}{url_redeem}", json=data, headers=headers)
        if not response.ok:
            print("redeem", data)
            print(response.text)
            response.raise_for_status()
        redeem_voucher_id = response.json()['id']
 
        # Set Redeemed
        data = {
            "voucherId": f"{voucher_id}",
            "userId": f"{user_id}"
        }
        response = self.client.post(f"{host_voucher}{url_redeem}/{redeem_voucher_id}", json=data, name=f"{url_redeem}/[redeem_voucher_id]", headers=headers)
        if not response.ok:
            print("set redeemed", data)
            print(response.text)
            response.raise_for_status()