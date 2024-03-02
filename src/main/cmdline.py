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
    if words[0] == "400" :
        raise Exception("Something went wrong: error from API is %s" % words[1]) 
    return words
    #Status code is always first element in word


def play_slots(ID,bet):
    call = "PlaySlots"
    params = {'userID':ID,
              'bet':str(bet)}
    data = query(call, params)
    code = int(data[0])
    #PayoutID is currently set to the 3rd element
    print("You rolled a %s" % data[3])
    winnings = float(data[2])
    return winnings

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
        match shd.lower():
            case "stand":
                pass
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
    code = query("CreateUser", {"username": username})[0]
    userdata = query("UserInfo", username)
    #Get userID, balance here:
    userid = int(userdata[1])
    balance = int(userdata[2])
    session = True
    winnings = -1
    while(session):
        ## Should get user balance here
        print(" Hi %s, Would you like to play Slots, or Blackjack?" % (username))
        game = input()
        bet = int(input("Please specify your starting bet: "))
        while(bet > balance):
            bet = int(input("Invalid bet, please enter a value less than your balance of %d: " % (balance)))

        match game.lower():
            case "slots":
               winnings = play_slots(userid,bet)
            case "blackjack":
                 multiplier = play_blackjack(userid,bet)
            case "No":
                break

        if winnings > bet:
            print("You won $ %d!" % winnings)
        else:
            print("You've recieved %d" % winnings)
        print("Would you like to play again?")
        session = (input().lower() == "y")
