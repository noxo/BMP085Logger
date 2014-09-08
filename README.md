BMP085Logger
============

Display log of air pressure and temperature using Raspberry PI + Java and BMP085 sensor

Features:
========
* Jetty based Web UI for showing JFreeChart 2D charts of temperature and pressure
* HSQLDB database for storing temperature and pressure data
* Integration with BMP085 sensor using PI4J library

Required software
=================
* Linux (preferrable Debian Wheezy)
* Java
* Wiring PI library (http://wiringpi.com/download-and-install/) [required by PI4J]

I2C setup on PI
===============
* connect BMP085 sensor 
* add "i2c-bcm2708" and "i2c-dev" in /etc/modules
* reboot to get modules loaded
* apt-get install i2c-tools
* run i2cdetect -y 1 (RPI v2) or i2cdetect -y 0 (RPI v1)
* should see 0x77 in i2cdetect output if BMP085 is properly connected

Running service
===============
* export TZ=Europe/Helsinki
* java -cp hsqldb.jar:jcommon-1.0.23.jar:jetty-all-9.0.4.v20130625.jar:jfreechart-1.0.19.jar:pi4j-core.jar:servlet-api-3.1.jar org.noxo.bmp085logger.LoggerServer

Browsing measurements
=====================
* hourly: http://aaa.bbb.ccc.ddd:8080/?range=hourly
* daily: http://aaa.bbb.ccc.ddd:8080/

