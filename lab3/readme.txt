Program overview:

My program is a heavily object oriented implementation of HW3 and LAB3. The LAB3 functions follow the expected syntax.
My trees are saved as serialized "sessions." A session is either an instance of the adBoost.java class or dtLearn.java class.
A loadable session contains an instance of a decisionTree (dtLearn) or ArrayList of adastumps (adaboost). 
    -The best tree is inside the output_files subfolder labeled "best" 
    -My best features are inside output_files labeled "features.txt"
    -My testing file is inside test_files labeled "train.dat"
    -My training data is inside input_files lagbeled "test.dat"

The behavior of the algorithms can be tweaked by two constants defined at the top of lab3.java:
    -dtLearnDepth -> defines the depth of a dt tree 
    -numberOfStumps -> defines the number of stumps in an adaboost session.

For debugging and evaluation there is also a constant TEST_SET_SIZE towards the bottom of lab3.
The print statements for this and printing the tree toString are commented out.

Files are broken down as such:
    -lab3.java:
        data read in, IO handling, and serialization.
        Contains functions for HW3, predicting, and training.
    
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
For example, even though "is" represents a very common word in both languages, I would not choose it for either since 
it's language association is ambigious. 
This was also true of things that in dutch have a meaning as English slang or abbreviation.
My number of features were initially set to 15 dutch and 15 of the most common English words.

Way back when I created HW3 in this project I made a toString to visualize a tree by listing a node and it's neighbors.
Using this and by looking at specific examples I added additional classifiers when things that shouldn't be grouped toegther were.

https://www.ef.edu/english-resources/english-vocabulary/top-1000-words/
https://languagebard.com/most-common-100-dutch-words

Training Procedure:

I used random wikipedia article finders to get a mix of dutch and English sentences which I classified and used for training.
I tried to have a variety of topics while avoiding topics which frequently use words from other languages.
I started with a training set of 50 english and 50 dutch examples and then evaluated the resulting tree performance.
I had a number of lines less then 15 words (and a couple a little over) for both training and validation. 
The logic here was that if my tree can classify these then it will have no problem classifying longer sentences.
This number was then raised to 75 data peices for each to determine if this would lead to more accuracy.

Tree evaluation/building procedure:

My trees were evaluated based on a % classifiation accuracy on my test set.
This test set was 50 English phrases and 50 dutch phrases.
For adaboost attempts ensemble was set to 1000 initially dt depth was set to 50 and then tweaked accordingly.
While still overkill, this was much more reasonable.

Progression:

Initial:

DT accuracy: 97% on my 100 phrase dataset
    -But only 9 branches actually contributed useful data. So I trimmed the tree to depth = 9;
ADA accuracy: 98% accuracy on my 100 phrase dataset.
    -I attempted to go up to 10000 stumps and it did not improve accuracy.
    -Dropping to 100 stumps also had no effect, but I figured 1000 can't hurt in this case.

Accuracy after increasing training set to 75 english and dutch:

DT accuracy: 97% on my 100 phrase dataset 
ADA accuracy: 98% accuracy on my 100 phrase dataset.


