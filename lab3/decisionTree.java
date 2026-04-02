
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
     * Train() - adds an additional decision tree split
     */
    public void train() {
        if(root == null) { 
            root = new node(knowledge);
        }

        //TODO: Implement
        //WHAT DOES THIS LOOK LIKE?
    }
    
    @Override
    public String toString() {
        //TODO: Implement
        //THIS IS HOW I WILL GET A TREE FOR HW3. THIS IS VERY IMPORTANT
        return "";
    }
}


