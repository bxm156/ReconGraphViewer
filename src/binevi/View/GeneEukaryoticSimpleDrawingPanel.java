package binevi.View;

import binevi.Resources.PathCaseResources.GenomeTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.*;


public class GeneEukaryoticSimpleDrawingPanel extends JPanel {


    public void setGenes(ArrayList<GenomeTable.GeneEntry> genes) {
        this.genes = genes;

    }


    public void setChromosomes(HashMap<Integer, GenomeTable.ChromosomeEntry> chromosomes) {
        this.chromosomes = chromosomes;
        if (chromosomes != null) {
            orderedchromosomes = new ArrayList<GenomeTable.ChromosomeEntry>(chromosomes.values());
            Collections.sort(orderedchromosomes);
            ChromosomeToOrderId = new HashMap<GenomeTable.ChromosomeEntry, Integer>();
            for (int i = 0; i < orderedchromosomes.size(); i++) {
                GenomeTable.ChromosomeEntry chromosome = orderedchromosomes.get(i);
                ChromosomeToOrderId.put(chromosome, i);
            }
        } else orderedchromosomes = null;
    }

    ArrayList<GenomeTable.ChromosomeEntry> orderedchromosomes;
    HashMap<Integer, GenomeTable.ChromosomeEntry> chromosomes;
    HashMap<GenomeTable.ChromosomeEntry, Integer> ChromosomeToOrderId;
    ArrayList<GenomeTable.GeneEntry> genes;
    HashSet<GenomeTable.GeneEntry> selectedGenes;

    JScrollPane scroller;

    GeneViewerPanel panel;

    private final int headerHeight = 5;
    private final int footerHeight = 5;
    private final int extraBarWidth = 15;
    private final int verticalDistanceBetweenChromosomes = 7;
    private final float chromosomeRoundCornersRatio = 0.20f;
    private final int chromosomeHeight = 13;

    private final float spaceBeforeChromosomeNameRatio = 1f / 50f;
    private final float spaceBeforeChromosomeRatio = 1f / 10f;
    private final float spaceAfterChromosomeRatio = 1f / 20f;
    private final float centromerWidthRatio = 1f / 25f;

    String selectedOrganismId;

    public GeneEukaryoticSimpleDrawingPanel(GeneViewerPanel panel) {
        this.panel = panel;
        initomouselisteners();
    }

    private void initomouselisteners() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
    }

    private void formMousePressed(MouseEvent evt) {
        selectGenesandDisplayDetailsbyMouseLocation(evt.getX(), evt.getY());
        repaint();
    }

    private void selectGenesandDisplayDetailsbyMouseLocation(int x, int y) {

        if (orderedchromosomes == null)
            return;

        float index = (float) (y - headerHeight) / (float) (chromosomeHeight + verticalDistanceBetweenChromosomes);
        int estimatedindex = Math.round(index);
        if (orderedchromosomes.size() - 1 < estimatedindex || estimatedindex < 0) {
            //out of drawing range
            deselectGenesAndUpdateGraph();
            return;
        }

        long longestChromosome = getLongestChromosomeSize();
        ChromosomeDrawing selectedChromosome = getChromosomeDrawing(estimatedindex, longestChromosome, orderedchromosomes.get(estimatedindex));
        if (x < selectedChromosome.longarmrec.getX() || x > selectedChromosome.longarmrec.getX() + selectedChromosome.getChromosomeLength() || y < selectedChromosome.longarmrec.getY() || y > selectedChromosome.longarmrec.getY() + selectedChromosome.longarmrec.getHeight()) {
            //out of chromosome range
            deselectGenesAndUpdateGraph();
            return;
        }

        GenomeTable.GeneEntry selectedgene = null;

        HashSet<GenomeTable.GeneEntry> genesOfSelectedChromosome = new HashSet<GenomeTable.GeneEntry>();

        for (GenomeTable.GeneEntry gene : genes) {
            int chromosomehashid = gene.ChromosomeIndexID;
            GenomeTable.ChromosomeEntry chromosomeofgene = chromosomes.get(chromosomehashid);
            int indexofchromosome = ChromosomeToOrderId.get(chromosomeofgene);
            if (indexofchromosome != estimatedindex) {
                continue;
            }

            genesOfSelectedChromosome.add(gene);

            float geneThickness;
            if (selectedGenes != null && selectedGenes.contains(gene)) {
                geneThickness = 1 / 40f;

            } else {
                geneThickness = 1 / 60f;
            }

            int geneHeight = chromosomeHeight;
            int geneWidth = Math.round(geneThickness * this.getWidth());
            float relativegenelocation = (float) gene.genelocation / (float) chromosomeofgene.length;


            int genetop;
            if (chromosomeofgene.centromerlocation >= 0 && gene.genelocation < chromosomeofgene.centromerlocation) {
                //on the longarm
                genetop = (int) Math.round(selectedChromosome.longarmrec.getX() + selectedChromosome.getRelativeChromosomeLength() * relativegenelocation);
            } else
            if (chromosomeofgene.centromerlocation >= 0 && gene.genelocation > chromosomeofgene.centromerlocation) {
                //on the shortarm
                genetop = (int) Math.round(selectedChromosome.longarmrec.getX() + selectedChromosome.centromer.getWidth() + selectedChromosome.getRelativeChromosomeLength() * relativegenelocation);
            } else
            if (chromosomeofgene.centromerlocation >= 0 && gene.genelocation == chromosomeofgene.centromerlocation) {
                //on the centromer
                genetop = (int) Math.round(selectedChromosome.longarmrec.getX() + selectedChromosome.centromer.getWidth() / 2 + selectedChromosome.getRelativeChromosomeLength() * relativegenelocation);
            } else
                genetop = (int) Math.round(selectedChromosome.longarmrec.getX() + selectedChromosome.getRelativeChromosomeLength() * relativegenelocation);

            if (x >= genetop - geneWidth / 2 && y >= selectedChromosome.longarmrec.getY() && x <= genetop - geneWidth / 2 + geneWidth && y <= selectedChromosome.longarmrec.getY() + geneHeight) {
                selectedgene = gene;
            }
        }


        if (selectedgene != null) {
            selectedGenes = new HashSet<GenomeTable.GeneEntry>();
            selectedGenes.add(selectedgene);
            panel.geneSelected(selectedGenes, null);
        } else {
            selectedGenes = genesOfSelectedChromosome;
            panel.geneSelected(selectedGenes, orderedchromosomes.get(estimatedindex));
        }

    }

    private void deselectGenesAndUpdateGraph() {
        selectedGenes = null;
        panel.geneSelected(selectedGenes, null);
        //this.repaint();
    }

    public void highlightGenesForGenericProcess(String dbid) {
        if (selectedGenes == null)
            selectedGenes = new HashSet<GenomeTable.GeneEntry>();
        else selectedGenes.clear();

        if (orderedchromosomes != null || genes != null)
            for (GenomeTable.GeneEntry entry : genes) {
                if (entry.genericprocessId.equals(dbid)) {
                    selectedGenes.add(entry);
                }
            }

        panel.geneSelected(selectedGenes, null);
        //this.repaint();
    }

    public class ChromosomeDrawing {
        public RoundRectangle2D longarmrec, shortarmrec;

        public Ellipse2D centromer;

        public ChromosomeDrawing(RoundRectangle2D longarmrec, RoundRectangle2D shortarmrec, Ellipse2D centromer) {
            this.longarmrec = longarmrec;
            this.shortarmrec = shortarmrec;
            this.centromer = centromer;
        }

        public double getChromosomeLength() {
            if (shortarmrec.getWidth() > 0) {
                return longarmrec.getWidth() + centromer.getWidth() + shortarmrec.getWidth();
            } else {
                return longarmrec.getWidth();
            }
        }

        public double getRelativeChromosomeLength() {
            return longarmrec.getWidth() + shortarmrec.getWidth();
        }
    }

    public ChromosomeDrawing getChromosomeDrawing(int drawingIndexFromTop, long lengthOfLongestChromosome, GenomeTable.ChromosomeEntry chromosome) {
        RoundRectangle2D longarmrec, shortarmrec;
        Ellipse2D centromer;

        float spaceBeforeChromosome = this.getWidth() * spaceBeforeChromosomeRatio;
        float spaceAfterChromosome = this.getWidth() * spaceAfterChromosomeRatio;
        float centromerWidth = this.getWidth() * centromerWidthRatio;

        float chromosomedrawlength = this.getWidth() - spaceBeforeChromosome - spaceAfterChromosome;
        double lengthratio = (double) chromosome.length / (double) lengthOfLongestChromosome;
        double relativechromosomelength = (chromosomedrawlength - centromerWidth) * lengthratio;
        double centromerratio = (chromosome.centromerlocation > 0) ? (double) chromosome.centromerlocation / (double) lengthOfLongestChromosome : lengthratio;
        double relativelongarmlength = (chromosomedrawlength - centromerWidth) * centromerratio;
        double relativeshortarmlength = relativechromosomelength - relativelongarmlength;

        float longarm_topleftX = Math.round(spaceBeforeChromosome);
        float longarm_topleftY = headerHeight + (chromosomeHeight + verticalDistanceBetweenChromosomes) * drawingIndexFromTop;
        float longarm_width = (float) relativelongarmlength;
        float longarm_height = chromosomeHeight;
        float longarm_roundedwidth = longarm_width * chromosomeRoundCornersRatio;
        float longarm_roundedheight = longarm_height * chromosomeRoundCornersRatio;

        longarmrec = new RoundRectangle2D.Float(longarm_topleftX, longarm_topleftY, longarm_width, longarm_height, longarm_roundedwidth, longarm_roundedheight);

        float shortarm_topleftX = Math.round(longarm_topleftX + longarm_width + centromerWidth);
        float shortarm_topleftY = longarm_topleftY;
        float shortarm_width = (float) relativeshortarmlength;
        float shortarm_height = chromosomeHeight;
        float shortarm_roundedwidth = shortarm_width * chromosomeRoundCornersRatio;
        float shortarm_roundedheight = shortarm_height * chromosomeRoundCornersRatio;

        shortarmrec = new RoundRectangle2D.Float(shortarm_topleftX, shortarm_topleftY, shortarm_width, shortarm_height, shortarm_roundedwidth, shortarm_roundedheight);

        float centromer_topleftX = longarm_topleftX + longarm_width;
        float centromer_topleftY = longarm_topleftY;
        float centromer_width = centromerWidth;
        float centromer_height = chromosomeHeight;
        centromer = new Ellipse2D.Float(centromer_topleftX, centromer_topleftY, centromer_width, centromer_height);

        return new ChromosomeDrawing(longarmrec, shortarmrec, centromer);
    }

    public void paintChromosomeDrawing(Graphics2D g2, int drawingIndexFromTop, long lengthOfLongestChromosome, GenomeTable.ChromosomeEntry chromosome) {
        ChromosomeDrawing chromdrawing = getChromosomeDrawing(drawingIndexFromTop, lengthOfLongestChromosome, chromosome);

        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(Color.black);
        g2.draw(chromdrawing.longarmrec);

        if (chromdrawing.shortarmrec.getWidth() > 0) {
            g2.fill(chromdrawing.centromer);
            g2.draw(chromdrawing.shortarmrec);
        }

        g2.setStroke(new BasicStroke(1.0f));
        GradientPaint gp = new GradientPaint((float) chromdrawing.longarmrec.getX(), (float) chromdrawing.longarmrec.getY(), Color.white, (float) chromdrawing.longarmrec.getX() + (float) chromdrawing.longarmrec.getWidth(), (float) chromdrawing.longarmrec.getY() + (float) chromdrawing.longarmrec.getHeight(), new Color(200, 255, 225));
        g2.setPaint(gp);
        g2.fill(chromdrawing.longarmrec);

        gp = new GradientPaint((float) chromdrawing.shortarmrec.getX(), (float) chromdrawing.shortarmrec.getY(), new Color(200, 255, 225), (float) chromdrawing.shortarmrec.getX() + (float) chromdrawing.shortarmrec.getWidth(), (float) chromdrawing.shortarmrec.getY() + (float) chromdrawing.shortarmrec.getHeight(), Color.white);
        g2.setPaint(gp);
        g2.fill(chromdrawing.shortarmrec);

        g2.setColor(Color.RED);
        float spaceBeforeAfterLabel = spaceBeforeChromosomeNameRatio * this.getWidth();
        float spaceBeforeChromosome = this.getWidth() * spaceBeforeChromosomeRatio;
        int label_topleftX = Math.round(spaceBeforeAfterLabel);
        int label_topleftY = (int) Math.round(chromdrawing.longarmrec.getY());
        int label_width = Math.round(spaceBeforeChromosome - 2 * spaceBeforeAfterLabel);
        int label_height = chromosomeHeight;

        int fontsize = Math.min(label_width, label_height);
        Font textfont = new Font("ARIAL", Font.BOLD, fontsize);
        drawTextInRectangle(chromosome.ChromosomeName, label_topleftX, label_topleftY, label_width, label_height, true, textfont, new Color(0, 32, 0), g2);

    }

    public long getLongestChromosomeSize() {
        long longestsize = 0;
        for (GenomeTable.ChromosomeEntry chromosome : orderedchromosomes) {
            if (chromosome.length > longestsize) {
                longestsize = chromosome.length;
            }
        }
        return longestsize;
    }

    private void paintGeneDrawing(Graphics2D g2, GenomeTable.GeneEntry gene, long longestchromosomeSize) {
        int chromosomehashid = gene.ChromosomeIndexID;
        GenomeTable.ChromosomeEntry chromosomeofgene = chromosomes.get(chromosomehashid);
        int indexofchromosome = ChromosomeToOrderId.get(chromosomeofgene);

        float geneThickness;
        if (selectedGenes != null && selectedGenes.contains(gene)) {
            geneThickness = 1 / 40f;
            g2.setColor(Color.red);
        } else {
            geneThickness = 1 / 60f;
            g2.setColor(Color.blue);
        }

        int geneHeight = chromosomeHeight;
        int geneWidth = Math.round(geneThickness * this.getWidth());
        float relativegenelocation = (float) gene.genelocation / (float) chromosomeofgene.length;

        float spaceBeforeChromosome = this.getWidth() * spaceBeforeChromosomeRatio;
        float spaceAfterChromosome = this.getWidth() * spaceAfterChromosomeRatio;
        float centromerWidth = this.getWidth() * centromerWidthRatio;
        float chromosomedrawlength = this.getWidth() - spaceBeforeChromosome - spaceAfterChromosome;
        double lengthratio = (double) chromosomeofgene.length / (double) longestchromosomeSize;
        double relativechromosomelength = (chromosomedrawlength - centromerWidth) * lengthratio;
        float longarm_topleftX = Math.round(spaceBeforeChromosome);
        float longarm_topleftY = headerHeight + (chromosomeHeight + verticalDistanceBetweenChromosomes) * indexofchromosome;

        int genetop;
        if (chromosomeofgene.centromerlocation >= 0 && gene.genelocation < chromosomeofgene.centromerlocation) {
            //on the longarm
            genetop = (int) Math.round(longarm_topleftX + relativechromosomelength * relativegenelocation);
        } else if (chromosomeofgene.centromerlocation >= 0 && gene.genelocation > chromosomeofgene.centromerlocation) {
            //on the shortarm
            genetop = (int) Math.round(longarm_topleftX + centromerWidth + relativechromosomelength * relativegenelocation);
        } else if (chromosomeofgene.centromerlocation >= 0 && gene.genelocation == chromosomeofgene.centromerlocation) {
            //on the centromer
            genetop = (int) Math.round(longarm_topleftX + centromerWidth / 2 + relativechromosomelength * relativegenelocation);
        } else
            genetop = (int) Math.round(longarm_topleftX + relativechromosomelength * relativegenelocation);

        //System.out.println(genetop);
        g2.fillRect(genetop - geneWidth / 2, (int) longarm_topleftY, geneWidth, geneHeight);
    }

    public void update(Graphics g) {

        int width = getWidth();
        int height = getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        if (orderedchromosomes == null) {
            Font textfont = new Font("Times", Font.PLAIN, 20);
            g.setFont(textfont);
            g.setColor(Color.red);
            g.drawString("Selected organisms", 5, 20);
            g.drawString("do not have", 5, 40);
            g.drawString("gene information.", 5, 60);
            /*g.drawString("(One pathway", 5, 90);
            g.drawString("for which", 5, 110);
            g.drawString("gene information is", 5, 130);
            g.drawString("available is the", 5, 150);
            g.drawString("folate pathway)", 5, 170);*/
        } else {
            long longestsize = getLongestChromosomeSize();

            int index = 0;
            for (GenomeTable.ChromosomeEntry chromosome : orderedchromosomes) {
                paintChromosomeDrawing(g2, index, longestsize, chromosome);
                index++;
            }

            for (GenomeTable.GeneEntry gene : genes) {
                paintGeneDrawing(g2, gene, longestsize);
            }
        }

    }

    public static void drawTextInRectangle(String drawString, int x, int y, int width, int height, boolean drawborder, Font textfont, Color textcolor, Graphics g) {
        /*g.setColor(Color.green);
        g.drawRect(x, y, width, height);*/

        String fontname = "Arial";
        float zoomAmount = 1;
        float drawfontSize = 12;

        if (textfont == null)
            textfont = new Font(fontname, Font.BOLD, (int) (drawfontSize * zoomAmount));
        g.setFont(textfont);

        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fontMetrics = g2d.getFontMetrics();

        /*float textwidthtest = fontMetrics.stringWidth(drawString);
        zoomAmount = textwidthtest/(float)width;
        textfont = new Font(fontname, Font.PLAIN, (int) (drawfontSize * zoomAmount));
        g.setFont(textfont);*/

        StringTokenizer st = new StringTokenizer(drawString, "\n");
        int numberoflinesintext = st.countTokens();

        if (numberoflinesintext <= 1) {

            int textwidth = fontMetrics.stringWidth(drawString);
            int textheight = fontMetrics.getHeight();
            int xdif = (int) ((width * zoomAmount - textwidth) / 2);
            int ydif = (int) ((height * zoomAmount - textheight) / 2);
            if (ydif < 0) {
                textfont = new Font(fontname, Font.PLAIN, (int) (height * zoomAmount));
                g.setFont(textfont);
                fontMetrics = g2d.getFontMetrics();
                textwidth = fontMetrics.stringWidth(drawString);
                textheight = fontMetrics.getHeight();
                xdif = (int) ((width * zoomAmount - textwidth) / 2);
                ydif = (int) ((height * zoomAmount - textheight) / 2);
            }
            while (xdif < 0 && drawString.length() > 0) {
                drawString = drawString.substring(0, drawString.length() - 1);
                textwidth = fontMetrics.stringWidth(drawString);
                xdif = (int) ((width * zoomAmount - textwidth) / 2);
            }

            g.setColor(textcolor);

            if (!drawborder) {
                g.drawString(drawString, Math.round(x), Math.round(y + textfont.getSize2D()/*+entityHeight*/));
            } else {
                g.drawString(drawString, Math.round(x + xdif), Math.round(y + ydif + textfont.getSize2D()/*+entityHeight*/));
            }

        } else {
            int topleftX = Math.round(x);
            int topleftY = Math.round(y);

            int lineindex = 0;

            while (st.hasMoreTokens()) {
                String drawLine = st.nextToken();
                int textwidth = fontMetrics.stringWidth(drawLine);
                int textheight = fontMetrics.getHeight();
                int xdif = (int) ((width * zoomAmount - textwidth) / 2);
                int ydif = (int) ((height * zoomAmount - textheight * numberoflinesintext) / (1 + numberoflinesintext));
                if (ydif < 0) {
                    textfont = new Font(fontname, Font.PLAIN, (int) (height * zoomAmount));
                    g.setFont(textfont);
                    fontMetrics = g2d.getFontMetrics();
                    textwidth = fontMetrics.stringWidth(drawLine);
                    textheight = fontMetrics.getHeight();
                    xdif = (int) ((width * zoomAmount - textwidth) / 2);
                    ydif = (int) ((height * zoomAmount - textheight * numberoflinesintext) / (1 + numberoflinesintext));

                }
                while (xdif < 0 && drawLine.length() > 0) {
                    drawLine = drawLine.substring(0, drawLine.length() - 1);
                    textwidth = fontMetrics.stringWidth(drawLine);
                    xdif = (int) ((width * zoomAmount - textwidth) / 2);
                }

                g.setColor(textcolor);
                if (!drawborder) {
                    g.drawString(drawLine, topleftX, topleftY + Math.round(textfont.getSize2D()));
                } else {
                    g.drawString(drawLine, topleftX + xdif, topleftY + (ydif * (lineindex + 1)) + (textheight * lineindex) + Math.round(textfont.getSize2D()));
                }

                lineindex++;
            }
        }

    }

    public void paint(Graphics g) {
        update(g);
    }

    public void setSelectedOrganism(String selectedOrganismId) {
        this.selectedOrganismId = selectedOrganismId;
        int required_height;
        if (orderedchromosomes == null)
            required_height = 0;
        else
            required_height = orderedchromosomes.size() * chromosomeHeight + (orderedchromosomes.size() - 1) * verticalDistanceBetweenChromosomes + headerHeight + footerHeight;
        this.setPreferredSize(new Dimension(this.getWidth() - extraBarWidth, required_height));
        if (scroller != null)
            scroller.updateUI();
    }

    public void setScroller(JScrollPane scroller) {
        this.scroller = scroller;
    }
}
