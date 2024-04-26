import json
import requests

url = "http://localhost:8081/PlayRoulette?token=1"

headers = {
    'Content-Type': 'application/json'
}

with open('RouletteRequest.json') as f:
    d = f.read();
print(d)

response = requests.post(url, headers=headers, data=d)
