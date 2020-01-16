// Copyright (C) 2008 by Thomas Moulard, the University of Southern California
// (USC), and iLab at USC.
//
// This file is part of the iRobot Create library.
//
// libirobot-create is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// libirobot-create is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with libirobot-create.  If not, see <http://www.gnu.org/licenses/>.

/**
 * \file irobot-create-fwd.hh
 *
 * \brief Forward declarations.
 */

#ifndef IROBOT_CREATE_FWD_HH
# define IROBOT_CREATE_FWD_HH

namespace iRobot
{
  class CommandNotAvailable;
  class InvalidArgument;

  class Create;
} // end of namespace iRobot.

// Forward declaration of the LibSerial classes
// that are used by the library.
namespace LibSerial
{
  class SerialStream;
} // end of namespace LibSerial.

#endif //! IROBOT_CREATE_FWD_HH
