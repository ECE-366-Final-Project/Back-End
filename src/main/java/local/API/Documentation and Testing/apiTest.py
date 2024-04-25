import requests
import random
import json
from http.client import responses as codes

def printResponse(response: requests.Response) -> None:
    print(json.dumps(response.json(), indent=2), end='\n\n')


iteration: int = 2

num: int = 20

username: str = f'user_{str(iteration)}_{num+int(random.random()*100)}'
passkey: str = f'password'

port: int = 8081

url: str = f'http://127.0.0.1:{port}'

responses: list[requests.Response] = []

# test CreateUser
response: requests.Response = requests.get(f'{url}/CreateUser?username={username}&passkey={passkey}')
print("CreateUser", response.status_code, codes[response.status_code], sep=': ')
printResponse(response)
responses.append(response)

# test LogIn
response: requests.Response = requests.get(f'{url}/LogIn?username={username}&passkey={passkey}')
print("LogIn", response.status_code, codes[response.status_code], sep=': ')
printResponse(response)
responses.append(response)

session_token: str = response.json()["TOKEN"]

# test deposit
d_amount: float = 10_000_000.00*random.random()
response = requests.get(f'{url}/Deposit?token={session_token}&amount={d_amount}')
print("Deposit", response.status_code, codes[response.status_code], sep=': ')
printResponse(response)
responses.append(response)


# test Withdraw
w_amount: float = 10_000.00*random.random()
response = requests.get(f'{url}/Withdraw?token={session_token}&amount={w_amount}')
print("Withdraw", response.status_code, codes[response.status_code], sep=': ')
printResponse(response)
responses.append(response)


# test Blackjack
blackjack_amount: int = int(31*random.random())+1

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
    
    move2: str = 'double_down'
    if (random.random() >= 0.5):
        move2 = 'stand'
    response = requests.get(f'{url}/UpdateBlackjack?token={session_token}&move={move2}')
    print("UpdateBlackjack", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

# test Slots
slots_amount: int = int(31*random.random())+1

for _ in range(slots_amount):
    bet: float = 10.00
    response = requests.get(f'{url}/PlaySlots?token={session_token}&bet={bet}')
    print("PlaySlots", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)
