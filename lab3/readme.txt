Program overview:

My program is a heavily object oriented implementation of HW3 and LAB3. The LAB3 functions follow the expected syntax.
My trees are saved as serialized "sessions." A session is either an instance of the adBoost.java class or dtLearn.java class.
A loadable session contains an instance of a decisionTree (dtLearn) or ArrayList of adastumps (adaboost).

The behavior of the algorithms can be tweaked by two constants defined at the top of lab3.java:
    -dtLearnDepth -> defines the depth of a dt tree 
    -numberOfStumps -> defines the number of stumps in an adaboost session.

Files are broken down as such:
    -lab3.java:
        data read in, IO handling, and serialization.
    
    -predictable.java
        an interface that just defines that a session has a predict function (dtlearn and adaboost)

        -dtLearn.java IMPLEMENTS predictable
            an instance of a dtLearning session.
            Contains a decision tree and the functions to train and predict
            HAS A decisionTree

        -adaBoost.java IMPLEMENTS predictable
            an instance of an adaboost session.
            Contains an araylist of adastumps

        
    -decisiontree.java
        Contains logic code, a root node, and other helpful tree related functionality.

        -adastump.java EXTENDS decisiontree.java
            Adds weight to a decisionTree

    -node.java 
        Has neighbors and is the foundational component of a decisionTree.
        Node also contains everything it needs to split itself.
        Each node has a value (either a question or label if leaf).
        Every node has a unique ID (only used for toString).

    -observation.java:
        a single example to be classified (unlabeled) or train on (labeled)
        Contains the attribute class defining a single attribute within an observation.

        -CONTAINS attribute class 
            defines a single attribute and stores its value assignment


Approach to feature selection:

To decide the words I'd use I looked over the most commonly occuring words in both English and Dutch.
Crucially, I made sure that any word I selected as a "common English" or "common dutch word" did not appear in both.
For example, even though "is" represents a very common word in both languages I would not choose it for either since 
it's language association is ambigious. 

https://www.ef.edu/english-resources/english-vocabulary/top-1000-words/
https://languagebard.com/most-common-100-dutch-words

Training Procedure:

Tree evaluation procedure:


