##Command line Interface for interacting With Backend
## Written in Python as ducktyping is clutch a.f

##Prereqs: Install request
import requests

head = "http://localhost:8080/"
EXIT_CODES = []

def query(func,payload):
    data = requests.get(head + func, payload)
    return parse(data)


# needed to parse HTTP requests: VERY VERY BAD
def parse(query_load):
    str = query_load.text
    words = str.split(',')
    if words[0] == "400":
        raise Exception("Something went wrong: error from API is %s" % words[1:])
    return words
    #Status code is always first element in word


def play_slots(ID,bet):
    winnings = 0
    payload = {
        "userID": ID,
        "bet": bet
    }
    data = query("PlaySlots",payload)
    payout = int(data[1])
    print("You rolled a %s" % data[3][:-1])
    return payout

def play_blackjack(ID,initial_bet):
    multiplier = -1
    hand = []
    dealer_hand = []
    ## pull new Game
    code = 0
    ## This is while it's not the exist code
    while(not code in EXIT_CODES):
        print("Your hand is: ", hand)
        print("The dealer has: ", dealer_hand)
        shd = input("Would you like to stand, hit, or double down?")
        cmd = "S"
        match shd.lower():
            ##Set cmd to whatever command is
            case "stand":
                pass
                ##Set cmd to whatever command is
            case "hit":
                pass
            case "double down":
                pass
            ## Make request to update_game() with this information 
            ## Set multiplier
            
    return multiplier



## Stores information about the session, such as userID & other relevant user info
if __name__ == "__main__":
    username = input("Please enter your username: ")
    usernd = {"username" : username}
    userdata = query("UserInfo", usernd)
    if(userdata[0] == "300"):
        code = query("CreateUser", usernd)[0]
        userdata = query("UserInfo", usernd)
    #Get userID, balance here:
    userid = int(userdata[1])
    #messy ahh call but it has to be done
    session = True
    while(session):
        userdata = query("UserInfo", usernd)
        balance = int(userdata[2][:-1])
        print("You have $%d" % balance)
        print("Would you like to make a deposit?")
        dep = (input().lower() == "y")
        if dep:
            dep_amnt = int(input("Enter deposit amount: "))
            dep_payload = { "userID": userid,
                            "amount": dep_amnt}
            query("Deposit", dep_payload)
            userdata = query("UserInfo", usernd)
            balance = int(userdata[2][:-1])

        print("Would you like to play Slots?")
        game = input()
        bet = int(input("Please specify your starting bet: "))
        while(bet > balance):
            bet = int(input("Invalid bet, please enter a value less than your balance of %d" % balance))
        payout = -1
        match game.lower():
            case "slots":
               payout = play_slots(userid,bet)
            case "blackjack":
                multiplier = play_blackjack(userid,bet)
            case _:
                session = False
        if(payout > bet):
            print("You won $ %d!" % payout)
        elif(-1 != payout):
            print("You've recieved %d" % payout)
        
        print("Would you like to make a withdrawal?")
        wth= (input().lower() == "y")
        if wth:
            userdata = query("UserInfo", usernd)
            balance = int(userdata[2][:-1])
            wth_amnt = int(input("Enter withdrawal amount: "))
            while(wth_amnt > balance):
                wth_amnt = int(input("Too much! Your balance is %d. Enter withdrawal amount: " % balance))
            wth = { "userID": userid,
                "amount": wth_amnt}
            query("Withdrawal", wth)

        print("Would you like to play again?")
        session = (input().lower() == "y")
