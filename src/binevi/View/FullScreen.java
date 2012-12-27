package binevi.View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FullScreen extends java.applet.Applet {
    private Label l;
    private Window w;
    private boolean running;
    private int clicks;
    private String[] messages = new String[]{
            "Scary, uh?",
            "So you want me to go away…",
            "You know I don’t have to, but…",
            "I’ll be nice, just click me one more time :)"
    };


    public synchronized void start() {
        w = new Window(new Frame());
        l = new Label("PWND");
        l.setFont(new Font("Serif", Font.BOLD, 120));
        l.setAlignment(l.CENTER);
        l.setForeground(Color.white);

        l.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                clicked();
            }
        });
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        w.setBackground(Color.black);
        w.setLayout(new BorderLayout());
        w.add(l, BorderLayout.CENTER);

        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        w.setBounds(0, 0, ss.width, ss.height);

        w.setVisible(true);

        running = true;
        new Thread() {
            public void run() {
                while (isRunning()) {
                    try {
                        EventQueue.invokeAndWait(toFront);
                        sleep(10);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                }
            }
        }.start();

        try {
            w.setAlwaysOnTop(true);
        } catch (Throwable t) {
            // it was just an attempt, we know this should be forbidden to Applets
        }
    }

    private Runnable toFront = new Runnable() {
        public void run() {
            w.toFront();
        }
    };

    private synchronized boolean isRunning() {
        return running;
    }

    private synchronized void clicked() {
        if (clicks >= messages.length) {
            running = false;
            w.dispose();
            return;
        }
        if (clicks == 1) {
            l.setFont(new Font("Serif", Font.BOLD, 40));
        }
        l.setText(messages[clicks++]);
    }
}
