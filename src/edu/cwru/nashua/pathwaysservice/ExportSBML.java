package edu.cwru.nashua.pathwaysservice;

import java.io.*;
import org.w3c.dom.*;

import binevi.Resources.PathCaseResources.PathCaseRepository;
import binevi.Resources.PathCaseResources.TableQueries;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class ExportSBML {
	
	 public static void outputSBML(PathCaseRepository repository) throws ParserConfigurationException {
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
         TableQueries.getCompartmentIDListInRepository(repository);   
    }
	 
}
