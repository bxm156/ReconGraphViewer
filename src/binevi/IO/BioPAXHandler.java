package binevi.IO;

import binevi.Parser.PathCaseParsers.BioPAXParser;
import binevi.Resources.PathCaseResources.PathCaseRepository;
import y.view.Graph2D;
import y.io.IOHandler;
import y.base.Node;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;


public class BioPAXHandler extends IOHandler {


    PathCaseRepository repository;
    public HashMap<Node, HashSet<String>> node2idtable;

    public BioPAXHandler(PathCaseRepository repository) {
        this.repository = repository;
    }

    public void write(Graph2D graph2D, OutputStream outputStream) throws IOException {
        System.out.println("Exporting to this format is not supported, yet");
    }

    public void read(Graph2D graph2D, InputStream inputStream) throws IOException {
        StringBuffer xmldocument = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            xmldocument.append(line);
        }
        BioPAXParser parser = new BioPAXParser(repository,graph2D);
        node2idtable = parser.loadGraphFromBioPAXString(xmldocument.toString());
    }

    public String getFileFormatString() {
        return "Simple XML Graph Format";
    }

    public String getFileNameExtension() {
        return "sxg";
    }

}
