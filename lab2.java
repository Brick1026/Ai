
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class lab2 {
     public static void main(String[] args) {

     }
}

class Clause {
    private ArrayList<Predicate> predicates;

    /**
     * Creates an empty clause
     */
    public Clause() {
        this.predicates = new ArrayList<>();
    }
    /**
     * Creates a clause from a predicate ArrayList
     * @param predicates pred Arraylist
     */
    public Clause(ArrayList<Predicate> predicates) {
        this.predicates = predicates;
    }

     /**
     * @param Clause clause
     * Creates a clause from a clause
     */
    public Clause(Clause clause) {
        //deep copy my predicate objects
        ArrayList<Predicate> predArr = clause.getPredicates();
        ArrayList<Predicate> predArrDeepCopy = new ArrayList<>();
        for(Predicate p : predArr) {
            predArrDeepCopy.add(new Predicate(p));
        }
        this.predicates = predArrDeepCopy;
    }
    

    /**
     * Get prdicate at i
     * @param i index
     * @return Predicate
     */
    public Predicate getPredicateAt(int i) {
        return predicates.get(i);
    }

    /**
     * Add predicate p to predicates
     * @param p
     */
    public void addPredicate(Predicate p) {
        this.predicates.add(p);
    }

     /**
     * Get predicate arraylist
     * @return Predicates
     */
    private ArrayList<Predicate> getPredicates() {
        return predicates;
    }


    class Predicate {
        private static int predicateCount = 0; //number of predicates 

        //keeps track of all name >> id pairs.
        //Two predicates must share BOTH a name and id if they share either.
        //These two predicates are the same.
        private static HashMap<String,Integer> nameIdPairs = new HashMap<>();

        private final int id; //id indicating a certain predicate.
                        //Multiple instnace of that predicate will share id.
                        //Two predicates with same name will share id

        private final boolean isNegated; //negation state of predicate

        private final String name; //String name 


        /**
         * Deep clones a predicate 
         * @param p
         */
        protected Predicate(Predicate p) {
            this.id = p.getId();
            this.name = p.getName();
            this.isNegated = p.isNegated();
        }

        /**
         * Creates a new predicate and assigns it a new ID if it doesn't exsist.
         * If the ID already exsists then a new predicate is made with the same ID.
         * @param name name of the predicate
         * @param isNegated negation status of predicate
         */
        protected Predicate(String name, boolean isNegated) {
            Integer lid;
            if((lid = nameIdPairs.get(name)) != null) { //already have a pedicate and id
                this.id = lid;
                this.name = name;
                this.isNegated = isNegated;
            } else { //new predicate so we should assign id and incr count
                this.id = predicateCount;
                this.name = name;
                this.isNegated = isNegated;
                
                //add to hashmap and increment id counter
                nameIdPairs.put(this.name,this.id);
                predicateCount++;
            }
        }
        

        /**
         * Gets the id of the current predicate
         * @return id
         */
        protected int getId() {
            return id;
        }
        
        /**
         * Returns negation status
         * @return isNegated
         */
        protected boolean isNegated() {
            return isNegated;
        }

        /**
         * Gets the String name of Predicate
         * @return name
         */
        protected String getName() {
            return name;
        }

        /**
         * To String override for debugging purposes
         */
        @Override
        public String toString() {
            return name + "<" + id + ", " + !isNegated + ">";
        }


        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + this.id;
            hash = 59 * hash + Objects.hashCode(this.name);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Predicate other = (Predicate) obj;
            if (this.id != other.id) {
                return false;
            }
            return Objects.equals(this.name, other.name);
        }
        
        
    }
}
