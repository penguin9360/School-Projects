import numpy as np

class NaiveBayes(object):
	def __init__(self,num_class,feature_dim,num_value):
		"""Initialize a naive bayes model. 

		This function will initialize prior and likelihood, where 
		prior is P(class) with a dimension of (# of class,)
			that estimates the empirical frequencies of different classes in the training set.
		likelihood is P(F_i = f | class) with a dimension of 
			(# of features/pixels per image, # of possible values per pixel, # of class),
			that computes the probability of every pixel location i being value f for every class label.  

		Args:
		    num_class(int): number of classes to classify 10
		    feature_dim(int): feature dimension for each example 28x28=784
		    num_value(int): number of possible values for each pixel 0-255 so 256 
		"""

		self.num_value = num_value
		self.num_class = num_class
		self.feature_dim = feature_dim

		self.prior = np.zeros((num_class))
		self.likelihood = np.zeros((feature_dim,num_value,num_class))

	def train(self,train_set,train_label):
		""" Train naive bayes model (self.prior and self.likelihood) with training dataset. 
			self.prior(numpy.ndarray): training set class prior (in log) with a dimension of (# of class,),
			self.likelihood(numpy.ndarray): traing set likelihood (in log) with a dimension of 
				(# of features/pixels per image, # of possible values per pixel, # of class).
			You should apply Laplace smoothing to compute the likelihood. 

		Args:
		    train_set(numpy.ndarray): training examples with a dimension of (# of examples, feature_dim) #each of these is val between 0-255?
		    train_label(numpy.ndarray): training labels with a dimension of (# of examples, ) #each of these is a val between 0-9 (class label)
		"""
		# YOUR CODE HERE
		sample_count=[]
		#obtain priors by emperical frequenceies
		#USE ENUMERATE TO SPEED UP THE LOOP!
		for i in range(self.num_class):
			s=list(train_label).count(i) #this should count how many things train label have index i
			sample_count.append(s)
			self.prior[i]=s/train_label.size
		#print(self.prior) #they all have the same priors? .1 ok

		# P(Fi = f | class) = (# of times pixel i has value f in training examples from this class) / (Total # of training examples from this class)

		#correct method
		for i,label in enumerate(train_label): #50000 examples
			for j,feat in enumerate(train_set[i]): #783 dims 
				self.likelihood[j,feat,label]+=1


		""" Could also do priors and liklihood at same time but that runs slower for some reason?
		#correct method
		for i,label in enumerate(train_label): #50000 examples
			for j,feat in enumerate(train_set[0]): #783 dims 
				self.likelihood[j,feat,label]+=1
				self.prior[label]+=1
		
		#make this next line after laplace smoothing and change lap smoothing
		#so that sample_count is self.prior[i]
		self.prior/=train_label
		"""
		#laplace smoothing
		k=.1
		self.likelihood+=k
		for i in range(self.num_class):
			self.likelihood[:,:,i]/=(sample_count[i]+self.num_value*k)
		

		#possible faster method:--jk don't do this!
		#make arrays of classes
		# arr_class = np.empty([self.num_class,train_set.shape[0],train_set.shape[1]])
		# for i in range(train_set.shape[0]):
		# 	np.append(arr_class[train_label[i]],train_set[i]) #add all training examples to this class
		# for c in range(self.num_class):
		# 	for f in range(self.feature_dim):
		# 		self.likelihood[f,:,c]=np.bincount(arr_class[c,f,:]) #bincount should make an array of indices 0-255 indicating how often each index appears in the various examples of feature f in class c
		# #c=np.array([[[1,1,1],[2,2,2],[3,3,3]],[[4,4,4],[5,5,5],[6,6,6]]])<--Array to test splicing
		# print("fin")


		
	def test(self,test_set,test_label):
		""" Test the trained naive bayes model (self.prior and self.likelihood) on testing dataset,
			by performing maximum a posteriori (MAP) classification.  
			The accuracy is computed as the average of correctness 
			by comparing between predicted label and true label. 

		Args:
		    test_set(numpy.ndarray): testing examples with a dimension of (# of examples, feature_dim)
		    test_label(numpy.ndarray): testing labels with a dimension of (# of examples, )

		Returns:
			accuracy(float): average accuracy value  
			pred_label(numpy.ndarray): predicted labels with a dimension of (# of examples, )
		"""    
		#YOUR CODE HERE
		# initialization
		sum=np.zeros(self.num_class)	#this is a row of all classes and their running sums
		pred_label=np.zeros(test_set.shape[0])

		#all the following are for evaluation
		classif_rate=np.zeros(self.num_class) #counts correctly labeled examples (like numerator of classification rate)
		true_labels_count=np.zeros(self.num_class)	# counts all labels in test set (like denomenator of classification rate)
		#confusion=np.zeros((self.num_class,self.num_class))	#confusion matrix
		hi_val=np.zeros(self.num_class) #this will hold max prob 
		hi_img=np.zeros((self.num_class,self.feature_dim))	#this will hold max img data in dim 
		lo_val=np.zeros(self.num_class) #this will hold lowest prob 
		lo_img=np.zeros((self.num_class,self.feature_dim))	#this will hold lowest img data in dim 

		
		for i,test in enumerate(test_set): #10000 test examples
			sum=np.zeros(self.num_class) #clear sum for next round
			for j,feat in enumerate(test):	#783 features
				sum+=np.log(self.likelihood[j,feat,:])
			sum+=np.log(self.prior)	#add the entire row of priors to the row of sums
		#	print("sum is:",sum)
			prediction=np.argmax(sum) #gives index of max val
			correct=test_label[i]
		#	print("predicting: ",prediction)
			pred_label[i]=prediction #return value 	
		#	return(test,sum)

			#below is evaluation stuff
			#classification rate
			if prediction==correct:
				classif_rate[prediction]+=1
			true_labels_count[correct]+=1

			#confusion matrix--jk apparently they already do this
			#confusion[correct,prediction]+=1

			#store highest and lowest probability
			if hi_val[prediction]==0: #initialize it to first one
				hi_val[prediction]=sum[prediction]
				hi_img[prediction]=test
				lo_val[prediction]=sum[prediction]
				lo_img[prediction]=test
				continue
			if sum[prediction]>hi_val[prediction]:
				hi_val[prediction]=sum[prediction]
				hi_img[prediction]=test
			if sum[prediction]<lo_val[prediction]:
				lo_val[prediction]=sum[prediction]
				lo_img[prediction]=test
			

		classif_rate/=true_labels_count
		
		"""
		jk apparently we don't need to print our own matrix
		print("Confusion Matrix") 
		for r, row in enumerate(confusion):
			row/=true_labels_count #this will make it a percentage... ideally the matrix should have all 1s in the diagonal
			#print(row) 
		"""

		accuracy = np.average(classif_rate)
		# print("highest log probabilities:",hi_val)
		# print("lowest log probabilities:",lo_val)
		# print("classification rates:",classif_rate)
		# print("accuracy:",accuracy)
		
		return accuracy, pred_label #,np.transpose(hi_img),np.transpose(lo_img)


	def save_model(self, prior, likelihood):
		""" Save the trained model parameters 
		"""    

		np.save(prior, self.prior)
		np.save(likelihood, self.likelihood)

	def load_model(self, prior, likelihood):
		""" Load the trained model parameters 
		""" 

		self.prior = np.load(prior)
		self.likelihood = np.load(likelihood)
	
	def intensity_feature_likelihoods(self,likelihood):
		"""
	    Get the feature likelihoods for high intensity pixels for each of the classes,
	        by sum the probabilities of the top 128 intensities at each pixel location,
	        sum k<-128:255 P(F_i = k | c).
	        This helps generate visualization of trained likelihood images. 
	    
	    Args:
	        likelihood(numpy.ndarray): likelihood (in log) with a dimension of
	            (# of features/pixels per image, # of possible values per pixel, # of class)
	    Returns:
	        feature_likelihoods(numpy.ndarray): feature likelihoods for each class with a dimension of
	            (# of features/pixels per image, # of class)
	    """
		#print("are classes 0 and 1 the same? ", np.array_equal(likelihood[:,:,0],likelihood[:,:,1]))
		feature_likelihoods = np.sum(likelihood[:,128:,:],1) #can see it works with test=np.ones(10,256,5); np.sum(test[:,128:,:],1)

		return feature_likelihoods
