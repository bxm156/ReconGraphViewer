package edu.cwru.nashua.pathwaysservice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;

import binevi.Resources.PathCaseResources.PathCaseRepository;
import binevi.Resources.PathCaseResources.TableQueries;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class ExportSBML {
	
	 public static void outputSBML(PathCaseRepository repository) throws ParserConfigurationException, TransformerException {
    	 DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
         Document doc = docBuilder.newDocument();
         
         //<sbml xmlns="http://www.sbml.org/sbml/level2" level="2" version="1">
         Element sbml = doc.createElement("sbml");
         sbml.setAttribute("xmlns","http://www.sbml.org/sbml/level2");
         sbml.setAttribute("level", "2");
         sbml.setAttribute("version", "1");
         doc.appendChild(sbml);
         
         //Model
         Element model = doc.createElement("model");
         model.setAttribute("id", "iAM303");
         sbml.appendChild(model);
         
         //List of Compartments
         Element listOfCompartments = doc.createElement("listOfCompartments");
         model.appendChild(listOfCompartments);
         ArrayList<String> cIds = TableQueries.getCompartmentIDListInRepository(repository);
         for (String cid : cIds) {
             String name = TableQueries.getCompartmnetNamebyCompartmentID(repository, cid);
             String outside = TableQueries.getCompartmnetOutsidebyCompartmentID(repository, cid);
        	 Element compartment = doc.createElement("compartment");
        	 compartment.setAttribute("id", cid);
        	 compartment.setAttribute("name", name);
        	 compartment.setAttribute("outside", outside);
        	 listOfCompartments.appendChild(compartment);
         }
         
         //List of Species
         Element listOfSpecies = doc.createElement("listOfSpecies");
         model.appendChild(listOfSpecies);
         for (String cid : cIds) {
        	 ArrayList<String> sIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository, cid);
        	 for (String sid : sIds) {
        		 String name = TableQueries.getSpeciesLabelBySpeciesId(repository, sid);
        		 Element species = doc.createElement("species");
        		 species.setAttribute("id", sid);
        		 species.setAttribute("name", name);
        		 species.setAttribute("compartment", cid);
        		 listOfSpecies.appendChild(species);
        	 }
         }
       
         /////////////////
         //Output the XML

         //set up a transformer
         TransformerFactory transfac = TransformerFactory.newInstance();
         Transformer trans = transfac.newTransformer();
         trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         trans.setOutputProperty(OutputKeys.INDENT, "yes");

         //create string from xml tree
         StringWriter sw = new StringWriter();
         StreamResult result = new StreamResult(sw);
         DOMSource source = new DOMSource(doc);
         trans.transform(source, result);
         String xmlString = sw.toString();
         System.out.println(xmlString);
    }
	 
}
