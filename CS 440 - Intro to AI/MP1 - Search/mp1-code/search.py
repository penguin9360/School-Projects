# search.py
# ---------------
# Licensing Information:  You are free to use or extend this projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to the University of Illinois at Urbana-Champaign
#
# Created by Michael Abir (abir2@illinois.edu) on 08/28/2018
# Modified by Rahul Kunji (rahulsk2@illinois.edu) on 01/16/2019

"""
This is the main entry point for MP1. You should only modify code
within this file -- the unrevised staff files will be used for all other
files and classes when code is run, so be careful to not modify anything else.
"""


# Search should return the path and the number of states explored.
# The path should be a list of tuples in the form (row, col) that correspond
# to the positions of the path taken by your search algorithm.
# Number of states explored should be a number.
# maze is a Maze object based on the maze from the file specified by input filename
# searchMethod is the search method specified by --method flag (bfs,dfs,greedy,astar)

def search(maze, searchMethod):
    return {
        "bfs": bfs,
        "dfs": dfs,
        "greedy": greedy,
        "astar": astar,
    }.get(searchMethod)(maze)


def bfs(maze): #bfs search with queue
    start = maze.getStart() #tuple of position
    at = start
    objs = maze.getObjectives() # list of objectives
    goal=objs[0]
    explored = {at:at} #holds nodes that we have explored and their predecessor
    frontier = [[at,at]] #list of nodes yet to explore and their predecessor
                         #we can mark start's predecessor as itself
    cnt=0
 #   print("Goal is: ",goal)
    while(at!=goal):
        cnt+=1
    #if at in objs: objs.pop(objs.index(at)) #remove objective if your at it
        neighbors = maze.getNeighbors(at[0],at[1])
        for i in neighbors: 
            if ((i not in explored) and (i not in [n[0] for n in frontier])): 
                frontier.insert(0,[i,at]) #insert all neighbors in queue
        at,prev=frontier.pop()
        explored[at]=prev
 #       print("at is: ",at)


    #now at should be at goal so find path by going backwards through dictionary
    path=[]
    while(True):
        path.append(at)
        next=explored[at]
        if next==at: break
        at= next
    path.reverse()
    # return path, num_states_explored
    #return list(explored.keys()),cnt #if you want to see how much it explored
    return path,cnt


def dfs(maze): #should be same as BFS except with stack instead of stack
    # TODO: Write your code here
    start = maze.getStart() #tuple of position
    at = start
    objs = maze.getObjectives() # list of objectives
    goal=objs[0]
    explored = {at:at} #holds nodes that we have explored and their predecessor
    frontier = [[at,at]] #list of nodes yet to explore and their predecessor
                         #we can mark start's predecessor as itself
    cnt=0
 #   print("Goal is: ",goal)
    while(at!=goal):
        cnt+=1
    #if at in objs: objs.pop(objs.index(at)) #remove objective if your at it
        neighbors = maze.getNeighbors(at[0],at[1])
        for i in neighbors:     
            if ((i not in explored) and (i not in [n[0] for n in frontier])): 
                frontier.append([i,at]) #insert all neighbors in queue
        at,prev=frontier.pop()
        explored[at]=prev
 #       print("at is: ",at)


    #now at should be at goal so find path by going backwards through dictionary
    path=[]
    while(True):
        path.append(at)
        next=explored[at]
        if next==at: break
        at= next
    path.reverse()
    #return path, num_states_explored
    #return list(explored.keys()),cnt #if you want to see how much it explored
    return path,cnt

def h_man(p1,p2): #calculates manhattan distance heuristic 
    return abs(p1[0]-p2[0])+abs(p1[1]-p2[1])

def greedy(maze):
    # TODO: Write your code here
    start = maze.getStart() #tuple of position
    at = start
    objs = maze.getObjectives() # list of objectives
    goal=objs[0]
    explored = {at:at} #holds nodes that we have explored and their predecessor
    frontier = [[at,at]] #list of nodes yet to explore and their predecessor
                         #we can mark start's predecessor as itself
    cnt=0
 #   print("Goal is: ",goal)
    while(at!=goal):
        cnt+=1
    #if at in objs: objs.pop(objs.index(at)) #remove objective if your at it
        neighbors = maze.getNeighbors(at[0],at[1])
        for i in neighbors:     
            if ((i not in explored) and (i not in [n[0] for n in frontier])): 
                frontier.append([i,at]) #insert all neighbors in queue
        small_h=frontier[0] #find the node in frontier with smallest heuristic
        for f in frontier:
            if h_man(f[0],goal)<h_man(small_h[0],goal):
                small_h=f
        at,prev=small_h
        frontier.remove(small_h)
        explored[at]=prev
 #       print("at is: ",at)

    #now at should be at goal so find path by going backwards through dictionary
    path=[]
    while(True):
        path.append(at)
        next=explored[at]
        if next==at: break
        at= next
    path.reverse()
    #return path, num_states_explored
    #return list(explored.keys()),cnt #if you want to see how much it explored
    #print(path)
    return path,cnt

def h_astar(p,objs):
#    print("checking heuristic for ",p)
#    print("objectives",objs)
    small = ((p[0]-objs[0][0])**2.+(p[1]-objs[0][1])**2.)**.5
#    print("small is ",small)
    closest=objs[0]
    for o in objs: 
        if ((p[0]-o[0])**2.+(p[1]-o[1])**2.)**.5<small:
            small=((p[0]-o[0])**2.+(p[1]-o[1])**2.)**.5
#            print("now small is ",small," from ",o)
            closest=o
#    print("closest o is ",closest)
    return small

def astar(maze):
    start = maze.getStart() #tuple of position
 #   print("start",start)
 #   print("Dims",maze.getDimensions())
    at = start
    objs = maze.getObjectives() # list of objectives
    goal=objs[0]
    explored = {at:at} #holds nodes that we have explored and their predecessor
    frontier = [] #list of nodes yet to explore and their predecessor
                         #we can mark start's predecessor as itself
                         #now we also need to keep track of how many steps from start
    cnt=0
    path=[]
    dist_start =0    #this is distance from start of the node you are at
    all_explored=[]
    prev=at
    while(len(objs)!=0):
        explored[at]=prev
        if (at in objs):
 #           print("\n\nreached objective!!!",at)
            objs.remove(at) #remove objective from list. reached it!
            curr_obj = at
            curr_path=[]
            while(True):   #now make it seem like this is the new begining for the next node
                curr_path.append(at)
                next=explored[at]
                if next==at: break
                at= next
            curr_path.reverse()
            path+=curr_path
            at=curr_obj
            for i in explored: #if it's not in all explored add it!
                if i not in all_explored:
                    all_explored.append(i)
            explored = {at:at} 
            frontier = []
            if len(objs)==0: break
        #if at in objs: objs.pop(objs.index(at)) #remove objective if your at it
        neighbors = maze.getNeighbors(at[0],at[1])
        for i in neighbors:     
            if ((i not in explored) and (i not in [n[0] for n in frontier])): 
                frontier.append([i,at,dist_start+1]) #insert all neighbors in queue
        #now find the node in frontier with smallest heuristic
        min_astar_h=frontier[0] 
 #       print("\n\n*STARTING FRONTIER AT: ",at,frontier)
        for f in frontier: #f[0] is cordinates, f[2] is cost-to-goal
#            print("considering",f) #f is a structure [current_cord,prev,_coord,distance]
#            print("rn min is ",min_astar_h)
            if h_astar(f[0],objs)+f[2]<h_astar(min_astar_h[0],objs)+min_astar_h[2]:
  #              print("\nYES! f is better!:",f)
                min_astar_h=f
#        print("min_astar is ",min_astar_h)
        at,prev,dist_start=min_astar_h 
        frontier.remove(min_astar_h)
#        print("explored",explored)
 #       print("at is: ",at)

#  #   print("Goal is: ",goal)
#     dist_start =0    #this is distance from start of the node you are at
#     while(at!=goal):
#         cnt+=1
#     #if at in objs: objs.pop(objs.index(at)) #remove objective if your at it
#         neighbors = maze.getNeighbors(at[0],at[1])
#         for i in neighbors:     
#             if ((i not in explored) and (i not in [n[0] for n in frontier])): 
#                 frontier.append([i,at,dist_start+1]) #insert all neighbors in queue
#         small_h=frontier[0] #find the node in frontier with smallest heuristic
#         for f in frontier:
#             if h_man(f[0],goal)+f[2]<h_man(small_h[0],goal)+small_h[2]:
#                 small_h=f
#         at,prev,dist_start=small_h 
#         frontier.remove(small_h)
#         explored[at]=prev
#  #       print("at is: ",at)

    #now at should be at goal so find path by going backwards through dictionary
 
    #return path, num_states_explored
    #return list(explored.keys()),cnt #if you want to see how much it explored
    #print(path)
    #return path,cnt
    return path,len(all_explored)