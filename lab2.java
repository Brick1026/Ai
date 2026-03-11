
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

 // int thisTermListSize = terms.size();
            // int otherTermListSize = other.getTerms().size();
            // int loopSize;
            // int leftover;
            // boolean thisLarger = thisTermListSize >= otherTermListSize;
            // if(thisLarger) { //determine smaller predicate
            //     loopSize = otherTermListSize;
            //     leftover = thisTermListSize;
            // } else {
            //     loopSize = thisTermListSize;
            //     leftover = otherTermListSize;
            // }

            // for(int i = 0; i < loopSize; i++) {
            //     Term thisTerm = this.terms.get(i);
            //     Term otherTerm = other.getTerms().get(i);

            //     Term result = thisTerm.unify(otherTerm); //unify two terms
            //     if(result == null) {
            //         return null; //two terms do not unify. Return null.
            //     } else {
            //         newPredicateTermList.add(result);
            // //     }
            // // }

            // for(int i = loopSize; i < leftover; i++) {
            //     if(thisLarger) {
            //         switch (terms.get(i)) {
            //             case Func func -> newPredicateTermList.add(new Func(func));
            //             case Const cnst -> newPredicateTermList.add(new Const(cnst.getName()));
            //             case Var var -> newPredicateTermList.add(new Var(var.getName()));
            //             default -> throw new Error("Unexpected generic term.");
            //         }
            //     } else {
            //         switch (other.getTerms().get(i)) {
            //             case Func func -> newPredicateTermList.add(new Func(func));
            //             case Const cnst -> newPredicateTermList.add(new Const(cnst.getName()));
            //             case Var var -> newPredicateTermList.add(new Var(var.getName()));
            //             default -> throw new Error("Unexpected generic term.");
            //         }
            //     }
            // }
            

public class lab2 {
     public static void main(String[] args) {

        KB myKnowledgeBase = new KB();
        HashSet<String> constants;
        HashSet<String> variables;
        File file = new File(args[0]);
        try (Scanner scanner = new Scanner(file)) {
            scanner.nextLine(); //skip predicates (we will read as we find them)

            scanner.skip(Pattern.compile("Variables: ")); //record variables as strings
            variables = new HashSet<>(Arrays.asList(scanner.nextLine().split(" ")));

            scanner.skip(Pattern.compile("Constants: ")); //records constants
            constants = new HashSet<>(Arrays.asList(scanner.nextLine().split(" ")));

            scanner.nextLine(); //skip functions (we will read as we find them)
            // scanner.skip(Pattern.compile("Functions: "));
            // functions = new HashSet<>(Arrays.asList(scanner.nextLine().split(" ")));

            scanner.nextLine(); // skip empty "Clauses: "" line

            while(scanner.hasNextLine()) {
                String[] clause = scanner.nextLine().split(" ");
                ArrayList<Predicate> predicates = new ArrayList<>();
                for(String predicate : clause) {
                    int parameterStartIndex = predicate.lastIndexOf('(');
                    int parameterEndIndex = predicate.lastIndexOf(')');

                    boolean isNegated = false;
                    String predicateName;
                    //check if predicate negated
                    if(predicate.charAt(0) == '!') {
                        predicateName = predicate.substring(1,parameterStartIndex);
                        isNegated = true;
                    } else {
                        predicateName = predicate.substring(0,parameterStartIndex); 
                    }
                    
                    //get all predicate terms
                    String[] terms = predicate.substring(parameterStartIndex+1,parameterEndIndex).split(",");
                    ArrayList<Term> predicateTerms = new ArrayList<>();
                    for(String term : terms) {
                        //identify term type
                        if(constants.contains(term)) {
                            predicateTerms.add(new Const(term));
                        } else if(variables.contains(term)) {
                            predicateTerms.add(new Var(term));
                        } else {
                            //if function identify function parameter and then adds
                            String param = term.substring(term.indexOf('(')+1,term.indexOf(')'));
                            String termName = term.substring(0,term.indexOf(')'));
                            if(constants.contains(param)) {
                                predicateTerms.add(new Func(termName,new Const(param)));
                            } else {
                                predicateTerms.add(new Func(termName,new Var(param)));
                            }
                        }
                    }
                    predicates.add(new Predicate(predicateName,isNegated,predicateTerms));

                }

                myKnowledgeBase.add(new Clause(predicates));
                
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(-1);
        }

         myKnowledgeBase.prove();
         if(myKnowledgeBase.holds()) {
            System.out.println("yes");
         } else {
             System.out.println("no");
         }

     }
}

class KB {
    private ArrayList<Clause> clauses;
    private boolean contradiction;
    private boolean proved;


    //TODO: Add toString to test read in



    /**
     * Representation on a Knowledge Base
     */
    public KB() {
        clauses = new ArrayList<>();
        contradiction = false;
        proved = false;
    }

    /**
     * Adds clause c to knowledge base
     * @param c clause to add
     */
    public void add(Clause c) {
        clauses.add(c);
    }

    /**
     * Perform resolution on the knowledge base
     */
    public void prove() {
        //TODO: Implement resolution algo
    }

    /**
     * Check if proven and no contradiction
     * @return boolean if holds
     */
    public boolean holds() {
        return !contradiction && proved;
    }

    @Override
    public String toString() {
        //TODO: Implement toString to verify functional read in.
        return "";
    }
}

class Clause {
    private ArrayList<Predicate> predicates;
    

    //TODO: Add toString to test read in


    //TODO: Add Clause Resolution function

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
     * Get predicate arraylist
     * @return Predicates
     */
    public ArrayList<Predicate> getPredicates() {
        return predicates;
    }

}

class Predicate {

    private static int predicateCount = 0; //number of predicates 

    //keeps track of all name >> id pairs.
    //Two predicates must share BOTH a name and id if they share either.
    //These two predicates are the same.
    private static HashMap<String,Integer> nameIdPairs = new HashMap<>();

    //Have we seen this a predicate with these parameters before?
    private static HashSet<Predicate> visited = new HashSet<>();


    private final ArrayList<Term> terms; //the terms for the predicate

    private final int id; //id indicating a certain predicate.
                    //Multiple instnace of that predicate will share id.
                    //Two predicates with same name will share id

    private boolean isNegated; //negation state of predicate

    private final String name; //String name 
 

    /**
     * Checks if two predicates can resolve. 
     * If yes, return the result of the resolution.
     * If no, return null;
     * If resolution produces empty clause will activate contradiction flag.
     * @param other predicate to resolve with
     * @return NULL or resolved Predicate
     */
    public Predicate resolve(Predicate other) {
        ArrayList<Term> newPredicateTermList = new ArrayList<>();

        if(this.isInverse(other)) { //check predicates are inverses (same name and opposite polarity)
            int thisTermListSize = terms.size();
            int otherTermListSize = other.getTerms().size();
            if(thisTermListSize != otherTermListSize) {
                return null; //different arity. Can't unify.
            }

            for(int i = 0; i < thisTermListSize; i++) {
                Term thisTerm = this.terms.get(i);
                Term otherTerm = other.getTerms().get(i);

                Term result = thisTerm.unify(otherTerm); //unify two terms
                if(result == null) {
                    return null; //two terms do not unify. Return null.
                } else {
                    newPredicateTermList.add(result);
                }
            }
            
            return new Predicate(name, false, newPredicateTermList); //valid new predicate can be made
        }

        return null; //not inverses. You can't unify.
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
        hash = 61 * hash + Objects.hashCode(this.terms);
        hash = 61 * hash + this.id;
        hash = 61 * hash + (this.isNegated ? 1 : 0);
        hash = 61 * hash + Objects.hashCode(this.name);
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

    /**
     * Checks if this is an equal inverse of p
     * @param p other predicate
     * @return boolean true or false
     */
    public boolean isInverse(Predicate p) {
        return this.equals(p) && (this.isNegated != p.isNegated());
    }

    /**
     * Deep clones a predicate 
     * @param p
     */
    public Predicate(Predicate p) {
        this.id = p.getId();
        this.name = p.getName();
        this.isNegated = p.isNegated();
        this.terms = new ArrayList<>();
        for(Term t : p.getTerms()) {
            switch (t) {
                case Func func -> this.terms.add(new Func(func));
                case Const cnst -> this.terms.add(new Const(cnst.getName()));
                case Var var -> this.terms.add(new Var(var.getName()));
                default -> throw new Error("Unexpected generic term.");
            }
        }
    }

    /**
     * Creates a new predicate and assigns it a new ID if it doesn't exsist.
     * If the ID already exsists then a new predicate is made with the same ID.
     * @param name name of the predicate
     * @param isNegated negation status of predicate
     */
    public Predicate(String name, boolean isNegated, ArrayList<Term> terms) {
        Integer lid;
        if((lid = nameIdPairs.get(name)) != null) { //already have a pedicate and id
            this.id = lid;
            this.name = name;
            this.isNegated = isNegated;
            this.terms = terms;
        } else { //new predicate so we should assign id and incr count
            this.id = predicateCount;
            this.name = name;
            this.isNegated = isNegated;
            this.terms = terms;
            
            //add to hashmap and increment id counter
            nameIdPairs.put(this.name,this.id);
            predicateCount++;
        }
        visited.add(this);
    }

    /**
     * Converts a term to a predicate. For internal processing only. Not logically sound.
     * @param t Term to convert
     * @param isNegated if to negate
     * @return the new Predicate object
     */
    public static Predicate asPredicate(Term t, boolean isNegated) {
        ArrayList<Term> myArr = new ArrayList<>();
        myArr.add(t);
        return new Predicate(t.getName(),isNegated,myArr);
    }
    

    /**
     * Gets the id of the current predicate
     * @return id
     */
    private int getId() {
        return id;
    }
    
    
    /**
     * Returns negation status
     * @return isNegated
     */
    private boolean isNegated() {
        return isNegated;
    }

    /**
     * Gets the String name of Predicate
     * @return name
     */
    private String getName() {
        return name;
    }

    private ArrayList<Term> getTerms() {
        return terms;
    }
    
    
}


abstract class Term {
    private static int termCount = 0; //number of predicates 
    //keeps track of all name >> id pairs.
    //Two terms must share BOTH a name and id if they share either.
    //These two terms are the same.
    private static HashMap<String,Integer> nameIdPairs = new HashMap<>();

    private final String name; 

    private final int id; //used to compare if a name is unique, 
                          //ignores things added by child classes
                          //(weaker equals basically)

    //should never trigger. You can't have a generic term.
    Term unify(Term otherParam) {
        throw new Error("This should never be triggered. You can't have a generic term.");
    }

    public String getName() {
        return name;
    }

    
    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + this.id;
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
        final Term other = (Term) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    public Term(Term t) {
        //deep copy constructor
        this.name = t.getName();
        this.id = t.getId();
    }

    public Term(String name) {
        Integer lid;
        if((lid = nameIdPairs.get(name)) != null) { //already have a term and id
            this.id = lid;
            this.name = name;
        } else { //new term so we should assign id and incr count
            this.id = termCount;
            this.name = name;
            //add to hashmap and increment id counter
            nameIdPairs.put(this.name,this.id);
            termCount++;
        }
    }

}

class Const extends Term {

    //unification polymorphism
    public Term unify(Const other) {
        if(this.equals(other)) {
            return this;
        }
        return null;
    }

    public Term unify(Func other) {
        return null;
    }

    public Term unify(Var other) {
        return this;
    }

    public Const(String name) {
        super(name);
    }
}

class Func extends Term {
    private Term parameter;
    
    public Term unify(Const other) {
        return null;
    }

    public Term unify(Func other) {
        if(this.getId() != other.getId()) { //check same symbol
            return null; //different functions, will not unify
        }
    
        Term otherParam = other.getParameter();
        Term result = this.getParameter().unify(otherParam);
        if(result == null) {
            return null; //can't unify terms 
        } else {
            Func unifiedFunction = new Func(this.getName(),result);
            return unifiedFunction;
        }
    }

    public Term unify(Var other) {
        if(!parameter.equals(other)) { //if function doesn't contain variable
            return this;
        }
        return null; //if function contains variable
    }

    public Func(String name, Term parameter) {
        super(name);
        this.parameter = parameter;
    }

    public Func(Func f) {
        super(f);
        this.parameter = f.getParameter();
    }

    public void setParameter(Term p) {
        this.parameter = p;
    }

    public Term getParameter() {
        return this.parameter;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.parameter) ;
        hash = 79 * hash + super.hashCode();
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
        final Func other = (Func) obj;
        if(!super.equals(other)) {
            return false;
        }
        return Objects.equals(this.parameter, other.parameter);
    }
}

class Var extends Term {
    
    //unification polymorphism
    public Term unify(Const other) {
        return other;
    }

    public Term unify(Func other) {
        if(!other.getParameter().equals(this)) { //if function doesn't contain variable
            return other;
        }
        return null;
    }

    public Term unify(Var other) {
        return this;
    }

    public Var(String name) {
        super(name);
    }
}