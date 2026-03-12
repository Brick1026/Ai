
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
            
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

            scanner.nextLine(); // skip empty "Clauses: "" line

            while(scanner.hasNextLine()) {
                String[] clause = scanner.nextLine().split(" ");
                ArrayList<Predicate> predicates = new ArrayList<>();
                for(String predicate : clause) {
                    int parameterStartIndex = predicate.lastIndexOf('(');
                    int parameterEndIndex = predicate.lastIndexOf(')');

                    if(parameterStartIndex == -1) {
                        parameterStartIndex = predicate.length(); //no parameters so the entire predicate is the substring
                    } 

                    boolean isNegated = false;
                    String predicateName;
                    //check if predicate negated
                    if(predicate.charAt(0) == '!') {
                        predicateName = predicate.substring(1,parameterStartIndex);
                        isNegated = true;
                    } else {
                        predicateName = predicate.substring(0,parameterStartIndex); 
                    }

                    ArrayList<Term> predicateTerms = new ArrayList<>();
                    if(parameterStartIndex != predicate.length()) {
                        //get all predicate terms
                        String[] terms = predicate.substring(parameterStartIndex+1,parameterEndIndex).split(",");
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
                    }
                    predicates.add(new Predicate(predicateName,isNegated,predicateTerms));

                }

                myKnowledgeBase.add(new Clause(predicates));
                
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(-1);
        }

        //System.out.println(myKnowledgeBase);
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

    /**
     * Representation on a Knowledge Base
     */
    public KB() {
        clauses = new ArrayList<>();
        contradiction = false;
        proved = false;
    }

    
    @Override
    public String toString() {
        int count = 1;
        String ret = "";
        for(Clause c : clauses) {
            ret+= "#" + count + " " + c.toString();
            count++;
        }
        return ret + "\n";
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
        proved = true;
        for(int i = 0; i < clauses.size(); i++) {
            for(int j = 0; j < clauses.size(); j++) {
                Clause newClause;
                if((newClause = clauses.get(i).resolve(clauses.get(j))) != null) {
                    if(newClause.getPredicates().isEmpty()) { //is this the empty clause
                        contradiction = true;
                        return;
                    }
                    //System.out.println(newClause);
                    clauses.add(newClause);
                }
            }
        }
    }

    /**
     * Check if proven and no contradiction
     * @return boolean if holds
     */
    public boolean holds() {
        return !contradiction && proved;
    }
}

class Clause {
    private static HashSet<Clause> visited = new HashSet<>();
    private ArrayList<Predicate> predicates;
    private HashMap<Term,Term> substitutions = new HashMap<>();

    @Override
    public String toString() {
        String ret = """
                     Clause:
                     """;
        for(Predicate p : predicates) {
            ret+= "\t" + p.toString() + "\n";
        }

        return ret + "\n";
    }

    /**
     * Creates a clause from a predicate ArrayList. maps all predicates to this clause.
     * @param predicates pred Arraylist
     */
    public Clause(ArrayList<Predicate> predicates) {
        this.predicates = predicates;
        for(Predicate p : this.predicates) { //mark each predicate with this clause as parent
            p.setAsParent(this);
        }
        visited.add(this);
    }

     /**
     *  Creates a clause from a clause and maps all predicates in predicate list to this clause.
     * @param Clause clause
     */
    public Clause(Clause clause) {
        //deep copy my predicate objects
        ArrayList<Predicate> predArr = clause.getPredicates();
        ArrayList<Predicate> predArrDeepCopy = new ArrayList<>();
        for(Predicate p : predArr) {
            predArrDeepCopy.add(new Predicate(p,this));
        }
        this.predicates = predArrDeepCopy;
    }

    /**
     * Given two clauses resolve as many predicates as possible.
     * @param other
     * @return a new clause made from the two clauses, null if it fails or is a duplicate
     */
    public Clause resolve(Clause other) {
        this.clearSubstitutions();
        other.clearSubstitutions();
        ArrayList<Predicate> otherPredicates = other.getPredicates();
        ArrayList<Predicate> newPredicateArraylist = new ArrayList<>();
        ArrayList<Predicate> leftoversList;
        ArrayList<Predicate> shortList;
        int thisLength = predicates.size();
        int otherLength = otherPredicates.size();
        boolean thisLonger = thisLength > otherLength;
        int shorter;
        int longer;

        if(thisLonger) {
            shorter = otherLength;
            longer = thisLength;
            leftoversList = predicates;
            shortList = otherPredicates;
        } else {
            shorter = thisLength;
            longer = otherLength;
            leftoversList = otherPredicates;
            shortList = predicates;
        }

        boolean copyOver = false;
        for(int i = 0; i < shorter; i++) {
            if(copyOver) { //we already found a resolution. Now just copy everything from both.
                newPredicateArraylist.add(new Predicate(shortList.get(i),substitutions)); 
            } else if(predicates.get(i).resolve(otherPredicates.get(i))) { //if two predicates resolve (automatically saves needed subsitutions)
                copyOver = true;
            } else {
                return null; //something doesn't resolve or unify.
            }
        }

        for(int i = shorter; i < longer; i++) {
            newPredicateArraylist.add(new Predicate(leftoversList.get(i),substitutions)); //add remaining predicates with substitutions defined earlier
        }   
        
        return new Clause(newPredicateArraylist);
    }
    
    /**
     * Get predicate arraylist
     * @return Predicates
     */
    public ArrayList<Predicate> getPredicates() {
        return predicates;
    }

    
    /**
     * add a substitution. 
     * @param substitution in the form Term[]{key,value}
     */
    public void AddSubstitution(Term[] substitution) {
        substitutions.put(substitution[0],substitution[1]);
    }
    
    /**
     * add a substitution. 
     * @param substitution in the form Term[]{key,value}
     */
    public Term getSubstitution(Term t) {
        return substitutions.get(t);
    }

    public void clearSubstitutions() {
        substitutions.clear();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predicates == null) ? 0 : predicates.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Clause other = (Clause) obj;
        if (predicates == null) {
            if (other.predicates != null)
                return false;
        } else if (!predicates.equals(other.predicates))
            return false;
        return true;
    }

}

class Predicate {

    private static int predicateCount = 0; //number of predicates 

    //keeps track of all name >> id pairs.
    //Two predicates must share BOTH a name and id if they share either.
    //These two predicates are the same.
    private static HashMap<String,Integer> nameIdPairs = new HashMap<>();

    private final ArrayList<Term> terms; //the terms for the predicate

    private final int id; //id indicating a certain predicate.
                    //Multiple instnace of that predicate will share id.
                    //Two predicates with same name will share id

    private boolean isNegated; //negation state of predicate

    private final String name; //String name 

    private Clause parentClause;
    
     /**
     * Deep clones a predicate and sets parent clause to c
     * @param p predicate to copy
     * @param c parent clause
     */
    public Predicate(Predicate p, Clause c) {
        this.id = p.getId();
        this.name = p.getName();
        this.isNegated = p.isNegated();
        this.terms = new ArrayList<>();
        for(Term t : p.getTerms()) {
            this.terms.add(Term.deepCopy(t));
        }
    }

     /**
     * Deep clones a predicate and remaps its terms using a substituion map. Does not set parent clause.
     * @param p predicate to copy
     * @param substitutions to apply
     */
    public Predicate(Predicate p, HashMap<Term,Term> substitutions) {
        this.id = p.getId();
        this.name = p.getName();
        this.isNegated = p.isNegated();
        this.terms = new ArrayList<>();
        for(Term t : p.getTerms()) {
            if(substitutions.get(t) != null) {
                t = substitutions.get(t);
            }
           this.terms.add(Term.deepCopy(t));
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
        this.name = name;
        this.isNegated = isNegated;
        this.terms = terms;
        if((lid = nameIdPairs.get(name)) != null) { //already have a predicate and id
            this.id = lid;
        } else { //new predicate so we should assign id and incr count
            this.id = predicateCount;
            //add to hashmap and increment id counter
            nameIdPairs.put(this.name,this.id);
            predicateCount++;
        }
    }

    /**
     * To String override for debugging purposes
     */
    @Override
    public String toString() {
        String predicate = name + "<" + id + ", " + !isNegated + ">\n" ;
        String terStr = "";
        for(Term t : this.terms) {
            terStr+="\t" + t + "\n";
        }

        return predicate + terStr;
    }
    
    /**
     * Checks if two predicates can resolve and saves neccesary subsitutions to parent clause.
     * @param other predicate to resolve with
     * @return true or false based on if resolution succeeded
     */
    public boolean resolve(Predicate other) {
        if(this.isInverse(other)) { //check predicates are inverses (same name and opposite polarity)
            int thisTermListSize = terms.size();
            int otherTermListSize = other.getTerms().size();
            if(thisTermListSize != otherTermListSize) {
                return false; //different arity. Can't unify.
            }

            for(int i = 0; i < thisTermListSize; i++) {
                Term substitution; //empty transfer var

                //uses term or its substituion if avaliable
                Term thisTerm = Term.deepCopy(this.terms.get(i));

                if((substitution = parentClause.getSubstitution(thisTerm)) != null) {
                    thisTerm = Term.deepCopy(substitution);
                }

                Term otherTerm = Term.deepCopy(other.getTerms().get(i));

                if((substitution = parentClause.getSubstitution(otherTerm)) != null) {
                    otherTerm = Term.deepCopy(substitution);
                }

                
                Term[] result = thisTerm.unify(otherTerm); //unify two terms
                if(result == null) {
                    return false; //two terms do not unify. Return false.
                } else {
                    //System.out.println("Resolve");
                    parentClause.AddSubstitution(result);
                    other.getParent().AddSubstitution(result);
                }
            }

            return true;
        }

        return false; //not inverses. You can't unify.
    }

    /**
     * Retrieves parent clause
     * @@return clause c
     */
    public Clause getParent() {
        return parentClause;
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
     * Sets parent clause
     * @param clause c
     */
    public void setAsParent(Clause c) {
        parentClause = c;
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
     * Gets the String name of Predicate
     * @return name
     */
    private String getName() {
        return name;
    }

    /**
     * Gets term list
     * @return terms
     */
    private ArrayList<Term> getTerms() {
        return terms;
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



    //stubs for double dispatch
    public abstract Term[] unify(Term otherParam);
    public abstract Term[] unify(Var v);
    public abstract Term[] unify(Func f);
    public abstract Term[] unify(Const c);

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
    /**
     * Performs a safe deepcopy with respect to the subclass of the original term.
     * @param thisTerm original term
     * @return a new deepcopied term
     */
    public static Term deepCopy(Term thisTerm) {
        switch (thisTerm) { //deepcopy Term while retaining subclass.
            case Func func -> thisTerm = new Func(func);
            case Const cnst -> thisTerm = new Const(cnst.getName());
            case Var var -> thisTerm = new Var(var.getName());
            default -> throw new Error("Unexpected generic term.");
        }
        return thisTerm;
    }


    @Override
    public String toString() {
        return "<" + name + ", " + id + ">";
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

}

class Const extends Term {
    
    
    /**
     * Basic constructor. Simply uses parent.
     * @param name The name of the term.
     */
    public Const(String name) {
        super(name);
    }

    /**
     * Double dispatch function, maps to speciric unify override
     * @param other another const
     * @return Return unification.
     */
    @Override
    public Term[] unify(Term other) {
        return other.unify(this);
    }

    /**
     * Unify a const and a const. 
     * @param other another const
     * @return Return a redundant mapping if same (skip) otherwise null cause these never unify.
     */
    @Override
    public Term[] unify(Const other) {
        if(this.equals(other)) {
            return new Term[]{this,this}; //same constant, map to itself.
        }
        return null;
    }

    /**
     * Unify a const and a function
     * @param other a function
     * @return null. These never unify.
     */
    @Override
    public Term[] unify(Func other) {
        return null;
    }

    /**
     * Unify a variable and const
     * @param other variable
     * @return a mapping of the variable to the const. These always unify.
     */
    @Override
    public Term[] unify(Var other) {
        return new Term[]{other,this}; 
    }
}

class Func extends Term {
    private final Term parameter;
    
    /**
     * Create a new function term
     * @param name function name
     * @param parameter function parameter
     */
    public Func(String name, Term parameter) {
        super(name);
        this.parameter = parameter;
    }

    /**
     * Create a deepcopy new function term from another function
     * @param f Function to use
     */
    public Func(Func f) {
        super(f);
        this.parameter = Term.deepCopy(f.getParameter());
    }
    
    @Override
    public String toString() {
        return "<" + this.getName() + ", " + this.getId() + ", " + parameter + ">";
    }

     /**
     * Double dispatch function, maps to speciric unify override
     * @param other another const
     * @return Return unification.
     */
    @Override
    public Term[] unify(Term other) {
        return other.unify(this);
    }

    /**
     * Unify a function and constant
     * @param other constant
     * @return null. This never unifies.
     */
    @Override
    public Term[] unify(Const other) {
        return null;
    }

    /**
     * Tries to unify two funtions
     * @param other another functions
     * @return Term[] representing a mapping if parameters unify and same function otherwise null.
     */
    @Override
    public Term[] unify(Func other) {
        if(this.getId() != other.getId()) { //check same symbol
            return null; //different functions, will not unify
        }
    
        Term otherParam = other.getParameter();
        return otherParam.unify(this.getParameter());
    }

    /**
     * unify a function with a variable
     * @param other variable
     * @return Term[] representing a mapping if possible otherwise null.
     */
    @Override
    public Term[] unify(Var other) {
        if(!parameter.equals(other)) { //if function doesn't contain variable
            return new Term[]{other, this};
        }
        return null; //if function contains variable
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

    /**
     * Get the parameter for a function.
     * @return the parameter
     */
    public Term getParameter() {
        return this.parameter;
    }
}

class Var extends Term {

    /**
     * Basic constructor. Simply uses parent.
     * @param name The name of the term.
     */
    public Var(String name) {
        super(name);
    }

    /**
     * Double dispatch function, maps to speciric unify override
     * @param other another const
     * @return Return unification.
     */
    @Override
    public Term[] unify(Term other) {
        return other.unify(this);
    }

    /**
     * Unify a variable and constant
     * @param other a const
     * @return Always unify. Map variable to const.
     */
    @Override
    public Term[] unify(Const other) {
        return new Term[]{this,other};
    }

    /**
     * Unify a function and variable
     * @param other a function
     * @return Return mapping if and only if variable isn't in function.
     */
    @Override
    public Term[] unify(Func other) {
        if(!other.getParameter().equals(this)) { //if function doesn't contain variable
            return new Term[]{this,other};
        }
        return null;
    }

    /**
     * Unify two variables
     * @param other another variable
     * @return Always unify so return a mapping
     */
    @Override
    public Term[] unify(Var other) {
        return new Term[]{this,other};
    }

}