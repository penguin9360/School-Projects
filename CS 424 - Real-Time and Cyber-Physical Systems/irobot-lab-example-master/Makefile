all: robotest

robotest: robotest.cc irobot-create.o
	g++ -std=c++11 -c robotest.cc
	g++ -std=c++11 -o robotest robotest.o irobot-create.o -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc

irobot-create.o: irobot-create.cc
	g++ -std=c++11 -c irobot-create.cc

clean:
	rm -f robotest *.o
