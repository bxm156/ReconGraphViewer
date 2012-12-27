package binevi.Parser.PathCaseParsers;

import y.view.Graph2D;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

import binevi.Resources.PathCaseResources.PathCaseRepository;
import binevi.View.PathCaseViewGenerator;


public class BioPAXParser {


    //private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    PathCaseRepository repository;
    Graph2D graph2D;

    public BioPAXParser(PathCaseRepository repository, Graph2D graph2D) {
        this.repository = repository;
        this.graph2D = graph2D;
    }

    // GraphData

    public HashMap<y.base.Node, HashSet<String>> loadGraphFromBioPAXString(String graphXML) {
        StringWriter outputWriter = new StringWriter();
        try {       
            
            graphXML = graphXML.replaceFirst("biopax-level2","biopax-level1");
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer (new javax.xml.transform.stream.StreamSource (new InputStreamReader(getClass().getResourceAsStream("/binevi/Resources/PathCaseBioPAX/Biopax.xsl"))));
            transformer.transform  (new javax.xml.transform.stream.StreamSource (new StringReader(graphXML)), new javax.xml.transform.stream.StreamResult  (outputWriter));
            String outputDocument = outputWriter.toString();
            PathCaseXMLParser pathCaseParser = new PathCaseXMLParser(repository);            
            pathCaseParser.loadRepositoryFromBioPAXGraphXML(outputDocument);            
            return PathCaseViewGenerator.createGraphFromWholeRepository(repository,graph2D,false,true,false);

        } catch (Exception e) {
            //Note that no effort was made to provide
            // meaningful results in the event of an
            // exception or error.
            ///
            e.printStackTrace(System.err);
            return new HashMap<y.base.Node, HashSet<String>>();
        }//end catch

    }

}
