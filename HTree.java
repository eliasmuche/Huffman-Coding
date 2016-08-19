import java.util.*;
import java.io.*;
import javax.swing.*;
/**
 * This class demonstrates Huffman Coding. The user enters a file and a unique code is created for each distinct character. 
 * Once the codes are created, the original message is encoded using the codes for each character. Once the message is encoded, it can be decoded using the Huffman tree.
 * @author Elias Muche 
 * @version 12/1/15
 */
public class HTree  
{
    private PriorityQueue<HNode> q;//will store the nodes temporarily to construct the HTree
    private HashMap<Character,HNode> map;//will store the characters and associated HNodes 
    private HNode overallRoot;//the tree itself
    private Collection<HNode> nodes;//Will the store the HNodes containing characters 
    private String encode="";//The encoded message
    private String original="";//The original message
    
    /**
     * This method runs the user interface (choosing a file and displaying the data that was pulled from the file).
     * @param args Not used
     * @throws FileNotFoundException If there was no such file
     */
    public static void main(String[] args)throws FileNotFoundException{
        JFileChooser chooser = new JFileChooser();
        int dialogBox=chooser.showOpenDialog(null);
        if(dialogBox==chooser.APPROVE_OPTION){//if the user clicked didnt click on the cancel button
            File f=chooser.getSelectedFile(); //get the file 
            HTree tree=new HTree();
            tree.encode(f);//create a tree and encode based off of the contents of the file 
            Iterator it=tree.nodes.iterator();//to dipslay the character-frequency table
            System.out.println("Original message:"+tree.original);//display the original contents of the file
            System.out.println("\n\n");
            while(it.hasNext()){
                HNode n=(HNode)it.next();
                if(n.data==' '){//if the character is a space character 
                    System.out.println("Char:<sp>"+"  "+"Frequency:"+n.frequency);//a way to represent a space character 
                }
                else if(n.data=='\n'){//if the character is a new line character
                    System.out.println("Char:<nl>"+"  "+"Frequency:"+n.frequency);//a way to represent a new line character 
                }
                else{
                    System.out.println("Char:"+n.data+"  "+"Frequency:"+n.frequency);//display each character and its code
                }
            }
            System.out.println("\n\n");
            System.out.println(tree);//display the tree
            Iterator it2=tree.nodes.iterator();//to dispaly the character-code table
            System.out.println("\n\n");
            while(it2.hasNext()){
                HNode n=(HNode)it2.next();
                if(n.data==' '){
                    System.out.println("Char:<sp>"+"  "+"Code:"+n.code);//display each character and its code
                }
                else if(n.data=='\n'){
                    System.out.println("Char:<nl>"+"  "+"Code:"+n.code);//display each character and its code
                }
                else{
                    System.out.println("Char:"+n.data+"  "+"Code:"+n.code);//display each character and its code
                }
            }
            System.out.println("\n\n");
            System.out.println("Encoded message:"+tree.encode);//display the encoded version of the original message
            System.out.println("\n\n");
            System.out.println("Decoded message:\n"+tree.decode(tree.encode));//display the decoded version of the message(should be the same as the original message)
            System.out.println("\n\n");
            double  average=tree.getAverage();//calculate the average number of bits used per symbol
            //System.out.println("Average # of bits used per character:"+average);//display it
            System.out.println("Average # of bits used per character:");//display it
            System.out.format("%.2f",average);
        }
    }

    /**
     * This method will calculate the average number of bits used per symbol
     * @return The average number of bits used per symbol
     */
    public double getAverage(){
        Iterator it=nodes.iterator();
        double sum=0;//accumulator 
        while(it.hasNext()){
            HNode n=(HNode) it.next();//pointer
            sum+=n.code.length()*n.frequency;//add the number of bits for that character

        }
        double average=sum/original.length();
        return average;
    }


    /**
     * This method is a kickoff method that will call other methods (making a map and constructing the tree) in this class.
     * The method then encodes the message.
     * @param f The file containing the message
     * @throws FileNotFoundException If there was no such file
     */
    public void encode(File f)throws FileNotFoundException{
        makeAMap(f);//the first step is to make a map that contains each character and its associated HNode
        makeATree();//the next step is to make a tree that contains the characters as leaf nodes
        getCharCodes(overallRoot, "");//generate the codes for each character
        Scanner s=new Scanner(f);//to read from the file 
        String line;//will store each line of the file
        char[] chars;//will contain each character in a given line
        while(s.hasNextLine()){
            line=s.nextLine();//store a line
            original+=line;//save this part of the message
            chars=line.toCharArray();//to be able to examine each character
            for(int i=0;i<chars.length;i++){
                encode+=map.get(chars[i]).code;//get the encoded version of the character and save it
            }
            if(s.hasNextLine()){
                original+="\n";
                encode+=map.get('\n').code;//adding back the newline character if necessary 
            }
        }
    }

    private void getCharCodes(HNode n, String code){
        if(n.left==null&&n.right==null){//if the current node is a leaf
            n.code=code;//record its code (was originally n.code)
            return;
        }
        getCharCodes(n.right,code+"1");//go left mark the path with a 0
        getCharCodes(n.left,code+"0");//go right, mark the path with a 1
    }

    /**
     * This method will read from a file and create a map with the characters as keys and HNodes as values.
     * If an HNode already exists, the frequency is incremented.
     * @param f The file containing the message
     * @throws FileNotFoundException
     */
    public void makeAMap(File f)throws FileNotFoundException{
        map=new HashMap<Character, HNode>();//initialize the map 
        Scanner s=new Scanner(f);//to read from the file
        String line="";//will store each line
        char[] chars;//will store each character of a given line
        HNode n=null;
        while(s.hasNextLine()){//while there are more lines 
            line=s.nextLine();//store a line
            chars=line.toCharArray();//store each character of the current line
            for(int i=0;i<chars.length;i++){//for each line 
                if(map.containsKey(chars[i])){//if its already in the map increment the frequency of the associated HNode
                    map.get(chars[i]).frequency+=1;

                }
                else{//if the current character isnt in the map, store it.
                    n=new HNode(chars[i],1,null,null);
                    map.put(chars[i],n);
                }
            }
            if(s.hasNextLine()){//adding back the newline character when necessary
                if(map.containsKey('\n')){//if the newline character is already in the map, increment the frequency of the associated HNode 
                    map.get('\n').frequency+=1;

                }
                else{//otherwise store it
                    n=new HNode('\n',1,null,null);
                    map.put('\n',n);
                }
            }
        }
    }

    /**
     * This method will construct a Huffman tree that will contain HNodes with characters(only the leaves have characters).
     */
    public void makeATree(){
        //The code below will add leaves containing characters and associated frequencies
        nodes=map.values();//get a collection of the values
        Iterator it=nodes.iterator();
        q=new PriorityQueue<HNode>();//initialize 

        while(it.hasNext()){
            q.add((HNode)it.next());//add the leaves 
        }
        HNode parent=null;
        HNode left=null;
        HNode right=null;
        while(q.size()>1){//while two nodes can still be taken out
            left=q.remove();//remove the lowest 
            right=q.remove();//remove the second lowest
            parent=new HNode(null,left.frequency+right.frequency,left,right);//create a new node containing the two removed nodes as its left and right
            q.add(parent);//add the newly created node to the 
        }//when the loop ends there should be one node left in the priority queue that contains the tree
        overallRoot=q.remove();//store the tree
    }

    /**
     * This method is a kickoff method that will decode an encoded message.
     * @param code The encoded message
     * @return The actual message
     */
    public String decode(String code){
        HNode pointer=overallRoot;//create a pointer
        String originalMessage="";//initialize 
        for(int i=0;i<code.length();i++){//for the length of the encoded message
            if(pointer.left==null&&pointer.right==null){//if a leaf is reached 
                originalMessage+=pointer.data;//add the associated character to the decoded message
                pointer=overallRoot;//reset the pointer
            }

            if(code.charAt(i)=='0'){//it the current part of the encoded message is a 0
                pointer=pointer.left;//go left
            }
            else if(code.charAt(i)=='1'){//if its a 1 
                pointer=pointer.right;//go right
            }
        }
        originalMessage+=pointer.data;//adds the last bit of the original message(because the loop test fails when its reached)
        return originalMessage;
    }

    /**
     * This method will provide a string representation of a HuffmanTree.
     * @return The represented as a string.
     */
    public String toString(){
        String tree= "node("+overallRoot.frequency+")\n";//initialize 
        tree+="\t"+toString(overallRoot.left,2)+"\n\t"+toString(overallRoot.right,2)+"\n";//call the recursive method
        return tree;
    }

    private String toString(HNode n,int numTabs){
        String branch="node(";//formatting
        if(n==null){
            return"";//do nothing if the HNode is non existent 
        }
        else if(n.left==null&&n.right==null){//if a leaf is reached 
            if(n.data==' '){//if the character is a space 
                branch+=n.frequency+")'"+"<sp>"+"'\n";//attach its character to the frequency 
             }
            else if(n.data=='\n'){
                branch+=n.frequency+")'"+"<nl>"+"'\n";//attach its character to the frequency 
            }
            else{
                branch+=n.frequency+")'"+n.data+"'\n";//attach its character to the frequency 
            }
        }
        else{//otherwise 
            branch+=n.frequency+")"+"\n";//attach the frequency 
        }
        for(int i=numTabs;i>0;i--){
            branch+="\t";//appropriate amount of spacing
        }
        branch+=toString(n.left,numTabs+1);//attach the left subtree
        for(int i=numTabs;i>0;i--){
            branch+="\t";//appropriate amount of spacing
        }
        branch+=toString(n.right,numTabs+1)+"\n";//attach the right subtree
        return branch;
    }
    class HNode implements Comparable{
        private Character data;//the character 
        private Integer frequency=0;//its associated frequency
        private HNode left,right;//left and right subtree(or possibly leaves)
        private String code;//The code associated with the character
        /**
         * This constructor initializes the fields of an HNode.
         * @param data The character associated with the HNode
         * @param frequency How Frequent the character shows up in a message
         * @param left The left child of the HNode
         * @param right The right child of the HNode
         */
        public HNode(Character data,Integer frequency,HNode left,HNode right){
            this.data=data;//intialize 
            this.frequency=frequency;//intialize 
            this.left=left;//intialize 
            this.right=right;//intitialize
        }

        /**
         * This method compares two Nodes.
         * @param o The object that will be compared to the current object.
         * @return 1 if the parameter is less than the current object, 0 if both objects are equal,
         * and -1 if the parameter is greater than the current object.
         */
        public int compareTo(Object o){
            if(!(o instanceof HNode)){//if the object isnt an HNode
                throw new IllegalArgumentException("It wasn't a node.");
            }
            HNode node=(HNode)o;//make it an HNode because the prevoius if statement failed
            if(node.frequency>frequency){//if the parameter's frequency is greater than the current node's frequency
                return -1;
            }
            else if(node.frequency<frequency){//if the parameter's frequency is less than the current node's frequency
                return 1;
            }
            return 0;//they're equal at this point so return 0;
        }
    }
}


