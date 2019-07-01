import numpy as np
import utils
import random


class Agent:
    
    def __init__(self, actions, Ne, C, gamma):
        self.actions = actions
        self.Ne = Ne # used in exploration function
        self.C = C
        self.gamma = gamma

        # Create the Q and N Table to work with
        self.Q = utils.create_q_table()
        self.N = utils.create_q_table() #is this a count of how many times that state has been visited?

        self.reset()

    def train(self):
        self._train = True
        
    def eval(self):
        self._train = False

    # At the end of training save the trained model
    def save_model(self,model_path):
        utils.save(model_path, self.Q)

    # Load the trained model for evaluation
    def load_model(self,model_path):
        self.Q = utils.load(model_path)

    def reset(self):
        self.points = 0
        self.s = None   #what the poopoo do these lines do?
        self.a = None

    def act(self, state, points, dead):
        '''
        :param state: a list of [snake_head_x, snake_head_y, snake_body, food_x, food_y] from environment.
        snake_body in state is a list of x,y tuples (multiples of 40) where first tuple is last (tail of snake) 
        and last tuple is box right after head

        :param points: float, the current points from environment
        :param dead: boolean, if the snake is dead
        :return: the index of action. 0,1,2,3 indicates up,down,left,right separately

        TODO: write your function here.
        Return the index of action the snake needs to take, according to the state and points known from environment.
        Tips: you need to discretize the state to the state space defined on the webpage first.
        (Note that [adjoining_wall_x=0, adjoining_wall_y=0] is also the case when snake runs out of the 480x480 board)

        State: A tuple (adjoining_wall_x, adjoining_wall_y, food_dir_x, food_dir_y, adjoining_body_top, adjoining_body_bottom, adjoining_body_left, adjoining_body_right).
            [adjoining_wall_x, adjoining_wall_y] gives whether there is wall next to snake head. It has 9 states: 
                adjoining_wall_x: 0 (no adjoining wall on x axis), 1 (wall on snake head left), 2 (wall on snake head right) 
                adjoining_wall_y: 0 (no adjoining wall on y axis), 1 (wall on snake head top), 2 (wall on snake head bottom) 
                (Note that [0, 0] is also the case when snake runs out of the 480x480 board)
            [food_dir_x, food_dir_y] gives the direction of food to snake head. It has 9 states: 
                food_dir_x: 0 (same coords on x axis), 1 (food on snake head left), 2 (food on snake head right)
              food_dir_y: 0 (same coords on y axis), 1 (food on snake head top), 2 (food on snake head bottom)
            [adjoining_body_top, adjoining_body_bottom, adjoining_body_left, adjoining_body_right] checks if there is snake body in adjoining square of snake head. It has 8 states: 
                adjoining_body_top: 1 (adjoining top square has snake body), 0 (otherwise) 
                adjoining_body_bottom: 1 (adjoining bottom square has snake body), 0 (otherwise) 
                adjoining_body_left: 1 (adjoining left square has snake body), 0 (otherwise) 
                adjoining_body_right: 1 (adjoining right square has snake body), 0 (otherwise)
    Actions: Your agent's actions are chosen from the set {up, down, left, right}.
    Rewards: +1 when your action results in getting the food (snake head position is the same as the food position),
            -1 when the snake dies, that is when snake head hits the wall, its body segment or the head tries to move towards its adjacent body segment (moving backwards).
            -0.1 otherwise (does not die nor get food).
            also for exploration function 1 for R+: 
        '''
        #print("state:",state)
        snake_head_x, snake_head_y, snake_body, food_x, food_y = state
  
    #1. Descritize states (get states index into Qtable)
       #top left for snake head to still be alive and go into is 40,40, top right is 480,40, bottom right is 480,480, bottom left i 40,480
        
        s_new=(
            2 if food_x>snake_head_x else (1 if food_x<snake_head_x else 0),
            2 if food_y>snake_head_y else (1 if food_y<snake_head_y else 0),
            1 if ((snake_head_x,snake_head_y-40) in snake_body) or snake_head_y==40 else 0,
            1 if ((snake_head_x,snake_head_y+40) in snake_body) or snake_head_y==480 else 0,
            1 if ((snake_head_x-40,snake_head_y) in snake_body) or snake_head_x==40 else 0,
            1 if ((snake_head_x+40,snake_head_y) in snake_body) or snake_head_x==480 else 0,
            1 if ((snake_head_x,snake_head_y-80) in snake_body) or snake_head_y==80 else 0,
            1 if ((snake_head_x,snake_head_y+80) in snake_body) or snake_head_y==440 else 0,
            1 if ((snake_head_x-80,snake_head_y) in snake_body) or snake_head_x==80 else 0,
            1 if ((snake_head_x+80,snake_head_y) in snake_body) or snake_head_x==440 else 0,
            )
            
        # ik this looks silly but it's the only way i could access arrays
        sn1,sn2,sn3,sn4,sn5,sn6,sn7,sn8,sn9,sn10=s_new
        
    #2. Return best action if we're not training
        if not self.train:
            return self.actions[np.argmax(self.Q[sn1,sn2,sn3,sn4,sn5,sn6,sn7,sn8,sn9,sn10:])]

    #3. Obtain reward
        reward=0
        if points-self.points==1: 
            #snake got the food!
            self.points+=1 
            reward=1
        elif dead:
            #snakey :(
            reward=-1
        else:
            #default reward
            reward=-.1

    #4. Update the Q table with the LAST action that you took (not the one you are going to take) since the reward should be attributed to that action, not the next 
        if not (self.s==None and self.a==None): #this doesn't update Qtable if u are at first step (just resetted)
            #update learning rate
            a,b,c,d,e,f,g,h,i,j=self.s
            N=self.N[a,b,c,d,e,f,g,h,i,j,self.a]
            alpha = self.C/(self.C+N)

            #update Q table
            Q=self.Q[a,b,c,d,e,f,g,h,i,j,:]
            #below should actually be the set of actions corresponding to this new state!
            Qs_next=np.max(self.Q[sn1,sn2,sn3,sn4,sn5,sn6,sn7,sn8,sn9,sn10, :])
            self.Q[a,b,c,d,e,f,g,h,i,j,self.a]+=alpha*(reward+self.gamma*Qs_next-self.Q[a,b,c,d,e,f,g,h,i,j,self.a])
            

    #5. If dead don't take another action just reset and leave
        if dead:
            self.reset()
            return 0
    
    #6. Now that we have fininshed updating Q table with previous state and action we can look at our new state and decide the next action
        self.s=s_new

    #7 Finally, chose next best action using exploration function
    #TODO: Fix this exploration function i think    
        Ns=self.N[sn1,sn2,sn3,sn4,sn5,sn6,sn7,sn8,sn9,sn10,:]
        f=np.ones(len(self.actions))
        for action,freq in enumerate(Ns):
            if (freq>=self.Ne):
                f[action]=self.Q[sn1,sn2,sn3,sn4,sn5,sn6,sn7,sn8,sn9,sn10, action]
        #self.a=np.argmax(f)
        max_ind=3 #priority is backwards (from greatest index to lowest!)
        for act,val in enumerate(f):
            if val>= f[max_ind]:
                max_ind=act
        self.a=max_ind

    #8. update count of how many times we've seen that action and state 
        self.N[sn1,sn2,sn3,sn4,sn5,sn6,sn7,sn8,sn9,sn10, self.a]+=1

        return self.actions[self.a]


"""checkpoint 1 has average of 13.23
    checkpoint 2 has average of 22.16
    checkpoint 3 has average of 12.047    
"""

