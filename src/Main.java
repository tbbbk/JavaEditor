import application.FileWindow;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        FileWindow tbk = new FileWindow();
        tbk.pack();
        tbk.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        tbk.setBounds(200, 180, 550, 360);
        tbk.setVisible(true);
    }
}