# Android Line Follower Robot
An autonomous Line follower Robot.
The Robot is based on an Arduino SDK board that interface with an Android Smartphone.
An Android app uses image processing to detect the line to follow and then calculate the correct PWM signal to send to the motors using a PID control system.

The Android app contains a settings menu that allow control of:
  - PID settings: Proportional, Integral and Derivative values.
  - Turn On/Off the flash light.
  - Choose the background color ( clear or dark ).
  - Limit the maximum motors speed.
  
<img src="Pics/Photo_1.jpg" width="400"> <img src="Pics/androidarduino.png" width="400">

# Eletrical Components:
The Robot is composed of:
 - An Android smartphone with the Line Follower app installed.
 - An arduin Mega ADK board.
   - More Info: https://www.arduino.cc/en/Main/ArduinoBoardMegaADK?from=Main.ArduinoBoardADK
 - One H-bridge motor driver.
   - Reference: L293D
   - Datasheet: Android-Line-Follower-Robot\Datasheets\L293D.pdf 
 - Two DC Motors.
-  One 9 V Battery.

# Screen Shots from the app:
Prebuilt app: Android-Line-Follower-Robot\Line_Follower.apk

<img src="Screen_Shots/Screenshot_2.png" width="400">    <img src="Screen_Shots/Screenshot_3.png" width="400">

<img src="Screen_Shots/Screenshot_4.png" width="400">    <img src="Screen_Shots/Screenshot_1.png" width="400">

# Wiring Components:
   
   The Motors:
   
   <img src="Electrical_Wiring/wiring_l293d.png" width="500">
   
   
