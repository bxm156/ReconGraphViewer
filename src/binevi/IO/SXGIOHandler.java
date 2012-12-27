package binevi.IO;

import y.io.IOHandler;
import y.view.Graph2D;

import java.io.*;

import binevi.Parser.SXGParser;


public class SXGIOHandler extends IOHandler {
    public void write(Graph2D graph2D, OutputStream outputStream) throws IOException {
        System.out.println("Exporting to this format is not supported, yet");
    }

    public void read(Graph2D graph2D, InputStream inputStream) throws IOException {
        StringBuffer xmldocument = new StringBuffer();
        BufferedReader br = new BufferedReader (new InputStreamReader(inputStream)) ;
        String line;
        while ((line=br.readLine())!=null){
            xmldocument.append(line);
        }
        SXGParser parser = new SXGParser(graph2D);
        parser.loadGraphFromSXGFString(xmldocument.toString());
    }

    public String getFileFormatString() {
        return "Simple XML Graph Format";
    }

    public String getFileNameExtension() {
        return "sxg";
    }
}
