all: robotest

robotest: robotest.cc irobot-create.o object_identification.o
	g++ -std=c++11 -c robotest.cc
	g++ -std=c++11 -o robotest robotest.o irobot-create.o object_identification.o -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

findme: thread_safe_robot findme.cc irobot-create.o
	g++ -std=c++11 -c findme.cc
	g++ -std=c++11 -o findme findme.o irobot-create.o -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

mp3: mp3.cc irobot-create.o navigation_manager.o handle_robot_safety.o contour_mapper.o object_identification.o navigator.o
	g++ -std=c++11 -c mp3.cc 
	g++ -std=c++11 -g -o mp3 mp3.o irobot-create.o handle_robot_safety.o contour_mapper.o naive_heuristic.o navigation_manager.o object_identification.o navigator.o -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

external:
	g++ -std=c++11 -c utility/external.cc
	g++ -std=c++11 -o external external.o

irobot-create.o: ./irobot/irobot-create.cc
	g++ -std=c++11 -c ./irobot/irobot-create.cc

thread_safe_robot: ./thread-safe-robot/thread_safe_robot.cc
	g++ -std=c++11 -c ./thread-safe-robot/thread_safe_robot.cc

object_identification.o: ./object-identification/object_identification.cc naive_heuristic.o
	g++ -std=c++11 -c ./object-identification/object_identification.cc -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

naive_heuristic.o: ./object-identification/naive_heuristic.cc
	g++ -std=c++11 -c ./object-identification/naive_heuristic.cc -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

handle_robot_safety.o: ./robot-safety/handle_robot_safety.cc navigation_manager.o
	g++ -std=c++11 -c ./robot-safety/handle_robot_safety.cc 	

navigation_manager.o: irobot-create.o ./navigation-manager/navigation_manager.cc
	g++ -std=c++11 -c ./navigation-manager/navigation_manager.cc

navigator.o: irobot-create.o ./navigation-manager/navigator.cc
	g++ -std=c++11 -c ./navigation-manager/navigator.cc

contour_mapper.o: ./contour-mapper/contour_mapper.cc
	g++ -std=c++11 -c ./contour-mapper/contour_mapper.cc	

clean:
	rm -f robotest mp3 *.o

# Testing files go below -----

test_contour: contour_mapper.o
	g++ -std=c++11 -c tests/test_contour.cc
	g++ -std=c++11 -o test_contour contour_mapper.o test_contour.o -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

test_detection: tests/test_detection.cc object_identification.o naive_heuristic.o
	g++ -std=c++11 -c tests/test_detection.cc
	g++ -std=c++11 -o test_detection object_identification.o naive_heuristic.o test_detection.o -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

fuck: fuck.cc irobot-create.o navigation_manager.o contour_mapper.o object_identification.o
	g++ -std=c++11 -c fuck.cc 
	g++ -std=c++11 -o fuck fuck.o irobot-create.o navigation_manager.o object_identification.o contour_mapper.o -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

test_navigator: ./tests/test_navigator.cc navigator navigation_manager.o irobot-create.o
	g++ -std=c++11 -c tests/test_navigator.cc 
	g++ -std=c++11 -o test_navigator test_navigator.o irobot-create.o navigator.o navigation_manager.o -L/opt/vc/lib -lserial -lmmal -lmmal_core -lmmal_util

navigator: irobot-create.o navigation_manager.o 
	g++ -std=c++11 -c ./navigation-manager/navigator.cc 

clean_test:
	rm -f test_*
