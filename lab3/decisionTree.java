
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A seralizable decision tree. This is the output of the dt learning program.
 */
public class decisionTree implements Serializable {

    private node root;
    private node next;
    private node current;
    private final ArrayList<observation> knowledge;

    public decisionTree(ArrayList<observation> knowledge) {
        this.knowledge = knowledge;
    }
    
    /**
     * Resets traversal through tree
     */
    public void resetTraversal() {
        next = root;
        current = null;
    }

    /**
     * Gets the label of the current node (for tree traversal)
     * If this is a leaf this will be the answer to a prediction.
     * @return node current
     */
    public String currentLabel() {
        return current.getValue();
    }


    /**
     * When prdicting, check if there is another decision to make
     * This will only be false if we are at a leaf.
     * @return true if there is a next move otherwise false
     */
    public boolean hasNext() {
        if(next != null) {
            return true;
        }
        return false;
    }

    /**
     * Move to next, look at the current example and determine new next based on old next
     * @return true if there is a next move otherwise false
     */
    public void branch(observation example) {
        current = next;
        if(current.getFalseNeighbor() == null) { //if this is a leaf then leave next as null
            next = null;
            return;
        }

        int idx = example.getIndexOfAttribute(current.getValue());

         //if for some reason our example was created without this feature just choose arbitary
        if(idx == -1) {

            next = current.getFalseNeighbor();
            return;
        } 
        
        //if the branch condition of the current node is true in our example set next to true neighbor
        //else set next to false
        if(example.getAttribute(idx).isTrue()) {
            next = current.getTrueNeighbor();
        } else {
            next = current.getFalseNeighbor();
        }
    }

    /**
     * Train() - adds an additional decision tree split to all leaves
     */
    public void train() {
        if(root == null) { 
            this.root = new node(knowledge);
            this.next = root;
            root.splitOnBest();
            return;
        }
        dfsplit(root.getFalseNeighbor());
        dfsplit(root.getTrueNeighbor());

    }

    /**
     * markLeaves() - denotes leaves with an A or B label. Should be run after training is complete.
     */
    public void markLeaves() {
        if(root == null) { 
            System.err.println("Need at least round of training to mark leaves");
            System.exit(-1);
        }
        
        dfsmark(root.getFalseNeighbor());
        dfsmark(root.getTrueNeighbor());

    }

    /**
     * dfsplit() - helper function for training
     * @param node n node to start dfs search for leaves to split
     */
    private void dfsplit(node n) {

        //if n is all B or all A then return.
        if(n.isPure()) {
            return;
        }

        if(n.getFalseNeighbor() == null) {
            n.splitOnBest();
            return;
        }

        dfsplit(n.getFalseNeighbor());
        dfsplit(n.getTrueNeighbor());
    }

    /**
     * dfsmark() - helper function for marking leaves
     * @param node n node to start dfs search for leaves to mark
     */
    private void dfsmark(node n) {
         if(n.getFalseNeighbor() == null) {
            if(n.getMajorityLabel().equals("A")) {
                n.setValue("A");
                return;
            } else {
                n.setValue("B");
                return;
            }
        }

        dfsmark(n.getFalseNeighbor());
        dfsmark(n.getTrueNeighbor());
    }
    
    @Override
    public String toString()  {
        return root.toString();
    }
}


