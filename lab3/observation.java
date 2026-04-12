
import java.io.Serializable;
import java.util.ArrayList;

/*
Observations can either hold an unlabelled occurence or a labeled occurence.
In other words, observations can function as both training examples and examples to identify.
These are used for cummunication between the main IO driven loop and the internal logical functions.
*/
public class observation implements Serializable {
    private final ArrayList<attribute> attributes = new ArrayList<>();
    private String label; //should be either A or B
                          //A is treated as English and B as dutch
                          //this is for simplicity with HW3 compat


    private double weight;

    /**
     * Looks through the attributes arraylist
     * if it finds an attribute with the given name returns it's index.
     * If an attribute is NOT found return -1.
     * @param name the name of an attribute in attributes 
     * @return -1 or the index of the attribute with the given name
     */
    public int getIndexOfAttribute(String name) {
        int idx = -1;
        for(int i = 0; i < attributes.size(); i++) {
            if(attributes.get(i).getName().equals(name)) {
                idx = i;
            }
        }
        return idx;
    }

    public double getWeight() {
        return weight;
    }
    

    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    /**
     * Used for training data that has already been labeled and is weighted.
     * @param String label
     * @param double weight
     */
    public observation(String label, double weight) {
        //convert to internal representation where en = A and dutch = B
        if(label.equals("en")) { 
            this.label = "A";
        } else if (label.equals("nl")) {
            this.label = "B";
        } else {
            this.label = label;
        }
        this.weight = weight;
    }

    /**
     * Used for training data that has already been labeled
     * @param String label
     */
    public observation(String label) {
        this.label = label;
        this.weight = 1;
    }
    
    /**
     * Used for examples that need to be labeled
     */
    public observation() {
        this.label = "unknown";
        this.weight = 1;
    }
    
    /**
     * Retrieves the string label for an observation
     * @return String label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets a label for an observation 
     * Used for prediction when the label is "unknown"
     * @param string label
     */
    public void setLabel(String label) {
        this.label = label;
    }

     /**
     * Adds an attribute to an observation
     * @param attribute a
     */
    public void addAttribute(String name, boolean value) {
        attributes.add(new attribute(name,value));
    }

     /**
     * Returns an attribute at an index
     * @return attribute at index
     */
    public attribute getAttribute(int index) {
        return attributes.get(index);
    }

    /**
     * Gets the number of attributes in an observation
     * @return attributes.size()
     */
    public int length() {
        return attributes.size();
    }

    public class attribute implements Serializable {
        private final String name;
        private final boolean value;

        /**
         * Stores info about a single attribute within an observation
         * @param String name of attribute
         * @param boolean value of attribute
         */
        public attribute(String name, boolean value) {
            this.name = name;
            this.value = value;
        }

        /**
         * for debugging and printing
         * @return String name of attribute
         */
        public String getName() {
            return name;
        }

        /**
         * for unpacking and logic inside of learning algorithms
         * @return boolean value of attribute
         */
        public boolean isTrue() {
            return value;
        }
          
    }
}
