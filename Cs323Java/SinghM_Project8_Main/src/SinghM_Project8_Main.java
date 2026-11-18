import java.io.*;

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
                if(checkAncestoers(newNode) == false) {
                    newNode.next = tmpList.next;
                    tmpList.next = newNode;
                }
            }
        }

        logFile.write("Leaving expandChildList method and printing tmpList\n");
        printList(tmpList, logFile);
        return tmpList;
    }
}

public class SinghM_Project8_Main {
    public static void main(String[] args) {

    }
}