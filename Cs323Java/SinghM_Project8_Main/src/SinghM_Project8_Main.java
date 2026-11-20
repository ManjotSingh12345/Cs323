import java.io.*;
import java.util.*;

class AStarNode {
    int[] config;
    int gStar;
    int hStar;
    int fStar;
    AStarNode next;
    AStarNode parent;

    public AStarNode(int[] config, int gStar, int hStar, int fStar, AStarNode next, AStarNode parent) {
        this.config = config;
        this.gStar = gStar;
        this.hStar = hStar;
        this.fStar = fStar;
        this.next = next;
        this.parent = parent;
    }

    public void printNode(AStarNode node, BufferedWriter outFile) throws IOException{
        outFile.write("<" + node.fStar + " [");
        for(int i=0;i<9;i++) {
            outFile.write(node.config[i] + " ");
        }
        outFile.write("] :: " + node.parent.fStar + " [");
        for(int i=0;i<9;i++) {
            outFile.write(node.parent.config[i] + " ");
        }
        outFile.write("]>");
    }
}

class AStar {
    AStarNode startNode;
    AStarNode Open;
    AStarNode Close;
    AStarNode childList;
    int[][] table = {{0,1,2,1,2,3,2,3,4},{1,0,1,2,1,2,3,2,3},
            {2,1,0,3,2,1,4,3,2},{1,2,3,0,1,2,1,2,3},
            {2,1,2,1,0,1,2,1,2},{3,2,1,2,1,0,3,2,1},
            {2,3,4,1,2,3,0,1,2},{3,2,3,2,1,2,1,0,1},
            {4,3,2,3,2,1,2,1,0}};
    int[] initConfig = new int[9];
    int[] goalConfig = new int[9];
    int[] dummyConfig = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

    public AStar() {

    }

    public int computeHStar(int[] nodeConfig, int[] goalConfig, BufferedWriter logFile) throws IOException{
        logFile.write("**** Entering computeHstar method\n");
        int sum = 0;
        for(int i=0; i<9; i++) {
            int p1 = nodeConfig[i];
            for(int j=0; j<9; j++) {
                if (goalConfig[j] == p1) {
                    sum += table[i][j];
                    break;
                }
            }
        }

        logFile.write("**** Leaving computeHstar method: sum = " + sum + "\n");
        return sum;
    }

    public AStarNode expandChildList(AStarNode currentNode, BufferedWriter logFile) throws IOException {
        logFile.write("*** Entering expandChildList method\n");
        logFile.write("** Printing currentNode\n");
        currentNode.printNode(currentNode, logFile);
        AStarNode tmpList = new AStarNode(dummyConfig, 0, 0, 0, null, null);

        int i = 0;
        int zeroPosition;
        while(currentNode.config[i] != 0 && i<9) {
            i++;
        }

        if(i>=9) {
            logFile.write("*** Something is wrong, currentNode does not have a zero in it\n");
            return null;
        }
        else {
            zeroPosition = i;
            logFile.write("*** find the zero position in currentNode at position i = " + i + "\n");
        }

        for(int j=0; j<9; j++) {
            if(table[zeroPosition][j] == 1) {
                AStarNode newNode = new AStarNode(currentNode.config, 999, 999, 999, null, currentNode);
                newNode.config[j] = 0;
                newNode.config[zeroPosition] = currentNode.config[j];
                if(checkAncestors(newNode) == false) {
                    newNode.next = tmpList.next;
                    tmpList.next = newNode;
                }
            }
        }

        logFile.write("Leaving expandChildList method and printing tmpList\n");
        printList(tmpList, logFile);
        return tmpList;
    }

    public void OpenInsert(AStarNode node) {
        AStarNode temp = Open.next;
        while(temp.next != null && temp.next.fStar <= node.next.fStar) {
            temp = temp.next;
        }

        node.next = temp.next;
        temp.next = node;
    }

    AStarNode remove(AStarNode list) {
        if(list.next == null) {return null;}
        AStarNode temp = list.next;
        list.next = temp.next;
        temp.next = null;

        return temp;
    }

    boolean match(int[] config1, int[] config2) {
        for(int i=0; i<9; i++) {
            if(config1[i] != config2[i]) {return false;}
        }
        return true;
    }

    boolean checkAncestors(AStarNode child) {
        AStarNode temp = child.parent;
        while(temp != startNode) {
            if(match(child.config, temp.config)) {return true;}
            temp = temp.parent;
        }

        return false;
    }

    void printList(AStarNode list, BufferedWriter outFile) throws IOException{
        AStarNode temp = list.next;
        while(temp != null) {
            temp.printNode(temp, outFile);
            outFile.write(" -> ");
            temp = temp.next;
        }
        outFile.write("null\n");
    }

    void printSolution(AStarNode node, BufferedWriter outFile) throws IOException {
        if (node == null) {return;}

        printSolution(node.parent, outFile);

        for(int i=0; i<9; i++) {
            outFile.write(node.config[i] + " ");

            if((i+1) % 3 == 0) {outFile.write("\n");}
        }
        outFile.write("\n\n");
    }
}

public class SinghM_Project8_Main {
    public static void main(String[] args) throws IOException {
        if(args.length != 5) {
            System.out.println("Error, you need 2 input files, 2 output files, and a logFile");
            System.exit(1);
        }

        Scanner inFile1 = null, inFile2 = null;
        BufferedWriter outFile1 = null, outFile2=  null, logFile = null;

        try{
            inFile1 = new Scanner(new FileReader(args[0]));
            inFile2 = new Scanner(new FileReader(args[1]));
            outFile1 = new BufferedWriter(new FileWriter(args[2]));
            outFile2 = new BufferedWriter(new FileWriter(args[3]));
            logFile = new BufferedWriter(new FileWriter(args[4]));
        } catch (FileNotFoundException e) {
            System.out.println("Error, can't open file");
            System.exit(1);
        }

        AStar star = new AStar();
        for(int i=0; i<9; i++) {
            star.initConfig[i] = inFile1.nextInt();
        }
        for(int i=0; i<9; i++) {
            star.goalConfig[i] = inFile2.nextInt();
        }
        star.startNode = new AStarNode(star.initConfig, 0, 9999, 9999, null, null);
        star.Open = new AStarNode(star.dummyConfig, 0, 0, 0, null, null);
        star.Close = new AStarNode(star.dummyConfig, 0, 0, 0, null, null);
        star.childList = star.Open = new AStarNode(star.dummyConfig, 0, 0, 0, null, null);

        star.startNode.gStar = 0;
        star.startNode.hStar = star.computeHStar(star.startNode.config, star.goalConfig, logFile);
        star.startNode.fStar = star.startNode.gStar + star.startNode.hStar;
        logFile.write("“*** Below is the startNode’s ***\n");
        star.startNode.printNode(star.startNode, logFile);
        star.OpenInsert(star.startNode);

        AStarNode currentNode = star.remove(star.Open);
        while(star.Open.next != null && !star.match(currentNode.config, star.goalConfig)) {
            logFile.write("“*** Below is the startNode’s ***\n");
            currentNode.printNode(currentNode, logFile);
            logFile.write("\n");

            if(currentNode != null)
        }
    }
}