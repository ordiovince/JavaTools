package vincent.ordioni.imdb.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;

import javax.swing.JDialog;

import org.apache.commons.net.ftp.FTPClient;

import vincent.ordioni.imdb.parser.IMDBParser;
import vincent.ordioni.imdb.view.MenuFrame;
import vincent.ordioni.imdb.view.RemoveDialog;

public class Controller extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -4949592342805317037L;
    
    private MenuFrame menuFrame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Controller dialog = new Controller();
                    dialog.setVisible(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the dialog.
     */
    public Controller() {
        menuFrame = new MenuFrame();
        menuFrame.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (menuFrame.hasChanged) {
                    try {
                        FTPClient client = new FTPClient();
                        client.connect("ftp.vincent-ordioni.fr");
                        client.login("vincento", "tNd2iLuu");
                        client.changeWorkingDirectory("/www/applications/CheckListWPF");

                        client.storeFile(IMDBParser.SOURCE_PATH, new FileInputStream(IMDBParser.SOURCE_PATH));

                        client.disconnect();
                    } catch (SocketException e1) {
                        e1.printStackTrace();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                System.exit(0);
            }
        });


        menuFrame.removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        menuFrame.isModified = RemoveDialog.display();
                        menuFrame.hasChanged = menuFrame.hasChanged ? menuFrame.hasChanged : menuFrame.isModified;
                    }
                });
                thread.start();
            }
        });
        
        menuFrame.setVisible(true);
    }

}
