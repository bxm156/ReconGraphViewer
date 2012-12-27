package binevi.View;

import binevi.Resources.PathCaseResources.GenomeTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;


public class GeneViewerPanel extends JPanel {

    JComboBox organismList;
    JScrollPane geneViewerScroller;
    GeneEukaryoticSimpleDrawingPanel drawingPanel;
    GeneDetailsViewerPanel detailsViewer;
    private boolean organismListActionsEnabled;
    HashMap<String, String> organismSimplifiedNameToId;
    private boolean bCompartmentH=false;
    PathCaseViewer observer;
    PathCaseViewerMetabolomics observerCompartmentH;

    public GeneViewerPanel(PathCaseViewer observer) {
        this.observer = observer;
        initComponents();
        detailsViewer.setRedirectionData(observer.applet);
    }

    public GeneViewerPanel(PathCaseViewerMetabolomics observer) {
        this.observerCompartmentH = observer;
        this.bCompartmentH=true;
        initComponents();
        detailsViewer.setRedirectionData(observer.applet);
    }

    private void initComponents() {
        organismList = new JComboBox();
        drawingPanel = new GeneEukaryoticSimpleDrawingPanel(this);
        geneViewerScroller = new JScrollPane(drawingPanel);
        drawingPanel.setScroller(geneViewerScroller);
        detailsViewer = new GeneDetailsViewerPanel();

        organismListActionsEnabled = false;

        this.setLayout(new BorderLayout());
        organismList.setPreferredSize(new Dimension(150, 20));
        this.add(organismList, BorderLayout.NORTH);

        organismList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawingPanel.setChromosomes(null);
                drawingPanel.setGenes(null);

                String selectedName = (String) organismList.getSelectedItem();
                if (selectedName != null && organismListActionsEnabled) {
                    //System.out.println("Item state changed");
                    if (organismSimplifiedNameToId != null) {
                        String selectedOrganismId = organismSimplifiedNameToId.get((String)organismList.getSelectedItem());
                        if (selectedOrganismId != null) {
                            //below three lines are used to synchronize list with organism browser
                            //HashSet<String> singleorganismset = new HashSet<String>();
                            //singleorganismset.add(selectedOrganismId);
                            //observer.organismSelected(singleorganismset);
                            if(bCompartmentH){
                                if (observerCompartmentH.repository != null) {
                                    Integer chromosomecount = observerCompartmentH.repository.genomeTable.OrganismIdTOChromosomeCount.get(selectedOrganismId);
                                    if (chromosomecount == null) {
                                        //repository do not contain gene info
                                        observerCompartmentH.loadGenomeDataForOrganism(selectedOrganismId);
                                        chromosomecount = observerCompartmentH.repository.genomeTable.OrganismIdTOChromosomeCount.get(selectedOrganismId);
                                    }
                                    if (chromosomecount != null) {

                                        HashMap<Integer, GenomeTable.ChromosomeEntry> chromosomes = new HashMap<Integer, GenomeTable.ChromosomeEntry>();
                                        ArrayList<GenomeTable.GeneEntry> genes = new ArrayList<GenomeTable.GeneEntry>();

                                        ArrayList<Integer> chromosomeIds = observerCompartmentH.repository.genomeTable.OrganismIdTOChromosomeId.get(selectedOrganismId);
                                        for (int chromid : chromosomeIds) {
                                            GenomeTable.ChromosomeEntry chromosome = observerCompartmentH.repository.genomeTable.ChromosomeIdTOChromosomeEntry.get(chromid);
                                            chromosomes.put(chromid, chromosome);
                                            ArrayList<Integer> geneIds = observerCompartmentH.repository.genomeTable.ChromosomeIdTOCGeneIdList.get(chromid);
                                            if (geneIds != null)
                                                for (int geneid : geneIds) {
                                                    GenomeTable.GeneEntry gene = observerCompartmentH.repository.genomeTable.GeneIdTOGeneEntry.get(geneid);
                                                    genes.add(gene);
                                                }
                                        }

                                        drawingPanel.setChromosomes(chromosomes);
                                        drawingPanel.setGenes(genes);
                                    }
                                }
                            }else {
                                if (observer.repository != null) {
                                    Integer chromosomecount = observer.repository.genomeTable.OrganismIdTOChromosomeCount.get(selectedOrganismId);
                                    if (chromosomecount == null) {
                                        //repository do not contain gene info
                                        observer.loadGenomeDataForOrganism(selectedOrganismId);
                                        chromosomecount = observer.repository.genomeTable.OrganismIdTOChromosomeCount.get(selectedOrganismId);
                                    }
                                    if (chromosomecount != null) {

                                        HashMap<Integer, GenomeTable.ChromosomeEntry> chromosomes = new HashMap<Integer, GenomeTable.ChromosomeEntry>();
                                        ArrayList<GenomeTable.GeneEntry> genes = new ArrayList<GenomeTable.GeneEntry>();

                                        ArrayList<Integer> chromosomeIds = observer.repository.genomeTable.OrganismIdTOChromosomeId.get(selectedOrganismId);
                                        for (int chromid : chromosomeIds) {
                                            GenomeTable.ChromosomeEntry chromosome = observer.repository.genomeTable.ChromosomeIdTOChromosomeEntry.get(chromid);
                                            chromosomes.put(chromid, chromosome);
                                            ArrayList<Integer> geneIds = observer.repository.genomeTable.ChromosomeIdTOCGeneIdList.get(chromid);
                                            if (geneIds != null)
                                                for (int geneid : geneIds) {
                                                    GenomeTable.GeneEntry gene = observer.repository.genomeTable.GeneIdTOGeneEntry.get(geneid);
                                                    genes.add(gene);
                                                }
                                        }

                                        drawingPanel.setChromosomes(chromosomes);
                                        drawingPanel.setGenes(genes);
                                    }
                                }
                            }
                        }
                        drawingPanel.setSelectedOrganism(selectedOrganismId);


                    }
                    //genomeDrawingPanel.genomeViewingIndex = organismList.getSelectedIndex();
                    //genomeDrawingPanel.graphSynchronize();
                }
            }
        });

        //detailsViewer.setPreferredSize(new Dimension(150, 100));
        detailsViewer.setMaximumSize(new Dimension(150, 100));


        JSplitPane detailsSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, geneViewerScroller, detailsViewer);
        detailsSplit.setDividerSize(5);
        //System.out.println(this.getPreferredSize().height+","+geneViewerScroller.getPreferredSize().height+","+detailsSplit.getPreferredSize().height);
        detailsSplit.setDividerLocation(600 - 150/*detailsViewer.getMaximumSize().height*/);

        this.add(detailsSplit, BorderLayout.CENTER);
        //this.add(geneViewerScroller, BorderLayout.CENTER);
        //this.add(detailsViewer, BorderLayout.SOUTH);

    }    

    public void updateOrganismNameToIdMap(HashMap<String, String> organismSimplifiedNameToId) {

        this.organismSimplifiedNameToId = organismSimplifiedNameToId;
        organismListActionsEnabled = false;
        organismList.removeAllItems();
        if (organismSimplifiedNameToId == null) return;
        ArrayList<String> organismnames = new ArrayList<String>(organismSimplifiedNameToId.keySet());
        Collections.sort(organismnames);


        for (String orgname : organismnames) {
            this.organismList.addItem(orgname);
        }
        organismList.setSelectedIndex(-1);
        drawingPanel.setChromosomes(null);
        drawingPanel.setGenes(null);
        organismListActionsEnabled = true;

        if (organismList.getItemCount()>0)
            organismList.setSelectedIndex(0);    
    }

    public void geneSelected(HashSet<GenomeTable.GeneEntry> selectedGenes, GenomeTable.ChromosomeEntry selectedChromosome) {


        //String tooltiptext = "<center><font size=-1><a href = \"" + geneviewerurl + "\">How are these obtained?</a><br></font></center>";
        String tooltiptext = "";
        if (selectedChromosome == null && selectedGenes != null) {
            //Distinct elimination
            HashSet<String> genenamestable = new HashSet<String>();
            for (GenomeTable.GeneEntry selectedGene : selectedGenes) {
                if (!genenamestable.contains(selectedGene.name + selectedGene.genelocation)) {
                    //tooltiptext += "<br><b>Gene</b>: <font size=-1 color=\"red\">" + selectedGene.name + "</font>";
                    String directiondetailslink;
                    if(this.bCompartmentH)
                        directiondetailslink = observerCompartmentH.getRedirectionString(selectedGene.geneId, "MolecularEntity", "000", false, false);
                    else
                        directiondetailslink = observer.getRedirectionString(selectedGene.geneId, "MolecularEntity", "000", false, false);
                    tooltiptext += "<b>Gene:</b> <font size=-1 color=\"red\"><a href = " + directiondetailslink + ">" + selectedGene.name + "</a></font>";

                    tooltiptext += "<br>&nbsp;&nbsp; <font size=-1>located at position: <i>" + selectedGene.genelocation + "</i></font>";
                    tooltiptext += "<br>";
                    genenamestable.add(selectedGene.name + selectedGene.genelocation);
                }
            }
        } else if (selectedChromosome != null) {
            tooltiptext += "<font size=+1><b>Chromosome:</b> <font color=\"blue\">" + selectedChromosome.ChromosomeName + "</font></font>";
            tooltiptext += "<br>&nbsp;&nbsp; <font size=-1>length: <i>" + selectedChromosome.length + "</i></font>";
            if (selectedChromosome.centromerlocation > 0)
                tooltiptext += "<br>&nbsp;&nbsp; <font size=-1>centromer location: <i>" + selectedChromosome.centromerlocation + "</i></font>"; //", %"+((float)((int)(10000*(float)selectedChromosome.centromerlocation/(float)selectedChromosome.length)))/100f+" of the chromosome";
            else
                tooltiptext += "<br>&nbsp;&nbsp; <font size=-1>single arm chromosome</font>";

            //Distinct elimination
            HashSet<String> genenamestable = new HashSet<String>();
            for (GenomeTable.GeneEntry selectedGene : selectedGenes) {
                if (!genenamestable.contains(selectedGene.name + selectedGene.genelocation)) {
                    String directiondetailslink;
                    if(this.bCompartmentH)
                        directiondetailslink = observerCompartmentH.getRedirectionString(selectedGene.geneId, "MolecularEntity", "000", false, false);
                    else
                        directiondetailslink = observer.getRedirectionString(selectedGene.geneId, "MolecularEntity", "000", false, false);
                    tooltiptext += "<b>Gene:</b> <font size=-1 color=\"red\"><a href = " + directiondetailslink + ">" + selectedGene.name + "</a></font>";
                   // tooltiptext += "<br><b>Gene</b>: <font size=-1 color=\"red\">" + selectedGene.name + "</font>";

                    tooltiptext += "<br>&nbsp;&nbsp; <font size=-1>located at position: <i>" + selectedGene.genelocation + "</i></font>"; //", %"+((float)((int)(10000*(float)gene.location/(float)selectedChromosome.length)))/100f+" of the chromosome";
                    genenamestable.add(selectedGene.name + selectedGene.genelocation);
                }

            }
        } else {
            tooltiptext = "<center><font size=-1>Click on a chromosome or gene</font></center>";
        }

        this.detailsViewer.setText(tooltiptext);
        if(this.bCompartmentH)
            this.observerCompartmentH.geneSelectProcessesandCollapsedPathways(selectedGenes);
        else
            this.observer.geneSelectProcessesandCollapsedPathways(selectedGenes);
    }

    public void highlightGenesForGenericProcess(String dbid) {
        //TODO: warn user to open the gene viewer
        drawingPanel.highlightGenesForGenericProcess(dbid);
        repaint();
    }

    public void resetView() {
        initComponents();
    }
}
