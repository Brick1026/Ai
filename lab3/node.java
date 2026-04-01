
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Node with unique ID (used for Hashing)
 */

class node implements Serializable {
    private final ArrayList<node> neighbors = new ArrayList<>();

    public void addNeighbor(node n) {
        //TODO: Implement
        neighbors.add(n);
    }

    public ArrayList<node> getNeighbors() {
        //TODO: Implement
        return neighbors;
    }

} 