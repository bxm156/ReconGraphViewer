package binevi.View;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.StringReader;

public class GeneDetailsViewerPanel extends JPanel {

    JScrollPane textSlider;
    private JTextPane textArea;

    static SimpleAttributeSet ITALIC_GRAY = new SimpleAttributeSet();
    static SimpleAttributeSet BOLD_BLACK = new SimpleAttributeSet();
    static SimpleAttributeSet BLACK = new SimpleAttributeSet();

    // Best to reuse attribute sets as much as possible.
    static {
        StyleConstants.setForeground(ITALIC_GRAY, Color.gray);
        StyleConstants.setItalic(ITALIC_GRAY, true);
        StyleConstants.setFontFamily(ITALIC_GRAY, "Helvetica");
        StyleConstants.setFontSize(ITALIC_GRAY, 14);

        StyleConstants.setForeground(BOLD_BLACK, Color.black);
        StyleConstants.setBold(BOLD_BLACK, true);
        StyleConstants.setFontFamily(BOLD_BLACK, "Helvetica");
        StyleConstants.setFontSize(BOLD_BLACK, 14);

        StyleConstants.setForeground(BLACK, Color.black);
        StyleConstants.setFontFamily(BLACK, "Helvetica");
        StyleConstants.setFontSize(BLACK, 14);
    }


    public GeneDetailsViewerPanel() {


        textArea = new JTextPane();
        textArea.setEditable(false);
        textSlider = new JScrollPane(textArea);
        setLayout(new BorderLayout());
        add(textSlider, BorderLayout.CENTER);

        textArea.setContentType("text/html");

        textArea.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (redirectionApplet != null)
                    {
                        try {
                            //JSObject.getWindow(applet).eval("winopen(\"" + applet.getConfiguration().get("geneViewerHelpURL") +"\")");
                            redirectionApplet.getAppletContext().showDocument(event.getURL());
                        } catch (Exception ioe) {
                            ioe.printStackTrace();
                        }
                        System.out.println("redirects to the link " + event.getURL());
                    }
                }

            }
        });

    }

    public void setText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
    }

    protected void insertText(String text, AttributeSet set) {
        try {
            textArea.getDocument().insertString(
                    textArea.getDocument().getLength(), text, set);
        } catch (BadLocationException e) {
            //e.printStackTrace();
        }
    }


    public void insertHTML(String html, int location) {
        //assumes editor is already set to "text/html" type


        try {
            HTMLEditorKit kit = (HTMLEditorKit) textArea.getEditorKit();
            Document doc = textArea.getDocument();
            StringReader reader = new StringReader(html);
            kit.read(reader, doc, location);
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }


    public static void main(String args[]) {
        JFrame frame = new JFrame();
        frame.setBounds(100, 100, 640, 480);
        frame.setLayout(new BorderLayout());
        GeneDetailsViewerPanel details = new GeneDetailsViewerPanel();
        frame.add(details);
        frame.setVisible(true);

        //details.setText("ALI");
        //details.insertText("\nWebsite for: www.java2s.com \n\n", BOLD_BLACK);
        details.insertHTML("<A href=\"http://www.yahoo.com\">yahoo</a>", 0);
    }


    JApplet redirectionApplet;

    public void setRedirectionData(JApplet redirectionApplet) {
        this.redirectionApplet = redirectionApplet;

    }
}

