/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spotify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.*;

/**
 * @author Rodrigo
 */
public class Midlet extends MIDlet implements DiscoveryListener, CommandListener {

    /*-
     * 
     *  ---- Debug attributes ----
     */
    static final boolean DEBUG = false;
    static final String DEBUG_address = "0013FDC157C8"; // N6630
    static final String PLAY_PAUSE = "PlayPause";
    static final String PLAY_NEXT = "PlayNext";
    static final String PLAY_PREV = "PlayPrev";
    static final String VOL_DOWN = "VolumeDown";
    static final String VOL_UP = "VolumeUp";
    static final String MUTE = "Mute";
    static final String BUSCAR = "Search";
    static final String NOW_PLAY = "NowPlaying";
    /*-
     * 
     *  ---- Bluetooth attributes ----
     */
    protected UUID uuid = new UUID(0x1101); // serial port profile
    protected int inquiryMode = DiscoveryAgent.GIAC; // no pairing is needed
    protected int connectionOptions = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;

    /*-
     * 
     *  ---- Echo loop attributes ----
     */
    protected int stopToken = 255;

    /*-
     * 
     *  ---- GUI attributes ----
     */
    protected Form infoArea = new Form("Spotify Remote Control");
    protected Gauge gau = new Gauge("", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
    protected TextBox busqueda = new TextBox("Buscar y Reproducir", "", 50, TextField.ANY);
    protected Vector deviceList = new Vector();
    Image logo, controls;

    protected void startApp() throws MIDletStateChangeException {
        try {
            logo = Image.createImage("/spotify.png");
            controls = Image.createImage("/controls.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        makeInformationAreaGUI();

        if (DEBUG) // skip inquiry in debug mode
        {
            startServiceSearch(new RemoteDevice(DEBUG_address) {
            });
        } else {
            try {
                startDeviceInquiry();
            } catch (Throwable t) {
                log(t);
            }
        }

    }

    /*-
     *   -------  Device inquiry section -------
     */
    private void startDeviceInquiry() {
        try {
            logSet("Bienvenido!");
            gau.setLabel("Buscando Equipos BT...");
            infoArea.append(gau);
            DiscoveryAgent agent = getAgent();
            agent.startInquiry(inquiryMode, this);

        } catch (Exception e) {
            log(e);
        }
    }

    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        deviceList.addElement(btDevice);
    }

    public void inquiryCompleted(int discType) {
        makeDeviceSelectionGUI();
    }

    /*-
     *   -------  Service search section -------
     */
    private void startServiceSearch(RemoteDevice device) {
        try {
            logSet("");

//            gau.setLabel("Intentando conectar con " + getFriendlyName(device) + "...");
//            infoArea.append(gau);
//            startGauge(gau);

            UUID uuids[] = new UUID[]{uuid};
            getAgent().searchServices(null, uuids, device, this);
        } catch (Exception e) {
            log(e);
        }
    }
    Vector urls = new Vector();

    /**
     * This method is called when a service(s) are discovered.This method starts
     * a thread that handles the data exchange with the server.
     */
    public void servicesDiscovered(int transId, ServiceRecord[] records) {
        for (int i = 0; i < records.length; i++) {
            ServiceRecord rec = records[i];
            String url = rec.getConnectionURL(connectionOptions, false);
            log(url);
            urls.addElement(url);
        }
    }

    public void serviceSearchCompleted(int transID, int respCode) {
        String msg;
        switch (respCode) {
            case SERVICE_SEARCH_COMPLETED:
                msg = "La conexión se completó con éxito! Cargando info...";
                makeMainGUI();
                break;
            case SERVICE_SEARCH_TERMINATED:
                msg = "La búsqueda fue cancelada - DiscoveryAgent.cancelServiceSearch()";
                break;
            case SERVICE_SEARCH_ERROR:
                msg = "Se produjo un error mientras se intentaba la conexión.";
                break;
            case SERVICE_SEARCH_NO_RECORDS:
                msg = "No se encontró spotify en la computadora seleccionada.";
                break;
            case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                msg = "No pudo alcanzarse el dispositivo. Se perdió cobertura o está detrás de un firewall?";
                break;
            default:
                msg = "Se produjo un error al intentar conectar.";
        }
        logSet(msg);

    }

    /*-
     *   -------  The actual connection handling. -------
     */
    private void handleConnection(final String url, final String cmd) {
        Thread echo = new Thread() {
            public void run() {
                StreamConnection stream = null;
                InputStream in = null;
                OutputStream out = null;

                try {
                    stream = (StreamConnection) Connector.open(url);
                    out = stream.openOutputStream();
                    if (cmd.equals(NOW_PLAY)) {
                        in = stream.openInputStream();
                        startReadThread(in);
                    }

                    out.write(cmd.getBytes());
                    out.flush();
                } catch (IOException e) {
                    log(e);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                            stream.close();
                        } catch (IOException e) {
                            log(e);
                        }
                    }
                }
            }
        };
        echo.start();
    }

    private void startReadThread(final InputStream in) {
        Thread reader = new Thread() {
            public void run() {
                boolean flag = true;
                byte[] s = new byte[512];
                while (flag) {
                    try {
                        int read = in.read(s);
                        if (read > 0) {
                            String h = new String(s);
                            if (h.startsWith(NOW_PLAY)) {
                                nowPlaying = h.substring(NOW_PLAY.length());
                                main.repaint();
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
                } catch (IOException ex) {
                }
            }
        };
        reader.start();
    }

    public void send(String cmd) {
        for (int i = 0; i < urls.size(); i++) {
            String url = (String) urls.elementAt(i);
            handleConnection(url, cmd);
        }
    }

    /*-
     *   -------  Graphic User Interface section -------
     */
    private void makeInformationAreaGUI() {
        infoArea.deleteAll();
        infoArea.addCommand(new Command("Salir", Command.EXIT, 3));
        infoArea.setCommandListener(this);
        Display.getDisplay(this).setCurrent(infoArea);

    }

    private void makeDeviceSelectionGUI() {
        final List devices = new List("Elija un dispositivo", List.IMPLICIT);
        for (int i = 0; i < deviceList.size(); i++) {
            devices.append(getDeviceStr((RemoteDevice) deviceList.elementAt(i)), null);
        }
        devices.setCommandListener(new CommandListener() {
            public void commandAction(Command arg0,
                    Displayable arg1) {
                makeInformationAreaGUI();
                startServiceSearch((RemoteDevice) deviceList.elementAt(devices.getSelectedIndex()));
            }
        });
        Display.getDisplay(this).setCurrent(devices);
    }
    String ultima = "";
    String nowPlaying = "";
    protected Canvas main = new Canvas() {
        protected void paint(Graphics g) {
            int width = getWidth();
            int height = getHeight();

            g.setColor(71, 71, 71);
            g.fillRect(0, 0, width, height);
            g.setColor(255, 255, 255);

            g.drawImage(logo, width / 2, height / 2, g.BOTTOM | g.HCENTER);
            g.drawImage(controls, width / 2, height - 20, g.BOTTOM | g.HCENTER);
            g.setColor(209, 209, 209);
            int indexOf = nowPlaying.indexOf(" - ");
            if (!nowPlaying.equals("")) {
                String artist = nowPlaying.substring(0, indexOf);
                String song = nowPlaying.substring(indexOf + 2);
                g.drawString(artist, 0, 0, g.TOP | g.LEFT);
                g.drawString(song, 0, 30, g.TOP | g.LEFT);
            }
        }

        protected void keyPressed(int keyCode) {
            
        }

        protected void keyReleased(int keyCode) {
            int ga = getGameAction(keyCode);

            switch (ga) {
                case UP:
                    send(VOL_UP);
                    ultima = VOL_UP;
                    break;
                case DOWN:
                    send(VOL_DOWN);
                    ultima = VOL_DOWN;
                    break;
                case RIGHT:
                    send(PLAY_NEXT);
                    ultima = PLAY_NEXT;
                    break;
                case LEFT:
                    send(PLAY_PREV);
                    ultima = PLAY_PREV;
                    break;
                case FIRE:
                case GAME_A:
                    send(PLAY_PAUSE);
                    ultima = PLAY_PAUSE;
                    break;
                default:
                    switch (keyCode) {
                        case KEY_POUND:
                            send(MUTE);
                            break;
                        case KEY_STAR:
                            makeBuscarGUI();
                            break;
                        case KEY_NUM1:
                            send(NOW_PLAY);
                            break;
                    }

            }
            main.repaint();
        }
    };

    private void makeMainGUI() {
        main.setFullScreenMode(true);
        Display.getDisplay(this).setCurrent(main);
    }

    private void makeBuscarGUI() {
        busqueda.addCommand(new Command("OK", Command.OK, 1));
        busqueda.addCommand(new Command("Atrás", Command.BACK, 2));
        busqueda.setCommandListener(this);
        Display.getDisplay(this).setCurrent(busqueda);

    }

    public void commandAction(Command com, Displayable dis) {
        String label = com.getLabel();

        if ("Salir".equals(label)) {
            notifyDestroyed();
        } else if ("Atrás".equals(label)) { //en buscar
            makeMainGUI();
        } else if ("OK".equals(label)) {//confirmar busqueda           
            send(BUSCAR + " \"" + busqueda.getString() + "\"");
            makeMainGUI();
        }
    }

    synchronized private void log(String msg) {
        infoArea.append(msg);
        infoArea.append("\n\n");
    }

    synchronized private void logSet(String msg) {
        infoArea.deleteAll();
        infoArea.append(msg);
        infoArea.append("\n\n");

    }

    private void log(Throwable e) {
        log(e.getMessage());
    }

    /*-
     *   -------  Utils section - contains utility functions -------
     */
    private DiscoveryAgent getAgent() {
        try {
            return LocalDevice.getLocalDevice().getDiscoveryAgent();
        } catch (BluetoothStateException e) {
            throw new Error(e.getMessage());
        }
    }

    private String getDeviceStr(RemoteDevice btDevice) {
        return getFriendlyName(btDevice);
    }

    private String getFriendlyName(RemoteDevice btDevice) {
        try {
            return btDevice.getFriendlyName(false);
        } catch (IOException e) {
            return "no name available";
        }
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void pauseApp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
