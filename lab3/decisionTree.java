
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A seralizable decision tree. This is the output of the dt learning program.
 */
public class decisionTree implements Serializable {

    private node root;
    private final ArrayList<observation> knowledge;

    public decisionTree(ArrayList<observation> knowledge) {
        this.knowledge = knowledge;
    }
    
    /**
     * Train() - adds an additional decision tree split to all leaves
     */
    public void train() {
        if(root == null) { 
            this.root = new node(knowledge);
            root.splitOnBest();
            return;
        }
        dfsplit(root.getFalseNeighbor());
        dfsplit(root.getTrueNeighbor());

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


