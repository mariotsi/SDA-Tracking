import javax.swing.*;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Simone
 * Date: 15/03/13
 * Time: 15.14
 * To change this template use File | Settings | File Templates.
 */
public class GestioneFileSpedizioni {
    private File backup;
    private JTextArea datiSpedizione;
    private BufferedWriter out;

    final String filepath = System.getProperty("user.home");
    final String filename = "backupDatiSpedizione.txt";

    public GestioneFileSpedizioni(JTextArea inputArea) {
        datiSpedizione = inputArea;
        File dir = new File(filepath);

        if (!dir.exists())
            dir.mkdir();

        backup = new File(filepath, filename);
    }


    public void salva() {
        try {
            FileWriter fw = new FileWriter(backup, false);
            out = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            out.write(datiSpedizione.getText());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public boolean carica() {
        boolean caricato = false;

        if (backup.isFile() && backup.canRead()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(backup));
                String dati;
                while ((dati = in.readLine()) != null) {
                    datiSpedizione.append(dati+"\n");
                    caricato = true;
                }
            } catch (FileNotFoundException e) {
                System.out.println("Non trovo " + filepath + System.getProperty("file.separator") + filename);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return caricato;
    }
}
