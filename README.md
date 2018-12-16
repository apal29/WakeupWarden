# WakeupWarden
Mini-Capstone ELEC 390 Project

This Android mobile application is an alarm manager for the Wakeup Warden alarm clock. The app allows you to create, delete, and modify
desired alarm times as well as easily toggle them ON/OFF. The project uses Firebase Real-Time Database to store the set alarms to be
retrieved by the Wakeup Warden alarm clock.

The hardware component of the project features the WEMOS D1R2 microcontroller with a built in ESP8266 WiFi module as well as a Phidgets
Capacitive Touch Sensor used to trigger the alarm ON/OFF. Once an alarm is set via the Wakeup Warden mobile app, the alarm clock retrieves
the alarms from Firebase as well as the current time to be displayed on the connected LCD display. When the alarm is triggered, a printed
attached to a servo motor launches an object (rubber ball) off the sensor, thus triggering the alarm. The only way to stop the alarm is to
retreive the ball and place it back on the touch sensor.
