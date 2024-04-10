import requests
import random
import json
from http.client import responses as codes

def printResponse(response: requests.Response) -> None:
    print(json.dumps(response.json(), indent=2), end='\n\n')

loop: int = int(63*random.random())+1

iteration: int = 2

for i in range(loop):
    num: int = i

    username: str = f'user_{str(iteration)}_{num+int(random.random()*100)}'
    passkey: str = f'password'

    port: int = 8080

    url: str = f'http://127.0.0.1:{port}'

    responses: list[requests.Response] = []

    response: requests.Response = requests.get(f'{url}/CreateUser?username={username}&passkey={passkey}')
    print("CreateUser", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

    response: requests.Response = requests.get(f'{url}/LogIn?username={username}&passkey={passkey}')
    print("LogIn", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

    session_token: str = response.json()["TOKEN"]

    amount: float = 10_000_000.00*random.random()
    response = requests.get(f'{url}/Deposit?token={session_token}&amount={amount}')
    print("Deposit", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

    amount = 10_000.00*random.random()
    response = requests.get(f'{url}/Withdraw?token={session_token}&amount={amount}')
    print("Withdraw", response.status_code, codes[response.status_code], sep=': ')
    printResponse(response)
    responses.append(response)

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

    slots_amount: int = int(31*random.random())+1

    for _ in range(slots_amount):
        bet: float = 10.00
        response = requests.get(f'{url}/PlaySlots?token={session_token}&bet={bet}')
        print("PlaySlots", response.status_code, codes[response.status_code], sep=': ')
        printResponse(response)
        responses.append(response)