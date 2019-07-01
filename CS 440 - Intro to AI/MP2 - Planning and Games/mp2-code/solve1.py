# -*- coding: utf-8 -*-
import numpy as np
""soln = []

def add(board, pent, coord):
    """
    Adds a pentomino pent to the board. The pentomino will be placed such that
    coord[0] is the lowest row index of the pent and coord[1] is the lowest 
    column index. 
    """
    pent=np.array(pent)
    for row in range(pent.shape[0]): #go through shape to check if it's valid
        for col in range(pent.shape[1]):
            if coord[0]+row >= board.shape[0] or coord[1]+col >=board.shape[1]:
                return False
            if pent[row][col] != 0 and board[coord[0]+row][coord[1]+col] != -1: 
                return False
    
    for row in range(pent.shape[0]): #no go through and place pentomino
        for col in range(pent.shape[1]):
            board[coord[0]+row][coord[1]+col] = pent[row][col]                                
    return True
    
def solve(board_p, pents_p):
    """
    This is the function you will implement. It will take in a numpy array of the board
    as well as a list of n tiles in the form of numpy arrays. The solution returned
    is of the form [(p1, (row1, col1))...(pn,  (rown, coln))]
    where pi is a tile (may be rotated or flipped), and (rowi, coli) is 
    the coordinate of the upper left corner of pi in the board (lowest row and column index 
    that the tile covers).
    
    -Use np.flip and np.rot90 to manipulate pentominos.
    
    -You can assume there will always be a solution.
    """
 #   print("TYPE!: ",type(pents_p))
    board=np.copy(board_p)
    pents=np.copy(pents_p).tolist() 
    # try to place pieces:
    #try sorting pieces first to see if it makes it faster
    #try starting on different spot in board to see if it's faster
    board
    for i in range(board.shape[0]): #do this so we can differntiated piece with index 1 and open spot
        for j in range(board.shape[1]):
            if board[i][j]==1: board[i][j]=-1
    pentlen=len(pents)
    while(len(soln)!=pentlen):
        csp(board,pents)

   # print(soln)
    return soln
   # raise NotImplementedError



def csp(board,pents):
    #find the first open spot going right to left
    #pent = pents[len(pents)-1]
    for r in range(board.shape[0]):
        for c in range(board.shape[1]):#go through entire board
            if board[r][c]==-1: #if a spot is empty
                #try each pent at the spot
                for pent in pents:
                    #make sure to try all rotations of the piece
                    for flipnum in range(3): 
                        p = np.copy(pent)
                        if flipnum > 0:
                            p = np.flip(pent, flipnum-1)
                        for rot_num in range(4):
                         #   print("trying pent: ",pent," on board \n",board)
                            if(add(board,pent,[r,c])):
                          #      print("success!")
                                pents.remove(pent)
                                soln.append((p,(r,c)))
                                csp(board,pents)
                                if len(soln)>=len(pents):
                                    #if u come back from recursive call and soln is full ur done
                                    return
                            p = np.rot90(p)
            ""