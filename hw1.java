import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class hw1 {

    private static HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));

    private static HashSet<WordNode> visited = new HashSet<>();
    


    public static void main(String[] args) {
        String str1 = args[1];
        String str2 = args[2];
        Queue<WordNode> queue = new LinkedList<>();
        HashSet<String> list = new HashSet<>();

        File file = new File(args[0]);
        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNext()) {
                list.add(scanner.next());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(-1);
        }


        BFS(str1, str2, list, queue);
        System.out.println("No solution");
        System.exit(0);

    }

    private static void BFS(String str1, String str2, HashSet<String> list, Queue<WordNode> queue) {
        WordNode str2WordNode = new WordNode(str2);
        queue.add(new WordNode(str1));
        while(!queue.isEmpty()) {
            WordNode current = queue.poll();
            if(current.equals(str2WordNode)) {
                System.out.println(current.backTrackThroughParents());
                System.exit(0);
            }
            for(int letterDiff = 0; letterDiff < str1.length(); letterDiff++) {
                for(char c : alphabet) {
                    WordNode modifiedString = new WordNode(current.getWord().substring(0, letterDiff) + c + current.getWord().substring(letterDiff + 1));
                    if(list.contains(modifiedString.getWord())) {
                        if(!visited.contains(modifiedString.setParent(current))) {
                            visited.add(modifiedString.setParent(current));
                            queue.add(modifiedString.setParent(current));
                        }
                    }
                }
            }
        }    
    }
}

class WordNode {
    private final String word;
    private WordNode parent;

    public WordNode(String word) {
        this.word = word;
        this.parent = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((word == null) ? 0 : word.hashCode());
        // result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    //parent not accounted in eqauls or hashcode
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordNode other = (WordNode) obj;
        if (word == null) {
            if (other.word != null)
                return false;
        } else if (!word.equals(other.word))
            return false;
        // if (parent == null) {
        //     if (other.parent != null)
        //         return false;
        // } else if (!parent.equals(other.parent))
        //     return false;
        return true;
    }

    public String getWord() {
        return word;
    }

    public WordNode getParent() {
        return parent;
    }

    public WordNode setParent(WordNode parent) {
        this.parent = parent;
        return this;
    }

    public String backTrackThroughParents() {
        if(this.parent == null) {
            return "";
        }

        ArrayList<String> ret = new ArrayList<>();
        ret.add(this.word + "\n");

        WordNode currentNode = this.parent;
        while(true) {
            ret.add(currentNode.getWord() + "\n");
            currentNode = currentNode.getParent();
            if(currentNode == null) {
                break;
            }
        }
        
        Collections.reverse(ret);
        String retStr = "";
        for(String x : ret) {
            retStr += x;
        }
        return retStr;
    }
}
