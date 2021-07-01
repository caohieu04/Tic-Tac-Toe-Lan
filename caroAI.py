import numpy as np

f = open("test.dat")
inf = int(1e9)
width = 20
height = 20
board = np.zeros([width, height], dtype=int)
mark = np.zeros([width, height], dtype=int)
DScore = [0, 1, 8, 64, 512]
AScore = [0, 3, 24, 174, 1536]
maxBreadth = 6

for line in f:
    piece = line.split(';')
    x = int(piece[0])
    y = int(piece[1])
    p = int(piece[2])
    mark[x][y] = p
print(mark)

def boardEval(player):
    global board
    board = np.zeros([width, height], dtype=int)
    ePc = 0
    eHuman = 0
    for row in range(width):
        for col in range(height - 4):
            ePc = 0
            eHuman = 0
            for i in range(5):
                if mark[row][col + i] == 1:
                    eHuman += 1
                elif mark[row][col + i] == 2:
                    ePc += 1

            if eHuman * ePc == 0 and eHuman != ePc:
                for i in range(5):
                    if mark[row][col + i] == 0:
                        if eHuman == 0:
                            if player == 1:
                                board[row][col + i] += DScore[ePc]
                            else:
                                board[row][col + i] += AScore[ePc]
                        if ePc == 0:
                            if player == 2:
                                board[row][col + i] += DScore[eHuman]
                            else:
                                board[row][col + i] += AScore[eHuman]
                        if eHuman == 4 or ePc == 4:
                            board[row][col + i] *= 2
    for row in range(width - 4):
        for col in range(height):
            ePc = 0
            eHuman = 0
            for i in range(5):
                if mark[row + i][col] == 1:
                    eHuman += 1
                elif mark[row + i][col] == 2:
                    ePc += 1

            if eHuman * ePc == 0 and eHuman != ePc:
                for i in range(5):
                    if mark[row + i][col] == 0:
                        if eHuman == 0:
                            if player == 1:
                                board[row + i][col] += DScore[ePc]
                            else:
                                board[row + i][col] += AScore[ePc]
                        if ePc == 0:
                            if player == 2:
                                board[row + i][col] += DScore[eHuman]
                            else:
                                board[row + i][col] += AScore[eHuman]
                        if eHuman == 4 or ePc == 4:
                            board[row + i][col] *= 2
    for row in range(width - 4):
        for col in range(height - 4):
            ePc = 0
            eHuman = 0
            if mark[row + i][col + i] == 1:
                eHuman += 1
            elif mark[row + i][col + i] == 2:
                ePc += 1

            if eHuman * ePc == 0 and eHuman != ePc:
                for i in range(5):
                    if mark[row + i][col + i] == 0:
                        if eHuman == 0:
                            if player == 1:
                                board[row + i][col + i] += DScore[ePc]
                            else:
                                board[row + i][col + i] += AScore[ePc]
                        if ePc == 0:
                            if player == 2:
                                board[row + i][col + i] += DScore[eHuman]
                            else:
                                board[row + i][col + i] += AScore[eHuman]
                        if eHuman == 4 or ePc == 4:
                            board[row + i][col + i] *= 2
    for row in range(4, width):
        for col in range(height - 4):
            ePc = 0
            eHuman = 0
            if mark[row - i][col + i] == 1:
                eHuman += 1
            elif mark[row - i][col + i] == 2:
                ePc += 1

            if eHuman * ePc == 0 and eHuman != ePc:
                for i in range(5):
                    if mark[row - i][col + i] == 0:
                        if eHuman == 0:
                            if player == 1:
                                board[row - i][col + i] += DScore[ePc]
                            else:
                                board[row - i][col + i] += AScore[ePc]
                        if ePc == 0:
                            if player == 2:
                                board[row - i][col + i] += DScore[eHuman]
                            else:
                                board[row - i][col + i] += AScore[eHuman]
                        if eHuman == 4 or ePc == 4:
                            board[row - i][col + i] *= 2


def checkEnd():
    ePc = 0
    eHuman = 0
    for row in range(width):
        for col in range(height - 4):
            ePc = 0
            eHuman = 0
            for i in range(5):
                if mark[row][col + i] == 1:
                    eHuman += 1
                elif mark[row][col + i] == 2:
                    ePc += 1
            if ePc == 5:
                return 2
            elif eHuman == 5:
                return 1
    for row in range(width - 4):
        for col in range(height):
            ePc = 0
            eHuman = 0
            for i in range(5):
                if mark[row + i][col] == 1:
                    eHuman += 1
                elif mark[row + i][col] == 2:
                    ePc += 1
            if ePc == 5:
                return 2
            elif eHuman == 5:
                return 1
    for row in range(width - 4):
        for col in range(height - 4):
            ePc = 0
            eHuman = 0
            for i in range(5):
                if mark[row + i][col + i] == 1:
                    eHuman += 1
                elif mark[row + i][col + i] == 2:
                    ePc += 1
            if ePc == 5:
                return 2
            elif eHuman == 5:
                return 1

    for row in range(4, width):
        for col in range(height - 4):
            ePc = 0
            eHuman = 0
            for i in range(5):
                if mark[row - i][col + i] == 1:
                    eHuman += 1
                elif mark[row - i][col + i] == 2:
                    ePc += 1
            if ePc == 5:
                return 2
            elif eHuman == 5:
                return 1
    return 0


def maxPos(type):
    maxi = 0
    point = (width / 2, height / 2)
    point = list(point)
    for row in range(width):
        for col in range(height):
            if board[row][col] > maxi:
                maxi = board[row][col]
                point[0] = row
                point[1] = col
    if type == 0:
        return maxi
    else:
        return point


lastPoint = (0, 0)
goPoint = (0, 0)
alpha = 0
beta = 0

def minimax(depth, alpha, beta, maximizingPlayer):
    global  lastPoint
    global  goPoint
    #print("%d %d %d" % (depth, alpha, beta))
    if maximizingPlayer:
        boardEval(2)
    else:
        boardEval(1)

    if depth == 0:
        return maxPos(0)
    if checkEnd():
        goPoint = lastPoint
        return maxPos(0)
    goodpoints = []
    for i in range(maxBreadth):
        point = maxPos(1)
        board[int(point[0])][int(point[1])] = 0
        goodpoints.append(point)

    if (maximizingPlayer):
        maxEval = -inf
        for point in goodpoints:
            mark[int(point[0])][int(point[1])] = 2
            lastPoint = point
            eval = minimax(depth - 1, alpha, beta, False)
            mark[int(point[0])][int(point[1])] = 0
            maxEval = max(maxEval, eval)
            alpha = max(alpha, eval)
            if (maxEval >= beta):
                goPoint = point
            if beta <= alpha:
                break
        return maxEval
    else:
        minEval = inf
        for point in goodpoints:
            mark[int(point[0])][int(point[1])] = 1
            eval = minimax(depth - 1, alpha, beta, True)
            mark[int(point[0])][int(point[1])] = 0
            minEval = min(minEval, eval)
            beta = min(beta, eval)
            if beta <= alpha:
                break
        return minEval


# minimax(6, inf, -inf, 1)
turn = 2

while (not checkEnd()):

    print(turn)
    if turn == 1:
        x = int(input())
        y = int(input())
        if (mark[x][y] == 0):
            mark[x][y] = 1
        else:
            print("!\n")
            continue
    else:
        goPoint = (-1, -1)
        minimax(3, -inf, inf, True)
        if goPoint == (-1, -1):
            goPoint = lastPoint
        mark[int(goPoint[0])][int(goPoint[1])] = 2
    if turn == 1:
        turn = 2
    else:
        turn = 1
    print(board)
    print(mark)
