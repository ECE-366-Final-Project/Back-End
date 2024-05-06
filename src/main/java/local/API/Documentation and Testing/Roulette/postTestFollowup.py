import time
import json
import requests
def printResponse(response: requests.Response) -> None:
    print(json.dumps(response.json(), indent=2), end='\n\n')

url: str = 'http://localhost'
port: int = '8081'

username: str = 'admin'
passkey: str = '170ffa3b63148dce14912b378ff5c1e8b1108bdb73841723a335a01ec91ac6a8'

urlLogin = f'{url}:{port}/LogIn?username={username}&passkey={passkey}'


headers = {
    'Content-Type': 'application/json'
}

#with open('RouletteRequest.json') as f:
with open('RouletteRequestMultiplayer.json') as f:
    d = f.read();
print(d)

login: requests.Request = requests.get(urlLogin, headers=headers)
printResponse(login)

token: str = login.json()["TOKEN"]

amount: float = 10_000_000.00
urlDeposit = f'{url}:{port}/Deposit?token={token}&amount={amount}'
deposit: requests.Request = requests.get(urlDeposit, headers=headers)
printResponse(deposit)

urlPlay = f'{url}:{port}/PlayRoulette?token={token}'

response: requests.Request = requests.post(urlPlay, headers=headers, data=d)
printResponse(response)


lobby: str = response.json()["LOBBY"]

urlFollowup = f'{url}:{port}/GetRouletteLobby?token={token}&lobby_id={lobby}'
response: requests.Request = requests.get(urlFollowup, headers=headers)
printResponse(response)

time.sleep(100)
response: requests.Request = requests.get(urlFollowup, headers=headers)
printResponse(response)

