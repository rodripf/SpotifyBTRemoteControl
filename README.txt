Spotify BT Remote Control
=========================

Spotify BT Remote Control is a small project to control the spotify
player from your Java powered cell phone (Nokia, Sony Ericsson, etc.)
via Bluetooth.

REQUERIMENTS
------------

-   Only for MS Windows
-   Java Runtime Environment 1.7 ([Download from source][1])
-   MS .NET Framework 4 ([Download from source][2])
-   Bluetooth enabled computer

-   Java compatible device
-   Bluetooth enabled device

FEATURES
--------

-   Play, Pause, Next, Previous, Volume Up, Volume Down, Mute
-   Get artist and song

INSTALATION
-----------

### SERVER (on the PC)

1.  Unpack the contents of the Spotify\_BT\_Remote\_Control.rar package
2.  Run Server/SpotifyServer.jar in your computer
3.  Check if your computer is visible for bluetooth devices (in
    bluetooth configuration)

### CLIENT (on the mobile)

-   Install the .jar file in the Client folder to your device
-   Run it on your device
-   Locate your computer and enjoy!

TESTED ON
---------

-   Server: Win7 x86 / Client: Nokia C2-01

*If you try it, please give me your feedback!*

TODO
----

-   Finish the search and play option
-   Translate to English and other languages (currently only Spanish)
-   Fix errors when you send orders repeatedly

MODIFY / CONTRIBUTE
-------------------

The project is not developed in the best possible way… but it works. Is
my first cell phone app and also my first bluetooth app, so I was
copying and pasting a lot, ergo disorder… The project is divided in 3
parts:

-   A Visual Basic 2010 project to control the spotify player via
    SendMessage and such things
-   A Java SE project for NetBeans to make the bluetooth server and
    interact with the VB application
-   A Java ME project for NetBeans to make the mobile application

  [1]: http://java.com/en/download/index.jsp
  [2]: http://www.microsoft.com/en-us/download/details.aspx?id=17851