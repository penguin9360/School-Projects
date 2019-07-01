import numpy as np

class MultiClassPerceptron(object):
	def __init__(self,num_class,feature_dim):
		"""Initialize a multi class perceptron model.

		This function will initialize a feature_dim weight vector,
		for each class.

		The LAST index of feature_dim is assumed to be the bias term,
			self.w[:,0] = [w1,w2,w3...,BIAS]
			where wi corresponds to each feature dimension,
			0 corresponds to class 0.

		Args:
		    num_class(int): number of classes to classify
		    feature_dim(int): feature dimension for each example
		"""
		self.num_class=num_class
		self.feature_dim=feature_dim
		self.w = np.zeros((feature_dim+1,num_class)) #doesn't adding bias to end of feature dim array mess it up? bias is supposed not be multiplied by the class value
													# like it's y=Wx+b not y=x(W+b)
													#^^^no bc when ur doing multiplication we're going to be appending 1's to end of x so it will end up being xW+1*b
	def train(self,train_set,train_label,):
		""" Train perceptron model (self.w) with training dataset.

		Args:
		    train_set(numpy.ndarray): training examples with a dimension of (# of examples, feature_dim)
		    train_label(numpy.ndarray): training labels with a dimension of (# of examples, )
		"""

		# YOUR CODE HERE
		self.w[-1]=np.ones(self.num_class) 	#set bias term to 1
		# df= .75
		# b=.68
		# lr_arr=np.ones(self.num_class)#learning rate (different for each class)
		lr = 1
		

		runs=10
		rcount=0
		while rcount<runs:
			for i,ex in enumerate(train_set):
				correct=train_label[i]
				while True:
					ans=np.matmul(np.append(ex,1),self.w)	#matrix multiplication
					prediction=np.argmax(ans)
					if prediction==train_label[i]: #if prediciton is correct break
						break
					#print(lr_arr[correct])
					self.w[:,prediction] -= np.append(ex,0)*lr #*lr_arr[correct] #otherwise update weights and loop again
					self.w[:,correct] += np.append(ex,0)*lr #*lr_arr[correct]
				#lr_arr[correct]= lr_arr[correct]*df if lr_arr[correct]>b else b

			#print("on run",rcount)
			rcount+=1
			
			lr=1/rcount
			#lr_arr[correct]= lr_arr[correct]*df if lr_arr[correct]>b else b
			#lr= lr-.00001 if lr<.6 else .6
			#lr= lr*.9 if lr<.7 else .7

		pass

	def test(self,test_set,test_label):
		""" Test the trained perceptron model (self.w) using testing dataset.
			The accuracy is computed as the average of correctness
			by comparing between predicted label and true label.

		Args:
		    test_set(numpy.ndarray): testing examples with a dimension of (# of examples, feature_dim)
		    test_label(numpy.ndarray): testing labels with a dimension of (# of examples, )

		Returns:
			accuracy(float): average accuracy value
			pred_label(numpy.ndarray): predicted labels with a dimension of (# of examples, )
		"""

		# YOUR CODE HERE
		accuracy = 0
		pred_label = np.zeros((len(test_set)))
		classif_rate=np.zeros(self.num_class) #counts correctly labeled examples (like numerator of classification rate)
		true_labels_count=np.zeros(self.num_class)	# counts all labels in test set (like denomenator of classification rate)
		hi_val=np.zeros(self.num_class) #this will hold max prob 
		hi_img=np.zeros((self.num_class,self.feature_dim))	#this will hold max img data in dim 
		lo_val=np.zeros(self.num_class) #this will hold lowest prob 
		lo_img=np.zeros((self.num_class,self.feature_dim))	#this will hold lowest img data in dim 

		for i,ex in enumerate(test_set):
			ans=np.matmul(np.append(ex,1), self.w)
			prediction=np.argmax(ans)
			correct=test_label[i]
			pred_label[i]=prediction
			if correct==prediction:
				classif_rate[correct]+=1
			true_labels_count[correct]+=1

			#store highest and lowest probability
			if hi_val[prediction]==0: #initialize it to first one
				hi_val[prediction]=ans[prediction]
				hi_img[prediction]=ex
				lo_val[prediction]=ans[prediction]
				lo_img[prediction]=ex
				continue
			if ans[prediction]>hi_val[prediction]:
				hi_val[prediction]=ans[prediction]
				hi_img[prediction]=ex
			if ans[prediction]<lo_val[prediction]:
				lo_val[prediction]=ans[prediction]
				lo_img[prediction]=ex

		classif_rate/=true_labels_count #element wise division
		accuracy=np.average(classif_rate)
		# print("highest perceptron scores:",hi_val)
		# print("lowest perceptron scores:",lo_val)
		# print("classification rates:",classif_rate)
		# print("accuracy:",accuracy)

		return accuracy, pred_label #,np.transpose(hi_img),np.transpose(lo_img)

	def save_model(self, weight_file):
		""" Save the trained model parameters
		"""

		np.save(weight_file,self.w)

	def load_model(self, weight_file):
		""" Load the trained model parameters
		"""

		self.w = np.load(weight_file)
