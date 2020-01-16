# Setting Up Raspberry Pi, Camera, and iRobot Create

## Introduction

* Raspberry Pi is a small single­board computer with USB, WiFi, Bluetooth, Ethernet, HDMI, Audio, and GPIO connectivity. In cs424, we will be controlling an iRobot create using a Raspberry Pi 3 Model B. This particular model is the most capable compared to the other models. It has 4 cores each clocked to 1.2 GHz, and 1 GB of RAM. The minimum hardware required to run the system is (1) a Raspberry Pi motherboard, (2) a MicroSD memory card, and (3) Power supply. Additionally we will also be using the Raspberry Pi Camera Module v2 as the “vision” for our robot.

* **General Precautions** : Raspberry Pi is a bare motherboard and has the electrical connections exposed. Therefore do not put it on a metallic surface as it may short some terminals. When putting it on the payload bin of the iRobot, note that there are metallic screws there and so take necessary precautions to isolate (for example put it on a paper or a plastic). Try not to touch the pins of the chips by hand whether the Pi board is powered on or not. Sometimes the static charge from our body is enough to destroy the chips. Hold the board by the edges and discharge static from your body before setting the board down. When transporting use the anti­static bag that the Raspberry Pi was originally in.

## Set Up Configurations

### Before You Start

* Lucky you, we have already flash the micro SD card and installed Raspbian for you. However, there're a few more configurations that you need to do in order to be able to develop on the Raspberry Pi.

* In this tutorial, we will guide you through how to set up WiFi and installing denpendencies (which could take some time to complete). We recommend you to finish this tutorial in the lab, where you can access monitors and keyboards. Or if you have those in your home, feel free to follow this instruction in you own set up.

* All you need to do for this section is to connect the HDMI cable, keyboard, (mouse, if available) and plug in the power cord and the Raspberry Pi should power up automatically.

* Wait for a while until Raspberry Pi is complete boot up, you will see a set up wizard. In there, make sure you set up the language and timezone correctly and for the rest you can configure as much as you like.

### Configure WiFi

* Execute `​sudo nano /etc/wpa_supplicant/wpa_supplicant.conf`

* Go to the bottom of the file, and add a section like the following. This setting will work for most of your home WiFi networks, given you are using WPA­PSK scheme (which is default these days). You should put appropriate values for the fields named *your_home_wifi_name* and *your_home_wifi_passphrase*. Note the presence of the (") quotation marks that should enclose these values.
    <pre>
    network={
        ssid="<i>your_home_wifi_name</i>"
        psk="<i>​your_home_wifi_passphrase</i>"
        key_mgmt=WPA­-PSK
    }
    </pre>

* Using the same mechanism, we now configure IllinoisNet Enterprise network. Enterprise network requires both an identity (Your NetId) and a password (Your NetId password). ​For security reasons, instead of directly putting the password in plaintext, we will be storing the password hash.

* Execute the following command to generate the hash: `echo ­-n ' ​your_netid_password' | iconv -t UTF-16LE | openssl md4`. Note the single quotation marks around the plaintext password. ​You must use the single quotation marks around the plaintext password. Because passwords generally contain special characters, it may not work if you use double quotation or no quotation. The output of this command will look like
    ​<pre>
        (stdin)= <i>1234567890abcdefghijklmopqrstuvwx</i>
    </pre>

* Your output will contain some other hexadecimal string instead of ​*1234567890abcdefghijklmopqrstuvwx* depending on your NetId password. We should now execute ​`history ­cw` to remove terminal history as we typed password in plain text in the terminal, and that should not stay in the history.

* Once you have the password hash, add the following block to the `​wpa_supplicant.conf` file. Replace *1234567890abcdefghijklmopqrstuvwx* by the actual hash you generated, and ​*your_net_id* by your net_id. Note the absence of quotation marks (​"​) around `hash:`. Make sure that there is no space between the keyword `​hash:` and the hash itself (i.e. the hexadecimal string that your generated from your NetId password)
    <pre>
    network={
        ssid="IllinoisNet"
        key_mgmt=WPA­-EAP
        proto=WPA2
        eap=PEAP
        ca_cert="/etc/ssl/certs/AddTrust_External_Root.pem"
        identity="<i>your_net_id</i>"
        password=hash:<i>1234567890abcdefghijklmopqrstuvwx</i>
        phase1="peapver=0"
        phase2="MSCHAPV2"
    }
    </pre>
* Use `​Ctrl + o`​ to save your changes, `​Ctrl + x`​ to exit nano

* You might want to reboot your Raspberry Pi with `sudo reboot now` to let it pick up the new change. When it boot up, it will automatically connect to the network your setup above.

## Additional Configurations

### Change Password

* You can change the password of the user ​`pi` from default `​raspberry`​ to something else by using the command ​`passwd` (if you haven't modify the password during first boot up)

### Set Up NTP

* Run `sudo apt-get install ntp` to install network time protocol for synchronization.

## Interfacing to iRobot Create

### Connecting iRobot Create

* iRobot create comes with a yellow colored Roomba battery. The battery compartment is at the bottom of iRobot, check if you have the green battery container installed. The green container is used to hold AA which we won’t be using. So eject the green container from the battery compartment, install the yellow battery, and connect the charger to charge it. While iRobot is charging the power light will pulse in orange. Once fully charged, the power light will become green. Charge the battery to full before first using it.

* Note that the robot will not start if the charger is connected. Once the battery is charged, disconnect the charger, connect the RS­232 adapter in the serial port. Connect that Serial Port to the Raspberry Pi by using the provided Serial to USB adapter. Note that you can power the Raspberry Pi is with the provided battery pack. Or you can use the wall charger if you want to, but it will be hard for the robot to move around.
  
* Once the connection is complete, in Raspberry Pi, run the command `lsusb`​. It should include the following line, which means it detected the USB­Serial adapter.

    ```
    Bus ​xx Device ​yy: ID ​zz Prolific Technology, In c. PL2303 Serial Port
    ```

* If you do not have any other USB device connected to the Raspberry Pi, running `ls /dev/tty*` ​or `​ls /dev/ttyUSB*` will include a file `/dev/ttyUSB0` ​, which corresponds to the iRobot Create. If you have other USB devices connected to the Pi, you need to figure out which one corresponds to the USB­Serial adapter connecting the iRobot.

### Install Serial Communications Libraries and Dependencies

* In the terminal, type in the following
    ```sh
    # boost
    sudo apt-get install libboost-all-dev

    # SIP
    sudo apt-get install python-sip
    sudo apt-get install python-sip-dev

    # pyserial
    sudo apt-get install python-pip
    sudo pip install pyserial

    # libserial
    wget
    http://downloads.sourceforge.net/project/libserial/libserial/0.6.0rc2/libserial-0.6.0rc2.tar.gz
    tar -zxvf libserial-0.6.0rc2.tar.gz
    cd libserial-0.6.0rc2/
    ./configure
    make
    sudo make install
    sudo ldconfig
    ```

## Installing Camera

### Install OpenCV

* For our projects, we will use OpenCV to process feed from the Raspberry Pi Camera for obstacle detection and other applications.

* In the terminal, type in the following
    ```sh
    # takes about an hour to update everything
    sudo apt-get update
    sudo apt-get update

    # developer tools
    sudo apt-get install -y build-essential cmake pkg-config

    # git
    sudo apt-get install git

    # image compression
    sudo apt-get install -y libjpeg-dev libtiff5-dev

    # video io libraries
    sudo apt-get install -y libavcodec-dev libavformat-dev libswscale-dev
    sudo apt-get install -y libv4l-dev libxvidcore-dev libx264-dev

    # gui modules
    sudo apt-get install -y libgtk2.0-dev

    # optimization libraries
    sudo apt-get install -y libatlas-base-dev gfortran
    sudo apt-get install screen

    # python bindings and dependencies
    sudo apt-get install python2.7-dev python3-dev
    sudo apt-get install python-pip
    sudo pip install numpy
    ```

* Now we need to install OpenCV. Using `screen` makes sure that even if you close the terminal, the compiling is still running.

    ```sh
    screen
    cd ~

    # download OpenCV
    wget -O opencv.zip https://github.com/Itseez/opencv/archive/3.1.0.zip
    unzip opencv.zip
    wget -O opencv_contrib.zip https://github.com/Itseez/opencv_contrib/archive/3.1.0.zip
    unzip opencv_contrib.zip

    # compile OpenCV
    cd ~/opencv-3.1.0/
    mkdir build
    cd build
    cmake -D CMAKE_BUILD_TYPE=RELEASE \
    -D CMAKE_INSTALL_PREFIX=/usr/local \
    -D INSTALL_PYTHON_EXAMPLES=ON \
    -D OPENCV_EXTRA_MODULES_PATH=~/opencv_contrib-3.1.0/modules \
    -D ENABLE_PRECOMPILED_HEADERS=OFF \
    -D BUILD_EXAMPLES=ON ..
    make -j4
    ```
* The last ​`make ­-j4` command is a time consuming step as it compiles OpenCV. It uses 4 Raspberry Pi cores, and can take from 1~1.5 hours. In case the parallel compile errors out for race conditions, you can clean the build by executing ​`make clean` and then using ​`make` to compile using a single core. ​`make`​ will take more time as opposed to ​`make ­-j4`

* Now install OpenCV libraries, run the following

    ```sh
    sudo make install
    sudo ldconfig
    ```

### Install RaspiCam

* In this section we install RaspiCam

    ```sh
    git clone https://github.com/cedricve/raspicam
    cd raspicam
    mkdir build
    cd build
    cmake ..
    ```

* Check the output of cmake and it should include lines like the following. Make sure `CREATE_OPENCV_MODULE` is configured as 1, which means it has properly detected the OpenCV installation.

    ```
    ­­-- CREATE OPENCV MODULE=1
    ­­-- CMAKE_INSTALL_PREFIX=/usr/local
    ...
    ...
    ...
    ­--­ Configuring done
    ­­-- Generating done
    ­­-- Build files have been written to: /home/pi/raspicam/trunk/build
    ```

* Now compile and install raspicam

    ```sh
    make
    sudo make install
    sudo ldconfig
    ```

* Next enable the camera module. To do it run `sudo raspi-config`, select `Interfacing Options` -> `P1 Camera` and enable it. Reboot the Raspberry Pi with `sudo reboot now`

* Congratulations, you're now ready to move on to mp1

## Headless Mode

* You might be wondering whether you need to plug in the monitor and keyboard everytime your boot up the Raspberry Pi. The answer is fortunately, no. As long as you have configured the WiFi or Ethernet the Pi should connect to. It will have the same IP address unless you explicity change it or reboot the Pi.

* For now, we ask that you boot up the Raspberry Pi with a monitor. You can run `ifconfig` in the terminal and look up Raspberry Pi's current ip address. Later you can ssh into your Raspberry Pi with the same address and then you can remove the connection to the monitor, your ssh session should not be interfered.
  
* We have also provided you a Ethernet cable, if you have a router in your home. You can connect the Raspberry Pi with the router and boot it up. (No additional set up required) Later you can find its IP address in your router's console and ssh into Raspberry Pi to continue the set up.

* We understand that this seems to be a fairly burdensome chore to do everytime you want to connect to the Raspberry Pi and we are actively working on some improvements to give you a pure "headless" experience with Raspberry Pi. Please keep an eye on our Piazza announcements.