import numpy as np

import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
from sklearn.utils.multiclass import unique_labels

def plot_confusion_matrix(y_true, y_pred, classes,
                          normalize=False,
                          title=None,
                          cmap=plt.cm.Blues):
    """
    This function prints and plots the confusion matrix.
    Normalization can be applied by setting `normalize=True`.
    """
    if not title:
        if normalize:
            title = 'Normalized confusion matrix'
        else:
            title = 'Confusion matrix, without normalization'

    # Compute confusion matrix
    cm = confusion_matrix(y_true, y_pred)
    # Only use the labels that appear in the data
    classes = classes[unique_labels(y_true, y_pred)]
    if normalize:
        cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
        print("Normalized confusion matrix")
    else:
        print('Confusion matrix, without normalization')

    print(cm)

    fig, ax = plt.subplots()
    im = ax.imshow(cm, interpolation='nearest', cmap=cmap)
    ax.figure.colorbar(im, ax=ax)
    # We want to show all ticks...
    ax.set(xticks=np.arange(cm.shape[1]),
           yticks=np.arange(cm.shape[0]),
           # ... and label them with the respective list entries
           xticklabels=classes, yticklabels=classes,
           title=title,
           ylabel='True label',
           xlabel='Predicted label')

    # Rotate the tick labels and set their alignment.
    plt.setp(ax.get_xticklabels(), rotation=45, ha="right",
             rotation_mode="anchor")

    # Loop over data dimensions and create text annotations.
    fmt = '.2f' if normalize else 'd'
    thresh = cm.max() / 2.
    for i in range(cm.shape[0]):
        for j in range(cm.shape[1]):
            ax.text(j, i, format(cm[i, j], fmt),
                    ha="center", va="center",
                    color="white" if cm[i, j] > thresh else "black")
    fig.tight_layout()
    return ax

"""
    Minigratch Gradient Descent Function to train model
    1. Format the data
    2. call four_nn function to obtain losses
    3. Return all the weights/biases and a list of losses at each epoch
    Args:
        epoch (int) - number of iterations to run through neural net
        w1, w2, w3, w4, b1, b2, b3, b4 (numpy arrays) - starting weights
        x_train (np array) - (n,d) numpy array where d=number of features
        y_train (np array) - (n,) all the labels corresponding to x_train
        num_classes (int) - number of classes (range of y_train)
        shuffle (bool) - shuffle data at each epoch if True. Turn this off for testing.
    Returns:
        w1, w2, w3, w4, b1, b2, b3, b4 (numpy arrays) - resulting weights
        losses (list of ints) - each index should correspond to epoch number
            Note that len(losses) == epoch
    Hints:
        Should work for any number of features and classes
        Good idea to print the epoch number at each iteration for sanity checks!
        (Stdout print will not affect autograder as long as runtime is within limits)
"""
def minibatch_gd(epoch, w1, w2, w3, w4, b1, b2, b3, b4, x_train, y_train, num_classes, shuffle=True):
    batch_size=200
    Ws=[w1,w2,w3,w4]
    bs=[b1,b2,b3,b4]
    losses=[]
   # print("epoch",epoch,"num_classes","xshape:", x_train.shape,"yshape",y_train.shape)
    for e in range(epoch):
        print("at epoch ",e)
        if shuffle: #mixes data (along first index)
            #make sure to shuffle both x and y!
            c=list(zip(x_train,y_train)) 
            np.random.shuffle(c)
            x_shuffled,y_shuffled = zip(*c) 
            x_shuffled=np.array(x_shuffled)
            y_shuffled=np.array(y_shuffled)
        else:
            x_shuffled=x_train
            y_shuffled=y_train
        loss=0
        for i in range(x_train.shape[0]//batch_size): #// is integer division   
        #    print("at i",i,"batch from:to",i*batch_size,":",(i+1)*batch_size)
            X=x_shuffled[i*batch_size:(i+1)*batch_size,:] #this makes the batch
            y=y_shuffled[i*batch_size:(i+1)*batch_size]

            loss+=four_nn(X,Ws,bs,y,False) #we are training the network, so False
        #    print("loss is",loss)
        losses.append(loss)

    w1, w2, w3, w4 = Ws
    b1, b2, b3, b4 = bs
    return w1, w2, w3, w4, b1, b2, b3, b4, np.array(losses)

"""
    Use the trained weights & biases to see how well the nn performs
        on the test data
    Args:
        All the weights/biases from minibatch_gd()
        x_test (np array) - (n', d) numpy array
        y_test (np array) - (n',) all the labels corresponding to x_test
        num_classesz (int) - number of classes (range of y_test)
    Returns:
        avg_class_rate (float) - average classification rate
        class_rate_per_class (list of floats) - Classification Rate per class
            (index corresponding to class number)
    Hints:
        Good place to show your confusion matrix as well.
        The confusion matrix won't be autograded but necessary in report.
"""
def test_nn(w1, w2, w3, w4, b1, b2, b3, b4, x_test, y_test, num_classes):
    print("Testing nn")
    avg_class_rate = 0.0
    class_rate_per_class = np.array([0.0] * num_classes)
    classes_count = np.array([0]*num_classes) #this just counts how many images are tested for each class
    Ws=[w1,w2,w3,w4]
    bs=[b1,b2,b3,b4]

    results = four_nn(x_test,Ws,bs,y_test,True)

    for i,correct in enumerate(y_test):
        #print(correct)
        classes_count[correct]+=1
        if correct==results[i]:
            class_rate_per_class[correct]+=1
    print("classes_counts:",classes_count)
    print("correct counts:",class_rate_per_class)
    avg_class_rate=np.sum(class_rate_per_class)/len(y_test)
    class_rate_per_class/=classes_count
    
    class_names = np.array(["T-shirt/top","Trouser","Pullover","Dress",
        "Coat","Sandal","Shirt","Sneaker","Bag","Ankle boot"])
    plot_confusion_matrix(y_test, results, classes=class_names, normalize=True,
                    title='Confusion matrix, with normalization')
    plt.show() 

    return avg_class_rate, class_rate_per_class

"""
    4 Layer Neural Network
    Helper function for minibatch_gd
    Up to you on how to implement this, won't be unit tested
    Should call helper functions below
    Basically runs inputs it through neural network
    Args:
    X (np array) - (n,d) of inputs
    Ws (np.array)
"""
def four_nn(X,Ws,bs,y,test):
    
    Z1,acache1=affine_forward(X,Ws[0],bs[0])
    A1,rcache1=relu_forward(Z1)
    Z2,acache2=affine_forward(A1,Ws[1],bs[1])
    A2,rcache2=relu_forward(Z2)
    Z3,acache3=affine_forward(A2,Ws[2],bs[2])
    A3,rcache3=relu_forward(Z3)
    F,acache4=affine_forward(A3,Ws[3],bs[3])
    #A4,rcache4=relu_forward(Z4)

    if test==True:
        return np.argmax(F,axis=1)
    
    loss,dF=cross_entropy(F,y)
    dA3,dW4,db4=affine_backward(dF,acache4)
    dZ3=relu_backward(dA3,rcache3)
    dA2,dW3,db3=affine_backward(dZ3,acache3)
    dZ2=relu_backward(dA2,rcache2)
    dA1,dW2,db2=affine_backward(dZ2,acache2)
    dZ1=relu_backward(dA1,rcache1)
    dX,dW1,db1=affine_backward(dZ1,acache1)

    eta = .1 # this is learning rate
    #pass by ref for np arrays so this should update the original weight
    Ws[3]-=eta*dW4
    Ws[2]-=eta*dW3 
    Ws[1]-=eta*dW2 
    Ws[0]-=eta*dW1 

    return loss

"""
    Next five functions will be used in four_nn() as helper functions.
    All these functions will be autograded, and a unit test script is provided as unit_test.py.
    The cache object format is up to you, we will only autograde the computed matrices.

    Args and Return values are specified in the MP docs
    Hint: Utilize numpy as much as possible for max efficiency.
        This is a great time to review on your linear algebra as well.
"""
def affine_forward(A, W, b):
    #NOTE: np broadcasts W so if A is an array containing multiple (n,d) (classs,feature) inputs 
    #then np will do each@W (@ is matrix mult) +b
    Z=np.matmul(A,W)+b
    cache=(A,W,b)
    return Z, cache

def affine_backward(dZ, cache):
    A,W,b=cache
    dA=np.matmul(dZ,np.transpose(W))
    dW=np.matmul(np.transpose(A),dZ)
    dB=np.sum(dZ,0)
    return dA, dW, dB

def relu_forward(Z):
    cache=Z
    A=np.array(Z) #make copy of Z into A
    A=np.where(A>0,A,0)

    #THIS IS THE SAME THING AS ABOVE JUST SLOWER
    # for i,val in enumerate(Z):
    #     for j,num in enumerate(val):
    #         if num<0: 
    #             A[i,j]= 0
    return A, cache

def relu_backward(dA, cache):
    Z=cache 
    dZ=np.where(Z<=0,0,dA)

    #THIS IS THE SAME THING AS ABOVE JUST SLOWER
    #dZ=np.array(dA)
    # for i,val in enumerate(Z):
    #     for j,num in enumerate(val):
    #         print("num:",num)
    #         if (num<0): 
    #             print("\tChanged dZ")
    #             dZ[i,j]=0
    return dZ

def cross_entropy(F, y):
    loss=0
    dF=np.zeros(F.shape)
    for i,row in enumerate(F):
        exp_sum=np.sum(np.exp(row))
        #print("IN CROSS ENTROPY: i",i,"y[i]",y[i])
        loss+= row[int(y[i])] - np.log(exp_sum) #for loss 
        for j,val in enumerate(row): #for dF
            dF[i,j]=int(j==y[i]) - np.exp(val)/exp_sum

    loss/=-F.shape[0]
    dF/=-F.shape[0]

    return loss, dF
