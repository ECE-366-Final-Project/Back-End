import json
import requests
def printResponse(response: requests.Response) -> None:
    print(json.dumps(response.json(), indent=2), end='\n\n')

urlLogin ="http://localhost:8081/LogIn?username=admin&passkey=170ffa3b63148dce14912b378ff5c1e8b1108bdb73841723a335a01ec91ac6a8"


headers = {
    'Content-Type': 'application/json'
}

with open('RouletteRequest.json') as f:
    d = f.read();
print(d)

login: requests.Request = requests.get(urlLogin, headers=headers)
printResponse(login)

token: str = login.json()["TOKEN"]

amount: float = 10_000_000.00
urlDeposit = f'http://localhost:8081/Deposit?token={token}&amount={amount}'
deposit: requests.Request = requests.get(urlDeposit, headers=headers)
printResponse(deposit)

urlPlay = f'http://localhost:8081/PlayRoulette?token={token}'

response: requests.Request = requests.post(urlPlay, headers=headers, data=d)
printResponse(response)
