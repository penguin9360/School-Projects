# text_main.py
# ---------------
# Licensing Information:  You are free to use or extend this projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to the University of Illinois at Urbana-Champaign
#
# Created by Dhruv Agarwal (dhruva2@illinois.edu) on 02/21/2019

import csv
from TextClassifier import TextClassifier
import string

"""#THESE ARE JUST FOR CONFUSTION MATRIX FUNCTION!
import numpy as np
# from perceptron import MultiClassPerceptron
# from naive_bayes import NaiveBayes
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
from sklearn.utils.multiclass import unique_labels"""

"""
This file contains the main application that is run for this part of the MP.
No need to modify this file
"""

def read_stop_words(filename):
    """
       Reads in the stop words which are used for data preprocessing
       Returns a set of stop words
    """
    stop_words=set()
    with open(filename, encoding="utf8") as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')

        for row in csv_reader:
            for word in row:
                stop_words.add(word.strip(" '"))
    stop_words.remove('')

    return stop_words

def readFile(filename,stop_words):
    """
    Loads the files in the folder and returns a list of lists of words from
    the text in each file and the corresponding labels
    """
    translator = str.maketrans("", "", string.punctuation)
    with open(filename, encoding="utf8") as csv_file:
        labels = []
        data = []
        csv_reader = csv.reader(csv_file, delimiter=',')

        for row in csv_reader:
            labels.append(int(row[0]))
            row[2] = row[2].lower()
            text= row[2].translate(translator).split()
            text = [w for w in text if w not in stop_words]
            data.append(text)

    return data,labels


def load_dataset(data_dir=''):
    """
    :param data_dir: directory path to your data
    :return: both the train and test data sets
    """
    stop_words=read_stop_words(data_dir+'stop_words.csv')
    x_train, y_train = readFile(data_dir+'train_text.csv',stop_words)
    x_test, y_test = readFile(data_dir+'dev_text.csv',stop_words)

    return x_train,y_train,x_test,y_test

def compute_results(actual_labels,pred_labels):
    """

    :param actual_labels: Gold labels for the given texts
    :param pred_labels: Predicted Labels for the given texts
    """
    precision=[]
    recall = []
    for c in range(1,15):
        actual_c = {i for i in range(len(actual_labels)) if actual_labels[i] == c}
        pred_c = {i for i in range(len(pred_labels)) if pred_labels[i] == c}
        tp = len(actual_c.intersection(pred_c))

        if len(pred_c) > 0:
            precision.append(tp/len(pred_c))
        else:
            precision.append(0.0)

        recall.append(tp/len(actual_c))

    f1=[2 * (p * r) / (p+r) if (p+r) !=0.0 else 0.0 for p, r in zip(precision,recall) ]

    print ("Precision for all classes :",precision)
    print ("Recall for all classes:",recall)
    print ("F1 Score for all classes:",f1)

# def plot_confusion_matrix(y_true, y_pred, classes,
#                           normalize=False,
#                           title=None,
#                           cmap=plt.cm.Blues):
#     """
#     This function prints and plots the confusion matrix.
#     Normalization can be applied by setting `normalize=True`.
#     """
#     if not title:
#         if normalize:
#             title = 'Normalized confusion matrix'
#         else:
#             title = 'Confusion matrix, without normalization'

#     # Compute confusion matrix
#     cm = confusion_matrix(y_true, y_pred)
#     # Only use the labels that appear in the data
#     classes = classes[unique_labels(y_true, y_pred)]
#     if normalize:
#         cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
#         print("Normalized confusion matrix")
#     else:
#         print('Confusion matrix, without normalization')

#     print(cm)

#     fig, ax = plt.subplots()
#     im = ax.imshow(cm, interpolation='nearest', cmap=cmap)
#     ax.figure.colorbar(im, ax=ax)
#     # We want to show all ticks...
#     ax.set(xticks=np.arange(cm.shape[1]),
#            yticks=np.arange(cm.shape[0]),
#            # ... and label them with the respective list entries
#            xticklabels=classes, yticklabels=classes,
#            title=title,
#            ylabel='True label',
#            xlabel='Predicted label')

#     # Rotate the tick labels and set their alignment.
#     plt.setp(ax.get_xticklabels(), rotation=45, ha="right",
#              rotation_mode="anchor")

#     # Loop over data dimensions and create text annotations.
#     fmt = '.2f' if normalize else 'd'
#     thresh = cm.max() / 2.
#     for i in range(cm.shape[0]):
#         for j in range(cm.shape[1]):
#             ax.text(j, i, format(cm[i, j], fmt),
#                     ha="center", va="center",
#                     color="white" if cm[i, j] > thresh else "black")
#     fig.tight_layout()
#     return ax

if __name__ == '__main__':
    x_train, y_train, x_test, y_test = load_dataset()
    MNB = TextClassifier()
    MNB.fit(x_train, y_train)
    """
        #code to determine best k value (for laplace smoothing)
    k=0
    accs=[]
    ks=[]
    while k<=1:
        #MNB.fit(x_train, y_train,k)
        accuracy,_ = MNB.predict(x_test, y_test,k)
        accs.append(accuracy)
        ks.append(k)
        k+=.01
        print(k,accuracy)
    print("Max is: ",max(accs), "with",ks[accs.index(max(accs))])
    #Max is:  0.918993709104398 with 0.11
    #^jk this is unigram with laplace smmothing of /total*k not /(total + num unique words*k)
    #best i 0 with 
    """
    accuracy,pred = MNB.predict(x_test, y_test)
    compute_results(y_test,pred)
    #plot_confusion_matrix(y_test,pred,classes=['1','2','3','4','5','6','7','8','9','10','11','12','13','14'])

    print("Accuracy {0:.4f}".format(accuracy))