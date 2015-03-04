/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.SwingUtilities.isEventDispatchThread;

/**
 * @author Simone
 */
public class BottoneListner implements ActionListener {

    MainGUI mainGUI;

    BottoneListner(MainGUI main) {
        this.mainGUI = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().toString().equals("Cerca")) {
            mainGUI.outputArea.setText("");
            //  mainGUI.bottoneRicercaPremuto();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mainGUI.bottoneRicercaPremuto();
                }
            });


        }
    }
}
