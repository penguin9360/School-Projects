#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import numpy as np 

pents= [np.array([[1],[1]]),np.array([[2],[2]])]
board = np.ones((2,2))

"""import numpy as np
p= np.array([[9,0,0],
                  [9,9,0],
                  [0,9,9]])
for fn in range(3):
    if fn >0:
        p=np.flip(p,fn-1)
    for rn in range(4):
        print("NEXT ONE:")
        for r in range(p.shape[0]):
            print(p[r])
        p=np.rot90(p)"""