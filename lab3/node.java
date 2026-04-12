
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Node with unique DT learning functionality. Every node has a unique ID.
 */

public class node implements Serializable {
    private node trueNeighbor;
    private node falseNeighbor;
    private final ArrayList<observation> knowledge;
    private static int nextID = 0;
    private final int id;

    //the attribute which the node uses to split OR a label if this is a leaf.
    private String value;


    public node(ArrayList<observation> knowledge) {
        this.knowledge = knowledge;
        this.id = nextID;
        nextID++;
    }

    public String getValue() {
        return value;
    }

    
    public int getId() {
        return id;
    }

    public node getTrueNeighbor() {
        return trueNeighbor;
    }

    public node getFalseNeighbor() {
        return falseNeighbor;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPure() {
        double countA = 0;
        double countB = 0;
        for(observation o : knowledge) {
            if(o.getLabel().equals("A")){
                countA++;
            } else {
                countB++;
            }
        }
       // System.out.println("COUNTA: " + countA + "COUNTB: " + countB);
        if(countA == 0 || countB == 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getMajorityLabel() {
        double countA = 0;
        double countB = 0;
        for(observation o : knowledge) {
            if(o.getLabel().equals("A")){
                countA += o.getWeight();
            } else {
                countB += o.getWeight();
            }
        }
        if(countA > countB) {
            return "A";
        } else {
            return "B";
        }
    }

    /**
     * Based on this node's knowledge, find and split on the best attribute.
     * Copy observation entries down to corrosponding node.
     * @return node[2] = {trueNeighbor, falseNeighbor}
     */
    public node[] splitOnBest() {
        int best = getIndexOfBestAttribute();
        this.value = knowledge.get(0).getAttribute(best).getName();

        //split observations based on their responses 
        ArrayList<observation> trueKnowledge = new ArrayList<>();
        ArrayList<observation> falseKnowledge = new ArrayList<>();
        //int misclassifiedCt = 0;
        for(observation o : knowledge) {
            if(o.getAttribute(best).isTrue()) {
                trueKnowledge.add(o);
            } else {
                falseKnowledge.add(o);
            }
        }

        //System.out.println(misclassifiedCt);
        this.falseNeighbor = new node(falseKnowledge);
        this.trueNeighbor = new node(trueKnowledge);

        return new node[]{this.trueNeighbor,this.falseNeighbor};
    }

    
    @Override
    public String toString() {
        if(getFalseNeighbor() == null) {
            return "";
        }
        String ret = "[" + value  + ", " + id + "]" + " Neighbors: ";
        ret += "F:" + "[" + getFalseNeighbor().getValue()  + ", " + getFalseNeighbor().getId() + "] " + "T:" + "[" + getTrueNeighbor().getValue() + ", " + getTrueNeighbor().getId() + "]\n";
        ret += getTrueNeighbor().toString();
        ret += getFalseNeighbor().toString();
        return ret;
    }

    /**
     * Computes entropy given A and B counts
     * @param int countA
     * @param int coutnB
     */
    private static double computeEntropy(double countA, double countB) {
        double total = countA + countB;
        double probA = countA/total;
        double probB = countB/total;
        return -1*(probA * Math.log(probA) + probB * Math.log(probB));
    }

    /**
     * Computes the entropy of the node in its current state (H(S))
     * @return double entropy
     */
    private double getEntropyBeforeSplit() {
        double countA = 0;
        double countB = 0;
         for(observation o : knowledge) {
            if(o.getLabel().equals("A")){
                countA += o.getWeight();
            } else {
                countB += o.getWeight();
            }
        }
       return computeEntropy(countA, countB);

    }

    /**
     * Calculates the entropy for every attribute in the knowledge base and finds the best.
     * @return The index within an observation that points to the attribute.
     * EX: knowledge(0) -> observation[return] = best attribute to split on
     */
    private int getIndexOfBestAttribute() {
        double currentEntropy = getEntropyBeforeSplit();
        double bestIG = -1;
        int indexOfBestAttribute = 0;

        for(int c = 0; c < knowledge.get(0).length(); c++) { //walk the columns
            double countATrue = 0;
            double countBTrue = 0;
            double countAFalse = 0;
            double countBFalse = 0;
            for(observation o : knowledge) { //walk the rows of that column
                if(o.getAttribute(c).isTrue()){
                    if(o.getLabel().equals("A")) {
                        countATrue += o.getWeight();
                    } else {
                        countBTrue += o.getWeight();
                    }
                } else {
                       if(o.getLabel().equals("A")) {
                        countAFalse += o.getWeight();
                    } else {
                        countBFalse += o.getWeight();;
                    }
                }
            }

            double hTrue = computeEntropy(countATrue, countBTrue);
            double hFalse = computeEntropy(countAFalse,countBFalse);
          
            //compute overall split H
            double totalOverall = countAFalse + countATrue + countBFalse + countBTrue;
            double trueSetSize = countATrue + countBTrue;
            double falseSetSize = countAFalse + countBFalse;
            double informationGain = currentEntropy - (hTrue * (trueSetSize/totalOverall) + hFalse * (falseSetSize/totalOverall));
            // System.out.println("info gain: " + informationGain);
            // System.out.println("best nfo gain: " + bestIG);
            // System.out.println(c);
            //if this cycle's info gain is greater then current best then update best index and best info
            if(informationGain > bestIG) {
                bestIG = informationGain;
                indexOfBestAttribute = c;
            }
        }

        return indexOfBestAttribute;
    }

} 