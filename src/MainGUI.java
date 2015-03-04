/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import com.apple.eawt.Application;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import static javax.swing.SwingUtilities.isEventDispatchThread;

/**
 * @author Simone
 */
public class MainGUI {

    JFrame frame, frame2;
    JScrollPane pannelloRisultati;
    JButton cercaBottone;
    JTextArea outputArea, inputArea;
    JPanel pannelloTop, pannelloMid, pannelloBot;
    BottoneListner listnerCerca;
    FrameListner listnerFrame;
    JLabel istruzioni;
    AnalizzaSpedizioni analizzaSpedizioni;
    GestioneFileSpedizioni gestioneFileSpedizioni;
    private String path16 = null;
    private String path32 = null;
    private String path100 = null;


    MainGUI() {
        analizzaSpedizioni = new AnalizzaSpedizioni();

        gui();
    }

    private void gui() {


        listnerFrame = new FrameListner(this);
        frame = new JFrame("Tracking online spedizioni SDA");

        final java.util.List<Image> icons = new ArrayList<Image>();
        try {
                        /* MacOS X Dock Image */
            if (System.getProperty("os.name").contains("Mac")) {
                //Application.getApplication().setDockIconImage(ImageIO.read(new File(path100)));
            }
            /*
            Stampa tutte le System Properties
            String[]s = {"file.separator" , "java.class.path" , "java.home" , "java.vendor" , "java.vendor.url" ,  "java.version" ,  "line.separator" ,  "os.arch" ,  "os.name" ,  "os.version" , "path.separator" ,"user.dir" ,"user.home" ,"user.name"};
            for(String p:s)
            System.out.println(p+": "+System.getProperty(p));
            icons.add(ImageIO.read(new File(path16)));
            icons.add(ImageIO.read(new File(path32)));
            */
            icons.add(new ImageIcon(getClass().getResource("/icona32.png")).getImage());
            /*
            per far funzionare le immagini da dentro il JAR ho dovuto settare la cartella img  "Mark Directory as-> Source"
            poi carico l'immagine come Resource, la trasformo in ImageIcon e da questa estraggo l'Image
            */
            // icons.add(ImageIO.read(new File(path100)));
            icons.add(new ImageIcon(getClass().getResource("/icona100.png")).getImage());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        frame.setIconImages(icons);

        //frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/package1/package2/dump.jpg")));
        frame.setResizable(false);
        frame.setBounds(0, 0, 600, 700);
        frame.addWindowListener(listnerFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.addWindowListener(listnerFinestra);

        pannelloTop = new JPanel(true);
        pannelloTop.setLayout(null);
        pannelloTop.setBounds(0, 0, 600, 30);
        //pannelloTop.setBorder(BorderFactory.createLineBorder(Color.yellow, 1));

        pannelloMid = new JPanel(true);
        pannelloMid.setBounds(0, 0, 600, 330);
        pannelloMid.setLayout(null);
        //pannelloMid.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));

        pannelloBot = new JPanel(null);
        pannelloBot.setBounds(0, 310, 600, 360);
        //pannelloBot.setBorder(BorderFactory.createLineBorder(Color.green, 1));

        cercaBottone = new JButton("Cerca");
        cercaBottone.setHorizontalAlignment(JButton.CENTER);
        cercaBottone.setVerticalAlignment(JButton.CENTER);
        cercaBottone.setBounds(247, 290, 100, 20);
        listnerCerca = new BottoneListner(this);
        cercaBottone.addActionListener(listnerCerca);

        istruzioni = new JLabel("Inserisci i codici delle lettere di vettura per le spedizioni da tracciare");
        istruzioni.setBounds(0, 0, 600, 30);
        istruzioni.setVerticalAlignment(JLabel.CENTER);
        istruzioni.setHorizontalAlignment(JLabel.CENTER);

        inputArea = new JTextArea("");
        inputArea.setBounds(7, 40, 580, 240);
        inputArea.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        outputArea = new JTextArea("");
        outputArea.setTabSize(5);
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        ((DefaultCaret) (outputArea.getCaret())).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        pannelloRisultati = new JScrollPane(outputArea);
        pannelloRisultati.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        pannelloRisultati.setBounds(7, 7, 582, 347);
        // pannelloRisultati.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pannelloTop.add(istruzioni, BorderLayout.CENTER);

        pannelloMid.add(inputArea);
        pannelloMid.add(cercaBottone);

        pannelloBot.add(pannelloRisultati);

        frame.add(pannelloBot);
        frame.add(pannelloTop);
        frame.add(pannelloMid);
        gestioneFileSpedizioni = new GestioneFileSpedizioni(inputArea);
        if (gestioneFileSpedizioni.carica())
            bottoneRicercaPremuto();

        frame.setVisible(true);

    }

    private void analizzaInput() {
        analizzaSpedizioni.cercaSpedizioni(this);
    }


    void bottoneRicercaPremuto() {
        //con questo SwingWorker delego ad un processo un backgroud di eseguire le operazioni pesanti e non gravo sull'actionPerformed
        //che ha richiamato questo metodo. Inoltre così riesco a rendere reattiva l'interfaccia e a cambiare il testo del bottone
        if (isEventDispatchThread()) {
            System.out.println("EDT");
        }
        AtomicReference<SwingWorker<Void, Void>> worker = new AtomicReference<SwingWorker<Void, Void>>(new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                // Call complicated code here
                SwingUtilities.invokeLater(new Runnable() {    //uso invokeLater perchè devo modificare la GUI
                    @Override
                    public void run() {
                        cercaBottone.setEnabled(false);
                        cercaBottone.setText("Ricerca...");
                        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                });


                analizzaInput();

                return null;


                // If you want to return something other than null, change
                // the generic type to something other than Void.
                // This method's return value will be available via get() once the
                // operation has completed.
            }

            @Override
            protected void done() {//una volta finito il processo in background ripristina il testo del bottone
                // get() would be available here if you want to use it
                frame.setCursor(null);
                cercaBottone.setEnabled(true);
                cercaBottone.setText("Cerca");
            }
        });


        worker.get().execute();//attivo i processo in backgroud

        /* try {
         Image img=ImageIO.read(getClass().getResource("caricamento2.gif"));
         img=img.getScaledInstance(cercaBottone.getHeight()-2,cercaBottone.getHeight()-2, java.awt.Image.SCALE_SMOOTH);
         JLabel imgLabel=new JLabel(new ImageIcon(img));
         pannelloMid.add(imgLabel);
         imgLabel.setBounds(cercaBottone.getX()+(cercaBottone.getWidth()-18)/2, cercaBottone.getY(), cercaBottone.getHeight(), cercaBottone.getHeight());
         } catch (IOException ex) {
         Logger.getLogger(checker.class.getName()).log(Level.SEVERE, null, ex);
         }
         ImageIcon icon=new ImageIcon("caricamento.gif");
         Image tmpIcon=icon.getImage();
         tmpIcon=tmpIcon.getScaledInstance(cercaBottone.getWidth()-2,cercaBottone.getHeight()-2, java.awt.Image.SCALE_SMOOTH);
         cercaBottone.setIcon(new ImageIcon(tmpIcon));*/
        //frame.revalidate();
    }
}
