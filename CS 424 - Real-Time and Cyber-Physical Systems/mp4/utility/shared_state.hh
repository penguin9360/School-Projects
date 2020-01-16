#pragma once

#include "../navigation-manager/navigation_manager.hh"

#include <mutex>
#include <ctime> 
#include <ostream>

//namespace Color {
//    enum Code {
//        FG_RED      = 31,
//        FG_GREEN    = 32,
//        FG_BLUE     = 34,
//        FG_DEFAULT  = 39,
//        BG_RED      = 41,
//        BG_GREEN    = 42,
//        BG_BLUE     = 44,
//        BG_DEFAULT  = 49
//    };
//    class Modifier {
//        Code code;
//    public:
//        Modifier(Code pCode) : code(pCode) {}
//        friend std::ostream&
//        operator<<(std::ostream& os, const Modifier& mod) {
//            return os << "\033[" << mod.code << "m";
//        }
//    };
//}

typedef struct SharedState {
  bool go; // Whether system is stopped.
  NavigationManager * nav_manager; // Central point to send nav commands to robot.
  std::mutex *robot_mutex;
  bool done; //Whether navigation is done or not

} SharedState;
