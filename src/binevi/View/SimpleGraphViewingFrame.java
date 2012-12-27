package binevi.View;

import javax.swing.*;

public class SimpleGraphViewingFrame extends JFrame {

    public static void main(String args[]) {
        SimpleGraphViewingFrame frame = null;
        if (args.length == 1) {
            frame = new SimpleGraphViewingFrame(args[0]);
        } else {
            frame = new SimpleGraphViewingFrame();
        }
    }

    SimpleGraphViewingPanel genericGraphViewer;

    public SimpleGraphViewingFrame() {
        genericGraphViewer = new SimpleGraphViewingPanel();
        getRootPane().setContentPane(genericGraphViewer.contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setBounds(10, 10, 1024, 768);
    }

    public SimpleGraphViewingFrame(String arg) {
        this();
        //System.out.println(arg);
        genericGraphViewer.autoLoadGraph(arg);
    }


}
