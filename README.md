# di-lambda
GUI for open hardware spectrophotometer
---------------------------------------

This project is intended to be a Graphic User Interface for [di-lambda spectrophotometer](http://wiki.openscienceschool.com/wiki/Tools/DI-Lambda)
It has a webinterface (at http://localhost:4444 ) to get measurements from spectrophotometer that is based on Akka-http.
The data from devices is obtained via connection to serial ports throw usb with a help of [flow](https://github.com/jodersky/flow) library.

Getting started
---------------

At the moment it does not have any installers (but it is planned) so you have to git clone it and run with sbt.