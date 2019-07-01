# -*- coding: utf-8 -*-
import numpy as np
soln = {}
DEBUG_BT = False #for debugging backtrack function

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

    board=np.copy(board_p)
    pents=np.copy(pents_p).tolist() 
    b=pent_init(pents,board)
    locations=list(b.keys())

    while(len(soln)!=len(pents_p)):
        backtrack(b,locations)
        
    #conver solution to the format they want:
    solution=[]
    for ind in soln:
        solution.append((np.array(soln[ind].arr), (soln[ind].r,soln[ind].c)))
    
    return solution

class Pent():
    #these two variable declared outside init without self are static/class variables (consistent throughout all instances of class)
    #they're essentially so all objects can see
    count_spots=[] #number of spots on board a pent at that index is available
    available=[] #pentidex of pents that are available
    groups=[]  #idea for creating better heuristic of counting available pents or locations or somehthing 
    win=0
    def __init__(self,givenID,array,givenr=0,givenc=0,fn=0,rn=0):
        self.id=givenID
        self.r=givenr #note this r,c is the topleft of the corner array.
        self.c=givenc
        self.flipnum=fn
        self.rotnum=rn
        self.arr=array
        self.orientation_used=False     

def pent_init(pents,board):
    """
    Initializes needed dictionary/data structures for algorithm
    input: pent arrays, and board
    Returns a dicitonary of Pent objects covering every space of the board
    """
    b={}
    for p in pents:
        Pent.count_spots.append(0) #for every pent add a corresponding count and available bool
        Pent.available.append(True)
        Pent.groups.append(set())
        id=get_pent_idx(p)+1
    #    print("Got pentidx for ",p," equals ",id)
        for row in range(board.shape[0]):
            for col in range(board.shape[1]):
              #  print("at",row,",",col)
                for flipnum in range(3): #for rotations (just using is_pentomino's method)
                    if flipnum > 0:
                        p = np.flip(p, flipnum-1)
                    for rot_num in range(4):
                #        print(p)
                #        print('\t',row,p.shape[0],col,p.shape[1])
                        if row+len(p) <= board.shape[0] and col+len(p[0]) <= board.shape[1]: #this means it fits on board at this position
                #            print("fits board dimensions")
                            p_obj=Pent(id,p,row,col,flipnum,rot_num)
                            p_obj.count_spots[p_obj.id-1]+=1 #because we are adding a potential spot of where it can be
                            Pent.groups[p_obj.id-1].add(p_obj)

                            pent_fits=True #need to check to make sure pent fits in the ones
                            for r in range(len(p)): 
                                for c in range(len(p[0])):
                                    if p[r][c]!=0 and board[row+r][col+c]==0:
                                        pent_fits=False
                                        break
                                if not pent_fits: break

                            if pent_fits:
                                #now loop through pent array and add r,c locations to dictionary if needed
                                for r in range(len(p)): 
                                    for c in range(len(p[0])):
                    #                   print('\t',r,len(p),c,len(p[0]))
                                        if  p[r][c] != 0: #don't add if pent is 0 (board shouldn't be zero over there^^we just checke that with pent_fits bool)
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

def count_pents(b,loc):
    """Return's the number of available pents at that location:
    --can't do this by storing values in array bc then when you remove a pent from options you would have to go through each of it's spots 
    to decrement the amount of options"""
    ct=0
    for p in b[loc]:
        if not p.orientation_used: ct+=1
    return ct

def backtrack(b,available_locs):#available locations will have to hold the values of locations with 1 in them ( available_locs should be list of tuples)
    #first chose row,col location with fewest possible pents. need list of row,col locations
    if len(available_locs)==0: #base case if you win!
        Pent.win=1
        return
    small_loc=available_locs[0] 
    #NOTE: THE BLOCK COMMENTED OUT BELOW WAS SUPPOSED TO MAKE IT FASTER BUT EITHER MAKES AN INFINTE LOOP OR TAKES A REALLY REALLY LONG TIME
    # small_ps=count_pents(b,small_loc)
    # for l in available_locs:
    #     #wait this won't be accurate bc u need to check each pent's .orient_used!--create function to do such and maybe add field to b dict counting such 
    #     check=count_pents(b,l)
    #     if check<small_ps: #len runs in O(1) so don't be afraid to use it!
    #         small_loc=l
    #         small_ps=check
    if DEBUG_BT: print("Checking location: ",small_loc)
    #now go through each pents and make a list with first being least possible locations
    p_avail=False
    possible_pents=[]
    for p in b[small_loc]:
        #make sure not to count the onees that aren't available or that aren't the top left corner of that spot
        #if not Pent.available[p.id-1] or not (p.r==small_loc[0] and p.c==small_loc[1]): continue 
        #jk the line above gives a bug if you are looking at bottom or right edge bc then nothing will ever start in that corner!
        if not Pent.available[p.id-1] : continue 
        if not p.orientation_used: 
            if p_avail==False: #first available pent
                p_avail=True
            possible_pents.append(p) #save as a pent option to check
            p.orientation_used=True #make sure to change all pent/orientations on this r,c location to used! (bc nothing can occupy this spot!)
            Pent.count_spots[p.id-1]-=1 #NOTE: it's p.id-1 bc my implementation of id is from index 1 one not zero
        #if by the end of this loop p-avail is false... then there's no available pent orientations for this spot! GG! Backtrack!
    if p_avail==False: return
    #this should sort the possible pents in order from least spots it can go on to most!
    possible_pents.sort(key=lambda x: Pent.count_spots[x.id-1]) #index minus one bc pents indices start from 1 unlike lists

    for p in possible_pents: #now we have pent and location! Sooo. try placing pent!
        if DEBUG_BT: print("\nWith pent: ",p.arr)
        soln[small_loc]=p #add it to solution dict (or change it from previous p if not in first round of this loop)
        #go through locations in pent and remove them from available locations
        removed=[] # locations removed from availble
        changed=[] #pents changed to used=true
        Pent.available[p.id-1] = False
        
        for r in range(len(p.arr)):
            for c in range(len(p.arr[0])):
                if p.arr[r][c]==0: continue
                if (r+p.r,c+p.c) in available_locs:
                    if DEBUG_BT: print("removing: ",(r+p.r, c+p.c),"from available_locs")
                    available_locs.remove((r+p.r, c+p.c)) 
                    #this should remove small_loc too (small loc is whatever location of the pent we are on ie in the domino, could be second could be first
                    #whereas p.r,p.c is always top left location of domino or pentomino
                    removed.append((r+p.r, c+p.c))
                for overlap_pent in b[(r+p.r,c+p.c)]: #NOTE: These are pents at this location... this is changing possible pents at other locations that the pent you are place at
                    if overlap_pent.orientation_used==False: #need to do this to make sure u don't change pents that were already used by other locations back to true when backtracking
                        overlap_pent.orientation_used=True 
                        Pent.count_spots[overlap_pent.id-1]-=1 #now that u have placed p in this location, there is one less spot overlap_pent can be...
                        changed.append(overlap_pent)
        if DEBUG_BT: print("\trecursing!")
       # if DEBUG_BT:put_on_board(np.copy(board))
        backtrack(b,available_locs)
        if DEBUG_BT: print("\tback from recursing on ",p.id)
        if Pent.win==1: return #if you win yay ur done!
        #if u didn't win something didn't go right along this path so we got to back track and try next pent
        available_locs.extend(removed)
        for op in changed: #fix all the overlapping pents that u switched   |
            op.orientation_used=False #think about case like _._._.| with  .|. .  the second one starts outside of the first but should still be marked as unavialbe
                                        #which doesn't happen if you don't change all pieces in block to used=True
            Pent.count_spots[p.id-1]+=1
        del soln[small_loc]
        Pent.available[p.id-1] = True
    #if u finished trying all the pents then something must have gone wrong before your move, so back track even more
    #del soln[p] #this p should be at the end of possible_pents so should be same as possible_pents.pop()
    for p in possible_pents:
        p.orientation_used=False 

     


    #probs need looping through ALL locations and ALL pents changing orientation used to false, bc if u use a pent 
    #on some unrelated pent possible orientation across the board it would change how many pents are available !
    #don't we need a big for loop outside of small loc, constantly chosing small locations? --maybe not let's see
    #also where is the win condition? where do i set win=1

def get_pent_idx(pent):
    """
    Returns the index of a pentomino.
    """
    pidx = 0
    
    for i in range(len(pent)):
        for j in range(len(pent[0])):
            if pent[i][j] != 0:
                pidx = pent[i][j]
                break
        if pidx != 0:
            break
    if pidx == 0:
        return -1
    return pidx - 1

def put_on_board(board): #NOT WORKING RN DO NOT USE 
    for i in range(board.shape[0]):
        for j in range(board.shape[1]):
            board[i][j]*=-1 #lets zeroes be and makes 1's negative 1s
    for loc in soln:
        p=soln[loc]
        for r in range(len(p.arr)):
            for c in range(len(p.arr[0])):
                board[p.r+r][p.c+c]=p.arr[r][c]
    for l in board:
        print(l)

"""if __name__=="__main__":
    #THIS PART TESTS PENT INIT!
    #Super basic test case dominoes:
    #pents= [np.array([[1],[1]]),np.array([[2],[2]])]
    #board = np.ones((2,2))

    #Super basic Triominoes test cases
    #pents=[np.array([[1,0],[1,1]]),np.array([[2,0],[2,2]])]
    #board=np.ones((2,3))

    #slightly bigger board test:
    #pents = [np.array([[i,0],[i,i]]) for i in range(1,5)]
    pents = [np.array([[i],[i]]) for i in range(1,7)]
    board=np.ones((4,3))

    #basic holes test case:
    #pents=[np.array([[1,0],[1,1]])]
    # board=np.array([[1,0,0],
    #                [1,1,0]])

    #test case with holes and pentominoes:
    # pents=[np.array([[0,1,1],
    #                [1,1,0],
    #                [0,1,0]]),
    #     np.array([[2],
    #               [2],
    #               [2],
    #               [2],
    #               [2]])]
    # board=np.array([[0,1,1],
    #                [1,1,1],
    #                [0,1,1],
    #                [0,0,1],
    #                [0,0,1],
    #                [0,0,1]])



    print(pents,'\n',board)
    # print("TESTING pent_init:")
    # b=pent_init(pents,board)
    # for ind in b:
    #     print(ind,":")
    #     for i in b[ind]:
    #         print("\t",i.id,i.arr,"Placed at ",i.r,',',i.c)

    solve(board,pents)
    for ind in soln:
        print(soln[ind].r,soln[ind].c,":")
        for i in soln[ind].arr:
            print(i)
            #print("\t",i.id,i.arr)

    
"""