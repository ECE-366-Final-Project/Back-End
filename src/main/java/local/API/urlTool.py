import requests
import json
from http.client import responses as codes

def printResponse(response: requests.Response) -> None:
    print(json.dumps(response.json(), indent=2), end='\n\n')

username: str = 'flof'
passkey: str = 'password'

port: int = 8080

url: str = f'http://127.0.0.1:{port}'

responses: list[requests.Response] = []

response: requests.Response = requests.get(f'{url}/LogIn?username={username}&passkey={passkey}')
print("LogIn", response.status_code, codes[response.status_code], sep=': ')
printResponse(response)
responses.append(response)

session_token: str = response.json()["token"]

amount: float = 10_000_000.00
response = requests.get(f'{url}/Deposit?token={session_token}&amount={amount}')
print("Deposit", response.status_code, codes[response.status_code], sep=': ')
printResponse(response)
responses.append(response)

amount = 12_057.63
response = requests.get(f'{url}/Withdraw?token={session_token}&amount={amount}')
print("Withdraw", response.status_code, codes[response.status_code], sep=': ')
printResponse(response)
responses.append(response)

blackjack_amount: int = 10

for _ in range(blackjack_amount):
    bet: float = 10.00
    response = requests.get(f'{url}/NewBlackjack?token={session_token}&bet={bet}')
    print("NewBlackjack", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

    move1: str = 'hit'
    response = requests.get(f'{url}/UpdateBlackjack?token={session_token}&move={move1}')
    print("UpdateBlackjack", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

    move2: str = 'stand'
    response = requests.get(f'{url}/UpdateBlackjack?token={session_token}&move={move2}')
    print("UpdateBlackjack", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

slots_amount: int = 25

for _ in range(slots_amount):
    bet: float = 10.00
    response = requests.get(f'{url}/PlaySlots?token={session_token}&bet={bet}')
    print("PlaySlots", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)