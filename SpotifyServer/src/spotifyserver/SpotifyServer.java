/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifyserver;

/**
 *
 * @author Rodrigo
 */
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;


import java.util.logging.Logger;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * A class that demonstrates Bluetooth communication between server mode PC and
 * client mode device through serial port profile.
 *
 * @see <a href="http://sourceforge.net/projects/bluecove/">BlueCove - JSR-82
 * implementation</a>
 */
public class SpotifyServer {
    
    static final String PLAY_PAUSE = "PlayPause";
    static final String PLAY_NEXT = "PlayNext";
    static final String PLAY_PREV = "PlayPrev";
    static final String VOL_DOWN = "VolumeDown";
    static final String VOL_UP = "VolumeUp";
    static final String MUTE = "Mute";
    static final String BUSCAR = "Buscar";
    static final String NOW_PLAY = "NowPlaying";

    /*-
     * ================
     * Bluetooth Server
     * ================
     * 
     * This example application is a straighforward implementation of 
     * a bluetooth server.
     * 
     * 
     * Usage
     * =====
     * 
     * Start the program. Events are logged by printing them out with standard 
     * output stream. Once the server is up and ready to accept connections, 
     * connect to server with client.
     * 
     * 
     * How it Works
     * ============
     * 
     * The application starts a loop to wait for a new connection. After a new 
     * connection is reseived the connection is handled. In the handling 
     * operation few tokens and end token is written to connection stream. 
     * Each read token is logged to standard output. After handling session 
     * the loop continues by waiting for a new connection.
     * 
     */

    /*-
     * 
     * ---- Bluetooth attributes ----
     */
    // serial port profile
    protected String UUID = new UUID("1101", true).toString();
    protected int discoveryMode = DiscoveryAgent.GIAC; // no paring needed
    public static InputStream in;
    public static OutputStream out;
    private static boolean ServerRun = true;
    Image aa;
    /*-
     * 
     * ---- Connection handling attributes ----
     */
    protected int endToken = 255;

    public SpotifyServer() {
        ServerRun = true;

        try {
            LocalDevice device = LocalDevice.getLocalDevice();
            device.setDiscoverable(DiscoveryAgent.GIAC);

            String url = "btspp://localhost:" + UUID + ";name=PCServerCOMM";

            log("Create server by uri: " + url);
            StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(url);

            serverLoop(notifier);

        } catch (Throwable e) {
            log(e);
        }
    }

    private void serverLoop(final StreamConnectionNotifier notifier) {
        Thread handler = new Thread() {
            @Override
            public void run() {
                try {
                    while (ServerRun) { // infinite loop to accept connections.

                        log("Waiting for connection...");
                        handleConnection(notifier.acceptAndOpen());
                    }
                } catch (Exception e) {
                    log(e);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SpotifyServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        handler.start();
    }

    private void handleConnection(StreamConnection conn) throws IOException {
        out = conn.openOutputStream();
        in = conn.openInputStream();
        startReadThread(in, out);
        log("Connection found...");
    }
    
    private boolean muted = false;


    private void startReadThread(final InputStream in, final OutputStream out) {

        Thread reader = new Thread() {
            @Override
            public void run() {
                boolean flag = true;
                log("Waiting for data");
                byte[] s = new byte[512];

                while (flag) {
                    try {
                        int read = in.read(s);
                        if (read > 0){
                            String h = new String(s).trim();
                            System.out.println(h);                            
                            
                            if(h.equals(NOW_PLAY)){
                                String res = control.SpotifyController.runCommand(h);
                                String salida = NOW_PLAY + " " + res.trim();                                
                                out.write(salida.getBytes());
                                out.flush();   
                                System.out.print(salida);
                            }else if (h.equals(MUTE)){                                
                                if(muted){
                                    control.SpotifyController.runCommand(h + " True");
                                }else{
                                    control.SpotifyController.runCommand(h + " False");
                                }
                                muted = !muted;
                            }else{
                                control.SpotifyController.runCommand(h);
                            }
                            flag = false;
                        }
                        

                    } catch (Throwable e) {
                        log(e);
                    } finally {                          
                    }
                }
                try {
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(SpotifyServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        reader.start();
    }

    /*-
     *   -------  Utility section -------
     */
    private void log(String msg) {

        System.out.print(msg);
    }

    private void log(Throwable e) {
        log(e.getMessage());

        e.printStackTrace();
    }

    public static void StopServer() {
        ServerRun = false;
    }

    public static void main(String args[]) {
        SpotifyServer ss = new SpotifyServer();
    }
}