##Command line Interface for interacting With Backend
## Written in Python as ducktyping is clutch a.f

##Prereqs: Install request
import requests

EXIT_CODES = []

def play_slots(ID,bet):
    multiplier = 0
    ## This is where a request to the server would go
    ##
    return multiplier

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
    UserId = int(input("Please enter your User ID"))

    ## Should get user balance & data here
    session = True
    while(session):
        ## Should get user balance here
        print("Would you like to play Slots, or Blackjack?")
        game = input()
        bet = int(input("Please specify your starting bet: "))
        multiplier = -1
        match game.lower():
            case "slots":
               multiplier = play_slots(UserId,bet)
            case "Blackjack":
                multiplier = play_blackjack(UserId,bet)
            case "No":
                session = False

        if(multiplier > 1):
            print("You won $ %d!", multiplier*bet)
        elif(multiplier == 1):
            print("You drew: Money returned")
        elif(multiplier < 1):
            print("You've recieved %d", multiplier*bet)

        print("Would you like to play again?")
        sucess = (input().lower() == "y")
