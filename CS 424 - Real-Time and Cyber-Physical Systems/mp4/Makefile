all: robotest

mp4: mp4.cc irobot-create.o navigation_manager.o handle_robot_safety.o control_loop_navigator.o controller.o
	g++ -std=c++11 -c mp4.cc 
	g++ -std=c++11 -g -o mp4 mp4.o irobot-create.o handle_robot_safety.o navigation_manager.o control_loop_navigator.o controller.o -pthread -L/opt/vc/lib -lserial -lmmal -lmmal_core -lmmal_util

irobot-create.o: ./irobot/irobot-create.cc
	g++ -std=c++11 -c ./irobot/irobot-create.cc

object_identification.o: ./object-identification/object_identification.cc naive_heuristic.o
	g++ -std=c++11 -c ./object-identification/object_identification.cc -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util -lopencv_core -lopencv_imgcodecs -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_calib3d -lopencv_xfeatures2d

handle_robot_safety.o: ./robot-safety/handle_robot_safety.cc navigation_manager.o
	g++ -std=c++11 -c ./robot-safety/handle_robot_safety.cc 	

navigation_manager.o: irobot-create.o ./navigation-manager/navigation_manager.cc
	g++ -std=c++11 -c ./navigation-manager/navigation_manager.cc

controller.o: ./control-loop/controller.cc
	g++ -std=c++11 -c ./control-loop/controller.cc

control_loop_navigator.o: controller.o navigation_manager.o ./control-loop/control_loop_navigator.cc
	g++ -std=c++11 -c ./control-loop/control_loop_navigator.cc


clean:
	rm -f robotest mp4 *.o

# Testing files go below -----
# :)

test_angle_translate: test_angle_translate.cc irobot-create.o navigation_manager.o
	g++ -std=c++11 -c test_angle_translate.cc 
	g++ -std=c++11 -g -o test_angle_translate test_angle_translate.o irobot-create.o navigation_manager.o -pthread -L/opt/vc/lib -lserial -lraspicam -lraspicam_cv -lmmal -lmmal_core -lmmal_util

clean_test:
	rm -f test_*
