<h1>Spotify BT Remote Control</h1>

Spotify BT Remote Control is a small project to control the spotify player from your Java powered cell phone (Nokia, Sony Ericsson, etc.) via Bluetooth.

<h2>REQUERIMENTS</h2>
<ul>
<li>Only for MS Windows</li>
<li>Java Runtime Environment 1.7 (<a href="http://java.com/en/download/index.jsp">Download from source</a>)</li>
<li>MS .NET Framework 4 (<a href="http://www.microsoft.com/en-us/download/details.aspx?id=17851">Download from source</a>)</li>
<li>Bluetooth enabled computer</li>
</ul>

<ul>
<li>Java compatible device</li>
<li>Bluetooth enabled device</li>
</ul>

<h2>FEATURES</h2>
<ul>
<li>Play, Pause, Next, Previous, Volume Up, Volume Down, Mute</li>
<li>Get artist and song</li>
</ul>

<h2>INSTALATION</h2>
<h3>SERVER (on the PC)</h3>
<ol>
<li>Unpack the contents of the Spotify_BT_Remote_Control.rar package</li>
<li>Run Server/SpotifyServer.jar in your computer</li>
<li>Check if your computer is visible for bluetooth devices (in bluetooth configuration)</li>
</ol>

<h3>CLIENT (on the mobile)</h3>
<ul>
<li>Install the .jar file in the Client folder to your device</li>
<li>Run it on your device</li>
<li>Locate your computer and enjoy!</li>
</ul>

<h2>TESTED ON</h2>
<ul>
<li>Server: Win7 x86 / Client: Nokia C2-01</li>
</ul>

<i>If you try it, please give me your feedback!</i>

<h2>TODO</h2>
<ul>
<li>Finish the search and play option</li>
<li>Translate to English and other languages (currently only Spanish)</li>
<li>Fix errors when you send orders repeatedly</li>
</ul>

<h2>MODIFY / CONTRIBUTE</h2>
The project is not developed in the best possible way... but it works. Is my first cell phone app and also my first bluetooth app, so I was copying and pasting a lot, ergo disorder...
The project is divided in 3 parts:
<ul>
<li>A Visual Basic 2010 project to control the spotify player via SendMessage and such things</li>
<li>A Java SE project for NetBeans to make the bluetooth server and interact with the VB application</li>
<li>A Java ME project for NetBeans to make the mobile application</li>
</ul>


