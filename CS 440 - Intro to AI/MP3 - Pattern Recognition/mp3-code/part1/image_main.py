# Main function for train, test and visualize. 
# You do not need to modify this file. 

import numpy as np
from perceptron import MultiClassPerceptron
from naive_bayes import NaiveBayes
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
from sklearn.utils.multiclass import unique_labels

def load_dataset(data_dir=''):
    """Load the train and test examples 
    """
    x_train = np.load("data/x_train.npy")
    y_train = np.load("data/y_train.npy")
    x_test = np.load("data/x_test.npy")
    y_test = np.load("data/y_test.npy")

    return x_train, y_train, x_test, y_test

def plot_visualization(images, classes, cmap):
    """Plot the visualizations 
    """    
    fig, ax = plt.subplots(2, 5, figsize=(12, 5))
    for i in range(10):
        ax[i%2, i//2].imshow(images[:, i].reshape((28, 28)), cmap=cmap)
        ax[i%2, i//2].set_xticks([])
        ax[i%2, i//2].set_yticks([])
        ax[i%2, i//2].set_title(classes[i])
    plt.show()

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


if __name__ == '__main__':

    # Load dataset. 
    x_train, y_train, x_test, y_test = load_dataset()

    
        # Initialize naive bayes model.
    num_class = len(np.unique(y_train))
    feature_dim = len(x_train[0]) 
    num_value = 256
    class_names = np.array(["T-shirt/top","Trouser","Pullover","Dress",
        "Coat","Sandal","Shirt","Sneaker","Bag","Ankle boot"])
    """ 
    #testing various K values
    NB = NaiveBayes(num_class,feature_dim,num_value)
    ks=np.linspace(.1,10,100)
    accuracies=[]
    for i,k in enumerate(ks):
        NB = NaiveBayes(num_class,feature_dim,num_value)
        NB.train(x_train,y_train,k)
        accuracy, _ = NB.test(x_test,y_test)
        accuracies.append(accuracies)
        print("done with k=",k," accuracy of ",accuracy)

    print("max accuracy is ",max(accuracies), "with k= ",accuracies.index(max(accuracies)))

    """
    NB = NaiveBayes(num_class,feature_dim,num_value)
    # Train model.
   # NB.train(x_train,y_train)
   # NB.save_model("testp","testli")
    NB.load_model("testp.npy","testli.npy")
    # Feature likelihood for high intensity pixels. 
    feature_likelihoods = NB.intensity_feature_likelihoods(NB.likelihood)
    # Visualize the feature likelihoods for high intensity pixels. 
    plot_visualization(feature_likelihoods, class_names, "Greys")
    # Classify the test sets. 
    accuracy, y_pred = NB.test(x_test,y_test)
    #imshow(hi_img[:, 0].reshape((28, 28)))
    #plot_visualization(hi_img,class_names,"Greys")
    #plot_visualization(lo_img,class_names,"Greys")
    # Plot confusion matrix. 
    plot_confusion_matrix(y_test, y_pred, classes=class_names, normalize=True,
                      title='Confusion matrix, with normalization')
    plt.show()
    
    
    # Initialize perceptron model.    
    perceptron = MultiClassPerceptron(num_class,feature_dim)

    """    
    #training loop:
    dfs=np.linspace(1,.1,91)
    base=np.linspace(1,.1,91)
    accs=np.zeros(91*91)
    np.load("accs_accuracy_arr.npy",accs)
    print(accs)
    for x,df in enumerate(dfs):
        for y,b in enumerate(base):
            perceptron.train(x_train,y_train,df,b)
            accs[x*91+y],_=perceptron.test(x_test,y_test)
            if x%5==0 and y==0: print("at: df ",df,", b",b, "w/ acc",accs[x])
    np.save("accs_accuracy_arr",accs)
    maxi=accs.index(max(accs))
    print("Max is ",maxi," with val: ",dfs[int(maxi//91)],"and base ",base[int(maxi%91)])
    """
    # test=[]
    # t=np.linspace(1,.1,10)
    # for ind,val in enumerate(t):
    #     perceptron.train(x_train,y_train,val)
    #     a,_=perceptron.test(x_test,y_test)
    #     test.append(a)
    #     print("t=",val,"\tresult=",a)
    # print("Max of",max(test))
    
    """
    Testing cutoff lowest:
    Max is  0.7502  with val:  0.68

    Testing Decrease multiplicative factor:
    Max is  0.7706  with val:  0.75 testing from 1-.6
    Max is  0.7599  with val:  0.36 .6 was other thing

    at: df  1.0 , b 1.0 w/ acc 0.6895
    at: df  0.95 , b 1.0 w/ acc 0.681
    at: df  0.9 , b 1.0 w/ acc 0.6853999999999999
    at: df  0.85 , b 1.0 w/ acc 0.7487000000000001
    at: df  0.8 , b 1.0 w/ acc 0.7112
    at: df  0.75 , b 1.0 w/ acc 0.74
    at: df  0.7 , b 1.0 w/ acc 0.7079000000000001
    at: df  0.6499999999999999 , b 1.0 w/ acc 0.7306
    at: df  0.6 , b 1.0 w/ acc 0.7305
    at: df  0.55 , b 1.0 w/ acc 0.7279
    at: df  0.5 , b 1.0 w/ acc 0.6902
    at: df  0.44999999999999996 , b 1.0 w/ acc 0.6937
    at: df  0.4 , b 1.0 w/ acc 0.7245999999999999
    at: df  0.35 , b 1.0 w/ acc 0.7123
    at: df  0.29999999999999993 , b 1.0 w/ acc 0.7144000000000001
    at: df  0.25 , b 1.0 w/ acc 0.7649
    at: df  0.19999999999999996 , b 1.0 w/ acc 0.7327000000000001
    at: df  0.15000000000000002 , b 1.0 w/ acc 0.7386
    at: df  0.1 , b 1.0 w/ acc 0.7419
    
    """
    # Train model.
    perceptron.train(x_train,y_train)
    # Visualize the learned perceptron weights. 
    plot_visualization(perceptron.w[:-1,:], class_names, None)
    # Classify the test sets. 
    accuracy, y_pred= perceptron.test(x_test,y_test)
    #plot_visualization(hi_img,class_names,"Greys")
    #plot_visualization(lo_img,class_names,"Greys")
    # Plot confusion matrix.
    plot_confusion_matrix(y_test, y_pred, classes=class_names, normalize=True,
                      title='Confusion matrix, with normalization')
    plt.show()    
    
