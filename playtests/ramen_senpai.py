def sexify ( word ):
    if len(word) > 1:
        start = word[0:2]
        if start == "ex":
            return "s" + word
        else:
            return word
        
def sexify_all ( str ):
    l = str.split(" ")
    l2 = map( sexify, l )
    s = reduce( lambda w1, s: w1 + " " + s, l2)
    return s
