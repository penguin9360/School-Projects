# -*- coding: utf-8 -*-
import numpy as np
soln = {}

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
"""

"""
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
            """

class PentStruct:
    #these two variable declared outside init without self are static/class variables (consistent throughout all instances of class)
    count_spots=0
    available=True 
    def __init__(self,givenID,numpents):
        self.id=givenID

class Pent(PentStruct):
    def __init__(self,idgiven,array,givenr=0,givenc=0,fn=0,rn=0):
        super().__init__(idgiven)
        self.r=givenr #note this r,c is the topleft of the corner array.
        self.c=givenc
        self.flipnum=fn
        self.rotnum=rn
        self.arr=array
        

def pent_init(pents,board):
    """
    Initializes needed dictionary/data structures for algorithm
    input: pent arrays, and board
    Returns a dicitonary of Pent objects covering every space of the board
    """
    b={}
    for p in pents:
        id=get_pent_idx(p)
        for row in range(board.shape[0]):
            for col in range(board.shape[1]):
                if board[row][col]==0: continue #we're only tiling the 1s on the board
              #  print("at",row,",",col)
                for flipnum in range(3): #for rotations (just using is_pentomino's method)
                    if flipnum > 0:
                        p = np.flip(p, flipnum-1)
                    for rot_num in range(4):
                #        print(p)
                #        print('\t',row,p.shape[0],col,p.shape[1])
                        if row+p.shape[0] <= board.shape[0] and col+p.shape[1] <= board.shape[1]: #this means it fits on board at this position
                #            print("fits board dimensions")
                            p_obj=Pent(id,p,row,col,flipnum,rot_num)
                            p_obj.count_spots+=1 #because we are adding a potential spot of where it can be
                            #now loop through pent array and add r,c locations to dictionary if needed
                            for r in range(p.shape[0]): 
                                for c in range(p.shape[1]):
                 #                   print('\t',r,p.shape[0],c,p.shape[1])
                                    if p[r][c] != 0:
                                        if (row+r,col+c) in b.keys():
                                            pent_in=False   #check to make sure you're not reusing the rotations that are equivalent (rotating squre vs. L shape)
                                            for checkp in b[(row+r,col+c)]:
                                                if np.array_equal(p,checkp.arr):
                                                    pent_in=True
                                            if not pent_in:
                                                b[(row+r,col+c)].append(p_obj)
                                        else:
                                            b[(row+r,col+c)]=[p_obj]
                        p=np.rot90(p)

    return b


def bt(b,available_locs):#available locations will have to hold the values of locations with 1 in them
    #first chose row,col location with fewest possible pents. need list of row,col locations
    small_loc=available_locs[0] 
    for l in available_locs:
        if len(b[l])<len(b[small_loc]): #len runs in O(1) so don't be afraid to use it!
            small_loc=l

    #now go through each pent's count_spots in that location and chose the one that has les





def get_pent_idx(pent):
    """
    Returns the index of a pentomino.
    """
    pidx = 0
    for i in range(pent.shape[0]):
        for j in range(pent.shape[1]):
            if pent[i][j] != 0:
                pidx = pent[i][j]
                break
        if pidx != 0:
            break
    if pidx == 0:
        return -1
    return pidx - 1


if __name__=="__main__":
    #THIS PART TESTS PENT INIT!
    #pents= [np.array([[1],[1]]),np.array([[2],[2]])]
    pents=[np.array([[1,0],[1,1]]),np.array([[2,0],[2,2]])]
    board = np.ones((2,2))
    print(pents,'\n',board)
    b=pent_init(pents,board)
    for ind in b:
        print(ind,":")
        for i in b[ind]:
            print("\t",i.id,i.arr)