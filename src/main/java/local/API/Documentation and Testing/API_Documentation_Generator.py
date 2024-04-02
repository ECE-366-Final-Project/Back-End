import json

Status = {
    'MESSAGE' : 'String'
}

Ping = {
    'MESSAGE' : 'String'
}

PlaySlots = {
    'MESSAGE' : 'String',
    'PAYOUT_ID' : 'int',
    'PAYOUT' : 'double',
    'WINNINGS' : 'double'
}

NewBlackjack = {
    'MESSAGE': 'String',
    'PLAYER_SCORE': 'int',
    'PLAYERS_CARDS': 'String',
    'DEALERS_CARDS': 'String',
    'GAME_ENDED': 'bool'
}

UpdateBlackjack = {
    'GAME_RESULT' : 'String',
    'WINNER' : 'String',
    'PLAYER_SCORE' : 'int',
    'DEALER_SCORE' : 'int',
    'PAYOUT' : 'double',
    'GAME_ENDED' : 'bool',
    'PLAYERS_CARDS' : 'String',
    'DEALERS_CARDS' : 'String'
}

LogIn = {
  'MESSAGE': 'String',
  'token': 'String'
}

CreateUser = {
  'MESSAGE': 'String',
}

Deposit = {
  'MESSAGE': 'String'
}

Withdraw = {
  'MESSAGE': 'String'
}

mappings = {
    'Status' : Status,
    'Ping' : Ping,
    'PlaySlots' : PlaySlots,
    'NewBlackjack' : NewBlackjack,
    'UpdateBlackjack' : UpdateBlackjack,
    'LogIn' : LogIn,
    'CreateUser' : CreateUser,
    'Deposit' : Deposit,
    'Withdraw' : Withdraw
}

with open('docs.json', 'w') as outfile:
    json.dump(mappings, outfile)