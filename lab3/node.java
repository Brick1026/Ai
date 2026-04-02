
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Node with unique DT learning functionality
 */

class node implements Serializable {
    private node trueNeighbor;
    private node falseNeighbor;
    private final ArrayList<observation> knowledge;

    //the attribute which the node uses to split OR a label if this is a leaf.
    private String value;

    public node(ArrayList<observation> knowledge) {
        this.knowledge = knowledge;
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
        for(observation o : knowledge) {
            if(o.getAttribute(best).isTrue()) {
                trueKnowledge.add(o);
            } else {
                falseKnowledge.add(o);
            }
        }
        this.falseNeighbor = new node(falseKnowledge);
        this.trueNeighbor = new node(trueKnowledge);

        return new node[]{this.trueNeighbor,this.falseNeighbor};
    }

    
    @Override
    public String toString() {
        return "[" + value + "]";
    }

    /**
     * Computes entropy given A and B counts
     * @param int countA
     * @param int coutnB
     */
    private static double computeEntropy(int countA, int countB) {
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
        int countA = 0;
        int countB = 0;
        for(observation o : knowledge) {
            if(o.getLabel().equals("A")){
                countA++;
            } else {
                countB++;
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
        double bestIG = 0;
        int indexOfBestAttribute = -1;

        for(int c = 0; c < knowledge.get(0).length(); c++) { //walk the columns
            int countATrue = 0;
            int countBTrue = 0;
            int countAFalse = 0;
            int countBFalse = 0;
            for(observation o : knowledge) { //walk the rows of that column
                if(o.getAttribute(c).isTrue()){
                    if(o.getLabel().equals("A")) {
                        countATrue++;
                    } else {
                        countBTrue++;
                    }
                } else {
                       if(o.getLabel().equals("A")) {
                        countAFalse++;
                    } else {
                        countBFalse++;
                    }
                }
            }

            double hTrue = computeEntropy(countATrue, countBTrue);
            double hFalse = computeEntropy(countAFalse,countBFalse);
          
            //compute overall split H
            int totalOverall = countAFalse + countATrue + countBFalse + countBTrue;
            int trueSetSize = countATrue + countBTrue;
            int falseSetSize = countAFalse + countBFalse;
            double informationGain = currentEntropy - (hTrue * (trueSetSize/totalOverall) + hFalse * (falseSetSize/totalOverall));

            //if this cycle's info gain is greater then current best then update best index and best info
            if(informationGain > bestIG) {
                bestIG = informationGain;
                indexOfBestAttribute = c;
            }
        }

        return indexOfBestAttribute;
    }

} 