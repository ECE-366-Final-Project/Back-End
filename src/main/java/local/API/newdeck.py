s = ['H', 'C', 'D', 'S']
r = ['A', '2', '3', '4', '5', '6', '7', '8', '9', 'J', 'Q', 'K']

deck = ""

for i in s:
    for j in r:
        deck += j + i
        
print(deck)