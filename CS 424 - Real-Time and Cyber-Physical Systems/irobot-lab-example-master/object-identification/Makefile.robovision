all: robovision

robovision: robovision.cc
	g++ -std=c++11 -o robovision robovision.cc -L/opt/vc/lib -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d -lopencv_imgcodecs

clean:
	rm -f robovision
