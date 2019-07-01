# TextClassifier.py
# ---------------
# Licensing Information:  You are free to use or extend this projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to the University of Illinois at Urbana-Champaign
#
# Created by Dhruv Agarwal (dhruva2@illinois.edu) on 02/21/2019

import math #should be in standard library
import sys

"""
You should only modify code within this file -- the unrevised staff files will be used for all other
files and classes when code is run, so be careful to not modify anything else.
"""
class TextClassifier(object):
    def __init__(self):
        """Implementation of Naive Bayes for multiclass classification

        :param lambda_mixture - (Extra Credit) This param controls the proportion of contribution of Bigram
        and Unigram model in the mixture model. Hard Code the value you find to be most suitable for your model
        """
        self.lambda_mixture = 0.0
        self.liklihood={} #dictionary of words as keys and arrays[14] of classes
        self.bigram={} #dictionary of word tuples as keys and arrays[14] of classes
        self.priors=[0]*14

    def fit(self, train_set, train_label):
        """
        :param train_set - List of list of words corresponding with each text
            example: suppose I had two emails 'i like pie' and 'i like cake' in my training set
            Then train_set := [['i','like','pie'], ['i','like','cake']]

        :param train_labels - List of labels corresponding with train_set
            example: Suppose I had two texts, first one was class 0 and second one was class 1.
            Then train_labels := [0,1]
        """

        # TODO: Write your code here
        # print("Scoping dimensions")
        # print("train_set has:",len(train_set),"first has this many words:", len(train_set[0]) \
        #     ,"last has:",len(train_set[-1]))
        total = [0]*14  #total number of words in class c
        #how many times word appears in c/ total number of words in class c
        for i,text in enumerate(train_set):
            total[train_label[i]-1]+=len(text) #count total text for class c
            self.priors[train_label[i]-1]+=1
            for j,word in enumerate(text):
                if word not in self.liklihood: #inintialize word with class array in dictionary if it hasn't been seen before
                    self.liklihood[word]=[0]*14
                self.liklihood[word][train_label[i]-1]+=1 #count how many times the word has been seen
                
                if j==len(text)-1: continue
                if (word,text[j+1]) not in self.bigram:
                    self.bigram[(word,text[j+1])]=[0]*14
                self.bigram[(word,text[j+1])][train_label[i]-1]+=1

        #laplace smoothing and divide into probability
        k=.11 #experimentally determined k value
        for i,d in enumerate(self.liklihood.values()): #go through dictionary 
            for j,elt in enumerate(d):  #go through each class element (can't do element wise ops with numpy easily)
                d[j]+=k         #if i do elt+=k, it doesn't work lol python is confusing
                d[j]/=(total[j]+len(self.liklihood)*k) #it seems to make references for enuemrate(self.liklihood) but not for enumerate(list)
        #now smooth bigram model and divide into probability
        for i,d in enumerate(self.bigram.values()):
            for j,elt in enumerate(d):
                d[j]+=k 
                d[j]/=((total[j]-1)*k) #there is one less bigram then total data

        #also divide for priors
        for i,p in enumerate(self.priors):
            if self.priors[i]==0: #this is to prevent error in case of overflow
                self.priors[i]=float_info.min 
                continue
            self.priors[i]/=len(train_set) #doing p/=here doesn't work but somehow
            

    def predict(self, x_set, dev_label,lambda_mix=0.0):
        """
        :param dev_set: List of list of words corresponding with each text in dev set that we are testing on
              It follows the same format as train_set
        :param dev_label : List of class labels corresponding to each text
        :param lambda_mix : Will be supplied the value you hard code for self.lambda_mixture if you attempt extra credit

        :return:
                accuracy(float): average accuracy value for dev dataset
                result (list) : predicted class for each text
        """
       # print("TOTAL AMT OF TEXT EXAMPLES!:",len(x_set))
        accuracy = 0.0
        result = []

        # TODO: Write your code here

        sum_vals=[0]*14 #unigram model log sum
        sum_bi_vals=[0]*14  #bigram model log sum
        sum_mix=[0]*14 #sum of mixture model
        true_val_count=[0]*14 #counts how many of each class is in dev label
        classif_rate=[0]*14
        confusion=[[0 for x in range(14)] for x in range(14)]
        
        for i,text in enumerate(x_set):
            sum_vals=[0]*14
            for j,word in enumerate(text):
                if word in self.liklihood:  #skip words that aren't in self liklihood
                    for c,possible_class in enumerate(sum_vals):
                        sum_vals[c]+=math.log(self.liklihood[word][c])
                        if j==len(text)-1 or ((word,text[j+1]) not in self.bigram): break 
                        sum_bi_vals[c]+=math.log(self.bigram[(word,text[j+1])][c])
                        
            for c,_ in enumerate(sum_vals):
                sum_vals[c]+= math.log(self.priors[c]) #math.log(1/14)
                sum_bi_vals[c]+=math.log(self.priors[c])
                sum_mix[c]=sum_vals[c]*(1-lambda_mix)+sum_bi_vals[c]*(lambda_mix)

            prediction = sum_mix.index(max(sum_mix)) +1   #plus 1 bc they want labels 1-14 not 0-13
            correct = dev_label[i]
            result.append(prediction)
            #print(prediction)
            #now update classification rate
            if prediction==correct:
                classif_rate[correct-1]+=1
           # if correct==1: print("ITS THERE!",prediction)
            true_val_count[correct-1]+=1

            #confusion matrix as well
            confusion[correct-1][prediction-1]+=1
            
        # print("class rate count:",classif_rate)
        # print("true_val_count:",true_val_count)
        #print("Confusion Matrix:")
        #get true classification rate (divide by true val count)
        for c,_ in enumerate(classif_rate):
            classif_rate[c]/=true_val_count[c]
            for c_pred,_ in enumerate(classif_rate):
                confusion[c][c_pred]/=true_val_count[c]
            #     print("{0:.5f}".format(confusion[c][c_pred]),end=" ") #will print a row of the confusion matrix
            # print()
        
        #average classification rate is accuracy
        accuracy=sum(classif_rate)/len(classif_rate)
     #   print("classification rates:",classif_rate)
     #   print("accuracy:",accuracy)

        """ #20 top feature words:
        print("feature words:")
        feat=[{} for x in range(14)] #each dictionary should be 20 words : values
        for word,class_arr in self.liklihood.items():
            for c,val in enumerate(class_arr):
                if len(feat[c])<20:    #if we're less than 20 words than duh add it
                    feat[c][word]=val
                    continue 
                
                min_word = min(feat[c],key=feat[c].get)
                if val>feat[c][min_word]: #if value of word you on is greater than the minimimum frequency word (i think min(dictionary) will look at values of dictionary)
                    feat[c].pop(min_word,None)  #remove that smaller word
                    feat[c][word]=self.liklihood[word][c]  #add the new word
        
        #print(feat)
        #maybe print these to a file for easier formatting
        for c,freq_dic in enumerate(feat):
            print("\n",c)
            #print(freq_dic)
            s_dic= sorted(freq_dic.items(),key=lambda kv_tuple: kv_tuple[1]) #lamda funciton takes one arg and just gets the first index of it
            s_dic.reverse()
            for _,pair in enumerate(s_dic):
             #   print('"'+str(pair[0])+'"',"\t\twith probability given this class: ","{0:.8f}".format(pair[1]))
                print('"'+str(pair[0])+'"'," {0:.8f}".format(pair[1]))"""

        
        return accuracy,result

