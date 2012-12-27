package binevi.View;

import binevi.IO.SXGIOHandler;
import y.util.D;

import java.io.IOException;

public class SimpleGraphViewingPanel extends GraphViewingPanel {


    public SimpleGraphViewingPanel() {
        super();
        killLoaderDialog();
    }

    public void autoLoadGraph(String filename) {

        if (!filename.endsWith(".sxg")) return;

        SXGIOHandler ioh = new SXGIOHandler();
        try {
            view.getGraph2D().clear();
            ioh.read(view.getGraph2D(), filename);

        } catch (IOException ioe) {
            D.show(ioe);
        }

        //force redisplay of view contents
        initialLayout();
        view.fitContent();
        view.getGraph2D().updateViews();
    }
}
