# di-lambda
GUI for open hardware spectrophotometer
---------------------------------------

This project is intended to be a Graphic User Interface for [di-lambda spectrophotometer](http://wiki.openscienceschool.com/wiki/Tools/DI-Lambda)
It has a webinterface (at http://localhost:4444 ) to get measurements from spectrophotometer that is based on Akka-http.
The data from devices is obtained via connection to serial ports through usb with a help of [flow](https://github.com/jodersky/flow) library.


Getting started
---------------

At the moment it does not have any installers (but it is planned) so you have to git clone it and run with sbt.
    * git clone https://github.com/antonkulaga/di-lambda
    * Install sbt (from http://www.scala-sbt.org/ ):
    * go to the repositorey and type following commands
        $ sbt   //sbt console
        $ re-start //from sbt console
        
Warning
-------

The project is on early stage so it is buggy. It works on Debian (Ubutu/Mint) based Linux but I have not tested it with other OSes.
There are also some bugs connected with unpluging of spectophotometer from network, so it is safer to switch the app off before unplugin.