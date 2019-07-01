from time import sleep
from math import inf
from random import randint
#import copy 

class ultimateTicTacToe:
    def __init__(self):
        """
        Initialization of the game.
        """
        self.board=[['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_']]
        """        self.board=[['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','X','X','_','_','_','_'],
                    ['_','_','_','O','X','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_'],
                    ['_','_','_','_','_','_','_','_','_']]"""
        """[[x for x in range(9)] for i in range(9)]"""
        self.maxPlayer='X'
        self.minPlayer='O'
        self.maxDepth=3
        #The start indexes of each local board
        self.globalIdx=[(0,0),(0,3),(0,6),(3,0),(3,3),(3,6),(6,0),(6,3),(6,6)]

        #Start local board index for reflex agent playing
        self.startBoardIdx=4
        #self.startBoardIdx=randint(0,8)

        #utility value for reflex offensive and reflex defensive agents
        self.winnerMaxUtility=10000
        self.twoInARowMaxUtility=500
        self.preventThreeInARowMaxUtility=100
        self.cornerMaxUtility=30
        # self.winnerMaxUtility=10
        # self.twoInARowMaxUtility=5
        # self.preventThreeInARowMaxUtility=3
        # self.cornerMaxUtility=10

        self.winnerMinUtility=-10000
        self.twoInARowMinUtility=-100
        self.preventThreeInARowMinUtility=-500
        self.cornerMinUtility=-30

        self.expandedNodes=0
        self.currPlayer=True
        self.designedExpandedNodes=[] #bc apparently YourAgent doesn't return expanded nodes array so ya.
    def boardRestore(self):
        """"
        Restores the board in case you want to run multiple games without having to reinstantiate new classes
        """
        self.board=[['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_'],
            ['_','_','_','_','_','_','_','_','_']]
    def printGameBoard(self):
        """
        This function prints the current game board.
        """
        print('\n'.join([' '.join([str(cell) for cell in row]) for row in self.board[:3]])+'\n')
        print('\n'.join([' '.join([str(cell) for cell in row]) for row in self.board[3:6]])+'\n')
        print('\n'.join([' '.join([str(cell) for cell in row]) for row in self.board[6:9]])+'\n')
    
    def all3combos(self):
        """"
        Just calls small3combos for all small boards on the big one
        input: none
        output: list of all 3 combos to look at in the board
        """
        ret =[]
        for g in self.globalIdx:
            set=self.checkcombos(g)
            ret+=set
        return ret
            
    def checkcombos(self,gi):
        """"
        This is a helper function that outputs all valid 3 combos to check for in a small tic tac toe board as lists
        input: global index of board you're looking at
        output: list of lists, each with 3 elements representing a row col or diagonal
        """
        x,y=gi
        ret =[]
        for i in range(3):
            ret.append(self.board[x+i][y:y+3]) #rows
            ret.append([self.board[x+j][y+i] for j in range(0,3)]) #cols
        ret.append([self.board[x][y], self.board[x+1][y+1],self.board[x+2][y+2]]) #diagonal left right
        ret.append([self.board[x+2][y], self.board[x+1][y+1], self.board[x][y+2]]) # diagonal right left
        return ret 

    def evaluatePredifined(self, isMax):
        """
        This function implements the evaluation function for ultimate tic tac toe for predifined agent.
        input args:
        isMax(bool): boolean variable indicates whether it's maxPlayer or minPlayer.
                     True for maxPlayer, False for minPlayer
        output: 
        score(float): estimated utility score for maxPlayer or minPlayer
        """     

        #print(isMax)
        score=0
        won = self.checkWinner()
        if (won==1 and isMax) or (won==-1 and (not isMax)): #rule 1
            return 10000*won 
        
        #rule 2
        rows= self.all3combos() #all possible connections of 3 on the board
        score=0
        for r in rows:
            if isMax:
                if r.count(self.maxPlayer)==2 and (self.minPlayer not in r): #two in a row unblocked
                    score+=self.twoInARowMaxUtility
                if r.count(self.minPlayer)==2 and r.count(self.maxPlayer)==1: #max blocks min
                    score+=self.preventThreeInARowMaxUtility
            else:
                if r.count(self.minPlayer)==2 and (self.maxPlayer not in r): #two in a row unblocked
                    score+=self.twoInARowMinUtility
                if r.count(self.maxPlayer)==2 and r.count(self.minPlayer)==1:#min blocks max
                    score+=self.preventThreeInARowMinUtility


        #don't do rule 3 if it was evaluated under rule 2
        if score!=0: return score 
        for i in range(9):
            for j in range(9):
                if i%3!=1 and j%3!=1:
                    if isMax and self.board[i][j]==self.maxPlayer:
                        score+=self.cornerMaxUtility
                    elif (not isMax) and self.board[i][j]==self.minPlayer:
                        score+=self.cornerMinUtility

        return score


    def evaluateDesigned(self, isMax):
        """
        This function implements the evaluation function for ultimate tic tac toe for your own agent.
        input args:
        isMax(bool): boolean variable indicates whether it's maxPlayer or minPlayer.
                     True for maxPlayer, False for minPlayer
        output:
        score(float): estimated utility score for maxPlayer or minPlayer
        """
        #NOTE: MY AGENT IS A MIN PLAYER, SO YOU CAN IGNORE ALL THE MAX FUNCTION SIDE
        #       I just wrote it in case I wanted to change it to max but i see no need...
        #
        score=0

        #print("EVALUATE DESIGNED IS CALLED WITH CURRENT PLAYER",self.currPlayer, "AND isMAX",isMax)
        won = self.checkWinner()
        if (won==1 and isMax) or (won==-1 and (not isMax)): #rule 1
            return 10000*won 
        
        #rule 2
        rows= self.all3combos() #all possible connections of 3 on the board
        score=0
        for r in rows:
            if isMax:
        #        print("NEVER TRUEEEE\n\n\n\n\n")
                if r.count(self.maxPlayer)==2 and (self.minPlayer not in r): #two in a row unblocked
                    score+=self.twoInARowMaxUtility
                if r.count(self.minPlayer)==2 and r.count(self.maxPlayer)==1: #max blocks min
                    score+=self.preventThreeInARowMaxUtility
                if r.count(self.maxPlayer)==2:
                    score+=90 #disincentivize letting them get 2 in a row in general
            else:
                if r.count(self.minPlayer)==2 and (self.maxPlayer not in r): #two in a row unblocked
                    score+=self.twoInARowMinUtility
                if r.count(self.maxPlayer)==2 and r.count(self.minPlayer)==1:#min blocks max
                    score+=self.preventThreeInARowMinUtility
                if r.count(self.maxPlayer)==2:
                    score+=-90 #disincentivize letting them get 2 in a row in general

        if score!=0: return score 

        #don't do rule 3 if it was evaluated under rule 2
        for i in range(9):
            for j in range(9):
                if i%3!=1 and j%3!=1:
                    if isMax and self.board[i][j]==self.maxPlayer:
                        score+=self.cornerMaxUtility
                    elif (not isMax) and self.board[i][j]==self.minPlayer:
                        score+=self.cornerMinUtility
                elif i%3==1 and j%3==1: #50 points for the middle!
                    if isMax and self.board[i][j]==self.maxPlayer:
                        score+=50
                    elif (not isMax) and self.board[i][j]==self.minPlayer:
                        score+=-50
                
        return score

    def checkMovesLeft(self):
        """
        This function checks whether any legal move remains on the board.
        output:
        movesLeft(bool): boolean variable indicates whether any legal move remains
                        on the board.
        """
        #YOUR CODE HERE
        for i in self.board: #go through board and check for empty spots
            if i.count('_')!=0: return True
        #if you went through whole board then, u right no valid moves left
        return False

    def checkWinner(self):
        #Return termimnal node status for maximizer player 1-win,0-tie,-1-lose
        """
        This function checks whether there is a winner on the board.
        output:
        winner(int): Return 0 if there is no winner.
                     Return 1 if maxPlayer is the winner.
                     Return -1 if miniPlayer is the winner.
        """
        #YOUR CODE HERE
        combos=self.all3combos()
        for i in combos: # go through all row combinations... if you have 3 of same kind in a row. that person wins!
            if i.count(self.maxPlayer)==3: return 1
            if i.count(self.minPlayer)==3: return -1
        return 0

    def alphabeta(self,depth,currBoardIdx,alpha,beta,isMax):
        """
        This function implements alpha-beta algorithm for ultimate tic-tac-toe game.
        input args:
        depth(int): current depth level
        currBoardIdx(int): current local board index
        alpha(float): alpha value
        beta(float): beta value
        isMax(bool):boolean variable indicates whether it's maxPlayer or minPlayer.
                     True for maxPlayer, False for minPlayer
        output:
        bestValue(float):the bestValue that current player may have
        """
        #YOUR CODE HERE

        if depth==self.maxDepth or self.checkWinner()!=0 or not self.checkMovesLeft(): #if you are at maxdepth, evaluate predefined
            self.expandedNodes+=1
            score= self.evaluatePredifined(self.currPlayer)
            return score
        
        values = []
        for i in range(9):
            gi= self.globalIdx[currBoardIdx]
            #print("At board: ",gi,"Max is moving" if isMax else "Min is moving","depth is ",depth )
            row=gi[0]+i//3 #// is integer division
            col=gi[1]+i%3

            #check if there is an available spot there
            if self.board[row][col]!='_': continue
            
            #if so add it to the board and evaluate
            self.board[row][col]=self.maxPlayer if isMax else self.minPlayer
            #self.expandedNodes+=1
            val = self.alphabeta(depth+1,i,alpha,beta,(not isMax))
            self.board[row][col]='_' #remove it for other tests
            values.append(val)
            if isMax:
                if val>=beta: 
                    #print("YES PRUNING BETA THROUHGH MAX!")
                    return val
                alpha=max(alpha,max(values))
            else:
                if val<=alpha: 
                    #print("YES PRUNING ALPHA THROUHGH MIN!")
                    return val
                beta = min(beta,min(values))
            
        #now find min or max
        if isMax: return max(values)
        else: return min(values)

    def Your_alphabeta_Agent(self,depth,currBoardIdx,alpha,beta,isMax):
        """
        This function implements alpha-beta algorithm for ultimate tic-tac-toe game. like the one above, except it calls evaluateDesigned for score
        input args:
        depth(int): current depth level
        currBoardIdx(int): current local board index
        alpha(float): alpha value
        beta(float): beta value
        isMax(bool):boolean variable indicates whether it's maxPlayer or minPlayer.
                     True for maxPlayer, False for minPlayer
        output:
        bestValue(float):the bestValue that current player may have
        """
        #YOUR CODE HERE

        if depth==self.maxDepth or self.checkWinner()!=0 or not self.checkMovesLeft(): #if you are at maxdepth, evaluate predefined
            self.expandedNodes+=1
            score= self.evaluateDesigned(self.currPlayer)
            return score
        
        values = []
        for i in range(9):
            gi= self.globalIdx[currBoardIdx]
            #print("At board: ",gi,"Max is moving" if isMax else "Min is moving","depth is ",depth )
            row=gi[0]+i//3 #// is integer division
            col=gi[1]+i%3

            #check if there is an available spot there
            if self.board[row][col]!='_': continue
            
            #if so add it to the board and evaluate
            self.board[row][col]=self.maxPlayer if isMax else self.minPlayer
            #self.expandedNodes+=1
            val = self.Your_alphabeta_Agent(depth+1,i,alpha,beta,(not isMax))
            self.board[row][col]='_' #remove it for other tests
            values.append(val)
            if isMax:
                if val>=beta: 
                    #print("YES PRUNING BETA THROUHGH MAX!")
                    return val
                alpha=max(alpha,max(values))
            else:
                if val<=alpha: 
                    #print("YES PRUNING ALPHA THROUHGH MIN!")
                    return val
                beta = min(beta,min(values))
            
        #now find min or max
        if isMax: return max(values)
        else: return min(values)

    def minimax(self, depth, currBoardIdx, isMax):
        """
        This function implements minimax algorithm for ultimate tic-tac-toe game.
        input args:
        depth(int): current depth level
        currBoardIdx(int): current local board index
        isMax(bool):boolean variable indicates whether it's maxPlayer or minPlayer.
                     True for maxPlayer, False for minPlayer
        output:
        bestValue(float):the bestValue that current player may have
        """
        if depth==self.maxDepth or self.checkWinner()!=0 or not self.checkMovesLeft(): #if you are at maxdepth, evaluate predefined
            self.expandedNodes+=1
            score= self.evaluatePredifined(self.currPlayer)
        #     print("score: ",score)
            return score
        
        values = []
        for i in range(9):
            gi= self.globalIdx[currBoardIdx]
            #print("At board: ",gi,"Max is moving" if isMax else "Min is moving","depth is ",depth )
            row=gi[0]+i//3 #// is integer division
            col=gi[1]+i%3

            #check if there is an available spot there
            if self.board[row][col]!='_': continue
            #if so add it to the board and evaluate
            self.board[row][col]=self.maxPlayer if isMax else self.minPlayer
            #self.expandedNodes+=1
            val = self.minimax(depth+1,i,(not isMax))
            #print("depth: ",depth)
            #self.printGameBoard()
            self.board[row][col]='_' #remove it for other tests
            values.append(val)

        #now find min or max
        if isMax: return max(values)
        else: return min(values)

    def copybrd(self):
        """
        Returns copy of current game board
        Ugggh Python is so annoying. self.board.copy(),list(self.board), and self.board[:] 
        were't working and we can't use np.copy() so i had to write this
        """
        d = []
        for i in range(len(self.board)):
            d.append([])
            for j in range(len(self.board[0])):
                d[i].append(self.board[i][j])
        return d
            
    def playGamePredifinedAgent(self,maxFirst,isMinimaxOffensive,isMinimaxDefensive):
        """
        This function implements the processes of the game of predifined offensive agent vs defensive agent.
        input args:
        maxFirst(bool): boolean variable indicates whether maxPlayer or minPlayer plays first.
                        True for maxPlayer plays first, and False for minPlayer plays first.
        isMinimaxOffensive(bool):boolean variable indicates whether it's using minimax or alpha-beta pruning algorithm for offensive agent.
                        True is minimax and False is alpha-beta.
        isMinimaxOffensive(bool):boolean variable indicates whether it's using minimax or alpha-beta pruning algorithm for defensive agent.
                        True is minimax and False is alpha-beta.
        output:
        bestMove(list of tuple): list of bestMove coordinates at each step
        bestValue(list of float): list of bestValue at each move
        expandedNodes(list of int): list of expanded nodes at each move
        gameBoards(list of 2d lists): list of game board positions at each move
        winner(int): 1 for maxPlayer is the winner, -1 for minPlayer is the winner, and 0 for tie.
        """
        #max is offensive, min is defensive
        #YOUR CODE HERE
        bestMove=[]
        bestValue=[]
        gameBoards=[]
        expandedNodes=[]
        atglobal=self.startBoardIdx #stores which global index we are at
        self.currPlayer= True if maxFirst else False
        
        while (self.checkMovesLeft() and not self.checkWinner()):
            #if isMinimaxOffensive and isMinimaxDefensive: #case of minimax vs minimax offensive (max) first
            values = []
            options = {} #holds potential moves so we know which one returns the max
            for i in range(9):
                gi= self.globalIdx[atglobal]
        #       print("At board: ",gi,"Max is moving" if self.currPlayer else "Min is moving","depth is ",0 )
                row=gi[0]+i//3
                col=gi[1]+i%3

                #check if there is an available spot there
                if self.board[row][col]!='_': continue
                
                #if there is an empty spot add it to the board and evaluate
                self.board[row][col]=self.maxPlayer if self.currPlayer else self.minPlayer
                #self.expandedNodes+=1
                val = self.minimax(1 ,i,not self.currPlayer) if (isMinimaxOffensive and self.currPlayer) or (isMinimaxDefensive and not self.currPlayer) else self.alphabeta(1,i,-inf,inf,not self.currPlayer)
                self.board[row][col]='_' #remove it for other tests
                values.append(val) #add the value for that move to the other possibilites
                if val not in options.keys(): #add store that value and the corresponding move that caused it
                    options[val]=[i]
                else: 
                    options[val].append(i)    


            #now find max and do the move, and store the values...
            chosenval=max(values) if self.currPlayer else min(values)
            bestValue.append(chosenval)
            bestidx=min(options[chosenval]) #this is just if multiple indices had same value, chose smallest index
            best=(self.globalIdx[atglobal][0]+bestidx//3,self.globalIdx[atglobal][1]+bestidx%3)
            bestMove.append(best)
            self.board[best[0]][best[1]]=self.maxPlayer if self.currPlayer else self.minPlayer
            atglobal=bestidx
            gameBoards.append(self.copybrd()) #make a new copy of board and append it
            expandedNodes.append(self.expandedNodes)
            self.expandedNodes=0
            #       print("max just played from values of: " if self.currPlayer else "min just played from values of: ",options)
            #       self.printGameBoard()
            self.currPlayer = not self.currPlayer#switches who's turn it is

        return gameBoards, bestMove, expandedNodes, bestValue, self.checkWinner()

    def playGameYourAgent(self):
        """processes of the game of your own agent vs predifined offensive agent.
        input args:
        output:
        bestMove(list of tuple): list of bestMove coordinates at each step
        gameBoards(list of 2d lists): list of game board positions at each move
        winner(int): 1 for maxPlayer is the winner, -1 for minPlayer is the winner, and 0 for tie.
        """
        #YOUR CODE HERE
        bestMove=[]
        gameBoards=[]
        self.designedExpandedNodes=[]
        atglobal=randint(0,8) #stores which global index we are at
        self.currPlayer= True if randint(0,1) else False #This will imply that the predefined agent is going first (offensive)
    #    print("Randomness: ",atglobal,self.currPlayer)

        while (self.checkMovesLeft() and not self.checkWinner()):
            values = []
            options = {} #holds potential moves so we know which one returns the max
            for i in range(9):
                gi= self.globalIdx[atglobal]
                row=gi[0]+i//3
                col=gi[1]+i%3

                #check if there is an available spot there
                if self.board[row][col]!='_': continue
                
                #if there is an empty spot add it to the board and evaluate
                if self.currPlayer:
                    self.board[row][col]=self.maxPlayer
                    val = self.alphabeta(1,i,-inf,inf,not self.currPlayer)
                else:
                    self.board[row][col]=self.minPlayer
                    val = self.Your_alphabeta_Agent(1,i,-inf,inf,not self.currPlayer)
                self.board[row][col]='_' #remove it for other tests
                values.append(val) #add the value for that move to the other possibilites
                if val not in options.keys(): #add store that value and the corresponding move that caused it
                    options[val]=[i]
                else: 
                    options[val].append(i)    

            #now find max and do the move, and store the values...
            chosenval=max(values) if self.currPlayer else min(values)
            bestidx=min(options[chosenval]) #this is just if multiple indices had same value, chose smallest index
            best=(self.globalIdx[atglobal][0]+bestidx//3,self.globalIdx[atglobal][1]+bestidx%3)
            bestMove.append(best)
            self.board[best[0]][best[1]]=self.maxPlayer if self.currPlayer else self.minPlayer
            atglobal=bestidx
            gameBoards.append(self.copybrd())
            self.designedExpandedNodes.append(self.expandedNodes)
            self.expandedNodes=0
        #   print("max just played from values of: " if self.currPlayer else "min just played from values of: ",options)
        #   self.printGameBoard()
            self.currPlayer = not self.currPlayer#switches who's turn it is

        return gameBoards, bestMove, self.checkWinner()
        
    def playGameHuman(self):
        """
        This function implements the processes of the game of your own agent vs a human.
        output:
        bestMove(list of tuple): list of bestMove coordinates at each step
        gameBoards(list of 2d lists): list of game board positions at each move
        winner(int): 1 for maxPlayer is the winner, -1 for minPlayer is the winner, and 0 for tie.
        """
        #YOUR CODE HERE
        bestMove=[]
        gameBoards=[]
        self.designedExpandedNodes=[] #this is bc we don't return expanded nodes so if we want to count them, we need something here
        atglobal=randint(0,8) #stores which global index we are at
        self.currPlayer= True if randint(0,1) else False #This will imply that the predefined agent is going first (offensive)
        print("Start:",atglobal,"You go ","first" if self.currPlayer else "second")

        while (self.checkMovesLeft() and not self.checkWinner()):
            values = []
            options = {} #holds potential moves so we know which one returns the max
            best=None #just place holder for human move
            bestidx=0
            if self.currPlayer: #human player, read input
                while(True):
                    print("At Board Index: ",atglobal)
                    self.printGameBoard()
                    try:
                        bestidx=int(input("What would you like to move (0-9 on small board)?"))
                    except:
                        print("Error: Try again")
                        continue
                    gi= self.globalIdx[atglobal]
                    row=gi[0]+bestidx//3
                    col=gi[1]+bestidx%3
                    if bestidx<9 and bestidx>=0 and self.board[row][col]=='_': break 
                    print("invalid move, try again!")
                best=(self.globalIdx[atglobal][0]+bestidx//3,self.globalIdx[atglobal][1]+bestidx%3)
            else: #Ai my agent player
                for i in range(9):
                    gi= self.globalIdx[atglobal]
                    row=gi[0]+i//3
                    col=gi[1]+i%3

                    #check if there is an available spot there
                    if self.board[row][col]!='_': continue
                    
                    #if there is an empty spot add it to the board and evaluate
                    self.board[row][col]=self.minPlayer
                    val = self.Your_alphabeta_Agent(1,i,-inf,inf,not self.currPlayer)

                    self.board[row][col]='_' #remove it for other tests
                    values.append(val) #add the value for that move to the other possibilites
                    if val not in options.keys(): #add store that value and the corresponding move that caused it
                        options[val]=[i]
                    else: 
                        options[val].append(i)
                
            if not self.currPlayer:
                chosenval=min(values)   #always do min 
                bestidx=min(options[chosenval]) #this is just if multiple indices had same value, chose smallest index
                best=(self.globalIdx[atglobal][0]+bestidx//3,self.globalIdx[atglobal][1]+bestidx%3)
                bestMove.append(best)
            
            self.board[best[0]][best[1]]=self.maxPlayer if self.currPlayer else self.minPlayer
            atglobal=bestidx
            gameBoards.append(self.copybrd())
            self.designedExpandedNodes.append(self.expandedNodes)
            self.expandedNodes=0
        #   print("max just played from values of: " if self.currPlayer else "min just played from values of: ",options)
        #   self.printGameBoard()
            self.currPlayer = not self.currPlayer#switches who's turn it is

        return gameBoards, bestMove, self.checkWinner()

    def testMyAgent(self):
        wins=0
        gamesNodes=[]
        for x in range(20):
            print("Round ",x)
            gameBoards, bestMove, winner=uttt.playGameYourAgent()
            if winner==-1:
                wins+=1
            gamesNodes.append(sum(self.designedExpandedNodes))
            if winner == 1:
                print("The winner is maxPlayer!!!")
            elif winner == -1:
                print("The winner is MyAgent!!!")
            else:
                print("Tie. No winner:(")
            self.printGameBoard()
            self.boardRestore()
            print()

        print("My agent won ",wins/20*100,"% of the time.")
        print(gamesNodes)
            

if __name__=="__main__":
    uttt=ultimateTicTacToe()
    gameBoards, bestMove, expandedNodes, bestValue, winner=uttt.playGamePredifinedAgent(False,True,False)#(True,False,False)
    print(bestMove,bestValue,winner)
    #gameBoards, bestMove, winner=uttt.playGameYourAgent()
    #gameBoards, bestMove, winner=uttt.playGameHuman()

    # for gb in gameBoards:
    #     for line in gb:
    #         print(line)
    #     print()
    #print("BestMove: ",bestMove, "BestValue: ",bestValue)
    uttt.printGameBoard()
    #print("number of nodes expanded in last turn: ", expandedNodes[len(expandedNodes)-1])
    #print("Total number of expanded nodes: ",sum(expandedNodes))

    # if winner == 1:
    #     print("The winner is HumanPlayer!!!")
    # elif winner == -1:
    #     print("The winner is AIAgent!!!")
    # else:
    #     print("Tie. No winner:(")

    if winner == 1:
        print("The winner is maxPlayer!!!")
    elif winner == -1:
        print("The winner is minPlayer!!!")
    else:
        print("Tie. No winner:(")

    #uttt.testMyAgent()



"""
Questions:
how do i copy the board?
how do i come up with my own evaluation function?
"""