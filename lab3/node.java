
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



        //split examples into s1 and s2 based on

        return null;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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

        double total = countA + countB;
        double probA = countA/total;
        double probB = countB/total;

        return -1*(probA * Math.log(probA) + probB * Math.log(probB));

    }

    /**
     * Calculates the entropy for every attribute in the knowledge base and finds the best.
     * @return The index within an observation that points to the attribute.
     * EX: knowledge(0) -> observation[return] = best attribute to split on
     */
    private int getIndexOfBestAttribute() {
        double currentEntropy = getEntropyBeforeSplit();
        double bestKG = 0;
        int indexOfBestKG = -1;

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
            int totalOverall = countAFalse + countATrue + countBFalse + countBTrue;
            int totalFalse = countAFalse + countBFalse;
            int totalTrue = countATrue + countBTrue;
            //compute the entropy


        }

        return indexOfBestKG;
    }

} 