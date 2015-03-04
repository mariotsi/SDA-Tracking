import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import static javax.swing.SwingUtilities.isEventDispatchThread;

/**
 * Created with IntelliJ IDEA.
 * User: Simone
 * Date: 15/03/13
 * Time: 16.24
 * To change this template use File | Settings | File Templates.
 */
public class FrameListner implements WindowListener {
    MainGUI mainGUI;

    FrameListner(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowClosing(WindowEvent e) {
        mainGUI.gestioneFileSpedizioni.salva();
    }

    @Override
    public void windowClosed(WindowEvent e) {

        mainGUI.frame.dispose();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
