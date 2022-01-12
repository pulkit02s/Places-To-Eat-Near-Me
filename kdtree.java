import java.util.*;
import java.io.*;
class Pair<A, B>{
	public A First;
	public B Second;
	public Pair(){}
	public Pair(A _first, B _second) {
        this.First = _first;
        this.Second = _second;
    }
    public A get_first() {
        return First;
    }
    public B get_second() {
        return Second;
    }
    public static void insortx(List<Pair<Integer,Integer>> lst) {
        for(int i=0; i<lst.size(); i++) {
            int j = i;
            while(j >= 1 && lst.get(j).First < lst.get(j-1).First){
                Collections.swap(lst, j, j-1);
                j--;
            }
        }
    }
    public static void insorty(List<Pair<Integer,Integer>> lst) {
        for(int i=0; i<lst.size(); i++) {
            int j = i;
            while(j >= 1 && lst.get(j).Second < lst.get(j-1).Second){
                Collections.swap(lst, j, j-1);
                j--;
            }
        }
    }
}

class kdtreeNode{
    public kdtreeNode left;
    public kdtreeNode right;
    public kdtreeNode parent;
    public Pair<Integer,Integer> xRange;
    public Pair<Integer,Integer> yRange;
    public Pair<Integer,Integer> val;
    public int numberLeaves;
    public int depth;
}

public class kdtree{
    kdtreeNode rootnode;

    public void printTree(kdtreeNode node){
        if(node.right == null && node.left == null){
            System.out.print("(" + node.val.First + "," + node.val.Second + ")  ");
            return;
        }
        printTree(node.left);
        printTree(node.right);
    }
    public void Build(List<Pair<Integer,Integer>> lst, kdtreeNode node) {
        node.numberLeaves = lst.size();
        if(node.parent != null){
            node.depth = node.parent.depth + 1;
        }
        else {
            node.depth = 0;
            node.xRange = new Pair<Integer, Integer>(Integer.MIN_VALUE ,Integer.MAX_VALUE);
            node.yRange = new Pair<Integer, Integer>(Integer.MIN_VALUE ,Integer.MAX_VALUE);
        }
        
        if(node.depth%2 == 0) {
            //sort list of restaurants according to x
            Pair.insortx(lst);
            if(node.parent != null) {
                node.xRange = node.parent.xRange;
                if(node.parent.left == node) {
                    node.yRange = new Pair<Integer, Integer>(node.parent.yRange.First, node.parent.val.Second);
                }
                else {
                    node.yRange = new Pair<Integer, Integer>(node.parent.val.Second, node.parent.yRange.Second);
                }
            }
        }
        else {
            //sort list of restaurants according to y
            Pair.insorty(lst);
            node.yRange = node.parent.yRange;
            if(node.parent.left == node) {
                node.xRange = new Pair<Integer, Integer>(node.parent.xRange.First, node.parent.val.First);
            }
            else {
                node.xRange = new Pair<Integer, Integer>(node.parent.val.First, node.parent.xRange.Second);
            }
        }
        if(lst.size() == 1) {
            node.val = lst.get(0);
            // System.out.println(node.val.First + "," + node.val.Second);
            return;
        }
        //NOW lst IS SORTED (according to..)
        List<Pair<Integer,Integer>> newlst1 = new ArrayList<Pair<Integer,Integer>> ();
		List<Pair<Integer,Integer>> newlst2 = new ArrayList<Pair<Integer,Integer>> ();
        if(lst.size() % 2 == 0){
        //if length of list is even take 1 to length/2 in newlst1 and rest in newlst2 (equal)
		    for(int i=0; i<lst.size()/2; i++)
			    newlst1.add(new Pair <Integer,Integer> (lst.get(i).First, lst.get(i).Second));
		    for(int i=lst.size()/2; i<lst.size(); i++)
			    newlst2.add(new Pair <Integer,Integer> (lst.get(i).First, lst.get(i).Second));
        }
        else {
        //else take 1 to (length+1)/2 in newlst1 and rest in newlst2
		    for(int i=0; i<=lst.size()/2; i++)
			    newlst1.add(new Pair <Integer,Integer> (lst.get(i).First, lst.get(i).Second));
		    for(int i=1+lst.size()/2; i<lst.size(); i++)
			    newlst2.add(new Pair <Integer,Integer> (lst.get(i).First, lst.get(i).Second));
        }
        node.val = newlst1.get(newlst1.size()-1);
        kdtreeNode l = new kdtreeNode(); kdtreeNode r = new kdtreeNode();
        node.left = l;  node.right = r;
        l.parent = r.parent = node;
        Build(newlst1, l);
        Build(newlst2, r);
    }
    public int query(Pair<Integer,Integer> coord, kdtreeNode node) {
        int count = 0;

        Integer x1 = coord.First-100;  Integer x2 = coord.First+100;
        Integer y1 = coord.Second-100; Integer y2 = coord.Second+100;
        if(node.right == null && node.left == null){
            if(node.val.First >= x1 && node.val.First <= x2 && node.val.Second >= y1 && node.val.Second <= y2){
                count += 1;
            }
            return count;
        }
        Integer x3, x4, x5, x6, y3, y4, y5, y6;
        x3 = node.left.xRange.First; x4 = node.left.xRange.Second;
        y3 = node.left.yRange.First; y4 = node.left.yRange.Second;
        x5 = Math.max(x1, x3);
        y5 = Math.max(y1, y3);
        x6 = Math.min(x2, x4);
        y6 = Math.min(y2, y4);
        if(x3 == x5 && y3 == y5 && x4 == x6 && y4 == y6){
            //range fully contained in R
            count += node.left.numberLeaves;
        }
        else if(x5 > x6 || y5 > y6){
            //no intersection, discard
        }
        else {
            count += query(coord, node.left);
        }
        x3 = node.right.xRange.First; x4 = node.right.xRange.Second;
        y3 = node.right.yRange.First; y4 = node.right.yRange.Second;
        x5 = Math.max(x1, x3);
        y5 = Math.max(y1, y3);
        x6 = Math.min(x2, x4);
        y6 = Math.min(y2, y4);
        if(x3 == x5 && y3 == y5 && x4 == x6 && y4 == y6){
            //range fully contained in R
            count += node.right.numberLeaves;
        }
        else if(x5 > x6 || y5 > y6){
            //no intersection, discard
        }
        else {
            count += query(coord, node.right);
        }
        return count;
    }

    public static void main(String[] args) {
        List<Pair<Integer, Integer>> lst = new ArrayList<Pair<Integer, Integer>>();
        try{
            FileInputStream fs1 = new FileInputStream("restaurants.txt");
            Scanner s = new Scanner (fs1);
            s.nextLine();
            while(s.hasNextLine()){
                String[] pair = s.nextLine().split(","); 
                lst.add(new Pair<Integer, Integer>(Integer.valueOf(pair[0]), Integer.valueOf(pair[1])));
            }
        } catch(Exception e){}

        kdtree tree = new kdtree();
        tree.rootnode = new kdtreeNode(); //needed?? YESSS
        tree.Build(lst, tree.rootnode);
        List<Pair<Integer, Integer>> list = new ArrayList<Pair<Integer, Integer>>();
        try{
            FileInputStream fs2 = new FileInputStream("queries.txt");
            Scanner s = new Scanner (fs2);
            s.nextLine();
            FileOutputStream fs = new FileOutputStream("output.txt");
            PrintStream p = new PrintStream (fs);
            while(s.hasNextLine()){
                String[] pair = s.nextLine().split(","); 
                // System.out.println(tree.query(new Pair<Integer, Integer>(Integer.valueOf(pair[0]), Integer.valueOf(pair[1])),  tree.rootnode));
                p.println(tree.query(new Pair<Integer, Integer>(Integer.valueOf(pair[0]), Integer.valueOf(pair[1])),  tree.rootnode));
            }
        } catch(Exception e){}
    }
}