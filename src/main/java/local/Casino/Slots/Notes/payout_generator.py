import pandas as pd

symbols = [x[:-1] for x in open("slots_symbols.txt", "r").readlines()]
print(symbols)

payouts = [0.0] * 1000

def get_single_payout(s):
    if symbols[2] == "1X BAR":
        return 0.11
    elif symbols[s] == "2X BAR":
        return 0.22222
    elif symbols[s] == "3X BAR":
        return 0.33333
    elif symbols[s] == "JACKPOT":
        return 0.0
    elif symbols[s] == "SEVEN":
        return 0.77777
    return 0.0

def get_payout(index):

    index = str(index)

    while len(index) < 3:
        index = '0' + index

    s1 = int(index[0])
    s2 = int(index[1])
    s3 = int(index[2])

    # triples
    if (s1 == s2 and s1 == s3):
        if symbols[s1] == "1X BAR":
            return 11.11111
        elif symbols[s1] == "2X BAR":
            return 22.22222
        elif symbols[s1] == "3X BAR":
            return 33.33333
        elif symbols[s1] == "JACKPOT":
            return 100.0
        elif symbols[s1] == "SEVEN":
            return 77.77777
        return 5.0
        
    # doubles
    if s1 == s2 or s1 == s3 or s2 == s3:
        if s1 == s2:
            pair = s1
            unique = s3
        elif s1 == s3:
            pair = s1
            unique = s2
        else:
            pair = s2
            unique = s1
        if symbols[pair] == "1X BAR":
            return 1.11111 + get_single_payout(unique)
        elif symbols[pair] == "2X BAR":
            return 2.22222 + get_single_payout(unique)
        elif symbols[pair] == "3X BAR":
            return 3.33333 + get_single_payout(unique)
        elif symbols[pair] == "JACKPOT":
            return get_single_payout(unique)
        elif symbols[pair] == "SEVEN":
            return 7.77777 + get_single_payout(unique)
        return get_single_payout(pair) + get_single_payout(unique)

    # singles
    return get_single_payout(s1) + get_single_payout(s2) + get_single_payout(s3)

for i in range(1000):
    payouts[i] = round(get_payout(i), 5)
    print(payouts[i])

s1index = [0]*1000
s2index = [0]*1000
s3index = [*range(10)] * 100

for i in range(10):
    for j in range(100):
        s1index[100*i + j] = i

for i in range(10):
    for j in range(10):
        for k in range(10):
            s2index[100*i + 10*j+k] = j

print(s1index)
print(s2index)
print(s3index)

s1_str = [symbols[x] for x in s1index]
s2_str = [symbols[x] for x in s2index]
s3_str = [symbols[x] for x in s3index]

payouts_df = {'Symbol 1': s1_str,
              'Symbol 2': s2_str,
              'Symbol 3': s3_str,
              'Payout': payouts}
df = pd.DataFrame(payouts_df)
df.to_csv('payouts.csv', index = False)

print("\nEXPECTED VALUE: "+str(sum(payouts)/1000.0))