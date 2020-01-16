all:
	g++ -std=c++11 -o test test.cc RobotIdentification.cc -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc

debug:
	g++ -g -std=c++11 -o test test.cc RobotIdentification.cc -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc

clean:
	rm test
