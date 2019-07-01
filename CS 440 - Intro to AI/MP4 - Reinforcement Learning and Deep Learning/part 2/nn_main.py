from neural_network import minibatch_gd, test_nn
import numpy as np
import time
import matplotlib.pyplot as plt

def init_weights(d, dp):
    return 0.01 * np.random.uniform(0.0, 1.0, (d, dp)), np.zeros(dp)

if __name__ == '__main__':
    x_train = np.load("data/x_train.npy")
    x_train = (x_train - np.mean(x_train, axis=0)) / np.std(x_train, axis=0)
    y_train = np.load("data/y_train.npy")

    x_test = np.load("data/x_test.npy")
    x_test = (x_test - np.mean(x_test, axis=0))/np.std(x_test, axis=0)
    y_test = np.load("data/y_test.npy")

    load_weights = True #set to True if you want to use saved weights

    if load_weights:
        w1 = np.load('w1.npy')
        w2 = np.load('w2.npy')
        w3 = np.load('w3.npy')
        w4 = np.load('w4.npy')

        b1 = np.load('b1.npy')
        b2 = np.load('b2.npy')
        b3 = np.load('b3.npy')
        b4 = np.load('b4.npy')

        losses = np.load("losses.npy")
    else:
        w1, b1 = init_weights(784, 256)
        w2, b2 = init_weights(256, 256)
        w3, b3 = init_weights(256, 256)
        w4, b4 = init_weights(256, 10)

    start=time.time()
    #w1, w2, w3, w4, b1, b2, b3, b4, losses = minibatch_gd(50, w1, w2, w3, w4, b1, b2, b3, b4, x_train, y_train, 10)
    runtime = time.time()-start
    np.save('w1', w1)
    np.save('w2', w2)
    np.save('w3', w3)
    np.save('w4', w4)
    np.save('losses',losses)

    np.save('b1', b1)
    np.save('b2', b2)
    np.save('b3', b3)
    np.save('b4', b4)

    plt.plot(range(len(losses)),losses,'b-o')
    plt.title("Epochs vs. Losses")
    plt.xlabel("Epoch #")
    plt.ylabel("Loss number")
    plt.show()
    avg_class_rate, class_rate_per_class = test_nn(w1, w2, w3, w4, b1, b2, b3, b4, x_test, y_test, 10)

    print("avg classification rate:", avg_class_rate)
    print("classification rates per class:", class_rate_per_class)
    print("runtime:",runtime,"seconds")

    
