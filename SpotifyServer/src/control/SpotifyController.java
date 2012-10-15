package control;

import java.awt.List;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import spotifyserver.SpotifyServer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rodrigo
 */
public class SpotifyController {

    public static String runCommand(String cmd) {
        ArrayList<String> args = new ArrayList<String>();
        args.add("SpotifySend.exe");
        String[] split = cmd.split(" ");
        args.addAll(Arrays.asList(split));
        
        ProcessBuilder pb = new ProcessBuilder(args);
        String res = "";
        try {
            Process p = pb.start();
            InputStream in = p.getInputStream();

            byte[] s = new byte[512];
            int read = in.read(s);
            if (read > 0) {
                res = new String(s);
                System.out.println(res);
            }
        } catch (IOException ex) {
            Logger.getLogger(SpotifyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    public static void main(String args[]) {
        SpotifyController sc = new SpotifyController();
        sc.runCommand("NowPlaying");
    }
}
