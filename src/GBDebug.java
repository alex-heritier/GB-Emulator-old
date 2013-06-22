
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alex
 */
public class GBDebug {

    private static final boolean debug = false;
    private static final String errorFilename = "error_log.txt";
    private static final File errorFile = new File(errorFilename);

    public static void log(String msg) {
        if (debug) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            String date = dateFormat.format(new Date());
            String errorMessage = date + " - " + msg + "\n";
            FileWriter out;
            System.err.print(errorMessage);
            try {
                out = new FileWriter(errorFile, true);
                out.write(errorMessage);
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(GBDebug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
