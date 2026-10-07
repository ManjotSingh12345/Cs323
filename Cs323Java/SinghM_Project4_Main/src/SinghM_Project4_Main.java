import java.io.*;
import java.util.*;

class DijktraSSS {
    int numNodes;
    int sourceNode;
    int minNode;
    int minCost;
    int currentNode;
    int newCost;
    int[][] costMatrix;
    int[] parent;
    int[] todo;
    int[] best;

    public DijktraSSS() {

    }

    public void loadCostMatrix(Scanner inFile) {
        numNodes = inFile.nextInt();

        costMatrix = new int[numNodes][numNodes];
        for(int i=0; i<numNodes; i++) {
            for(int j=0; j<numNodes; j++) {
                if(i == j) {
                    costMatrix[i][j] = 0;
                }
                else {
                    costMatrix[i][j] = 999;
                }
            }
        }

        while(inFile.hasNextLine()) {
            int i = inFile.nextInt();
            int j = inFile.nextInt();
            costMatrix[i][j] = inFile.nextInt();
        }
    }

    public void initBest(int sourceNode) {
        this.sourceNode = sourceNode;
        best = new int[numNodes];

        for(int i=0; i<numNodes; i++) {
            best[i] = costMatrix[sourceNode][i];
        }
    }

    public void initParent(int sourceNode) {
        this.sourceNode = sourceNode;
        parent = new int[numNodes];
        for (int i=0; i< numNodes; i++) {
            parent[i] = sourceNode;
        }
    }

    public void initToDo(int sourceNode) {
        this.sourceNode = sourceNode;
        todo = new int[numNodes];
        for(int i=0; i<numNodes; i++) {
            if(i == sourceNode) {
                todo[i] = 0;
            }
            else {
                todo[i] = 1;
            }
        }
    }

    public int findMinNode(BufferedWriter logFile) throws IOException {
        logFile.write("*** Entering findMinNode () method ***\n");
        minCost = 999;
        minNode = 0;
        int index = 0;
        while(index < numNodes) {
            if(todo[index] == 1 && best[index] < minCost) {
                minCost = best[index];
                minNode = index;
            }
            index++;
        }
        logFile.write("*** Leaving findMinNode(): minNode is" + minNode + "***\n");
        return minNode;
    }

    boolean checkToDo() {
        boolean toReturn = true;
        for (int i=0; i<numNodes; i++) {
            if (todo[i] == 1) {
                toReturn = false;
            }
        }
        return toReturn;
    }

    public void printParent(BufferedWriter outFile) throws IOException{
        outFile.write("Below is Parent Array: \n");
        outFile.write("Index: ");
        for (int i=0; i<numNodes; i++) {
            outFile.write(i + " ");
        }
        outFile.write("\nParent: ");
        for (int i=0; i<numNodes; i++) {
            outFile.write(parent[i] + " ");
        }
    }

    public void printToDo(BufferedWriter outFile) throws IOException{
        outFile.write("Below is ToDo Array: \n");
        outFile.write("Index: ");
        for (int i=0; i<numNodes; i++) {
            outFile.write(i + " ");
        }
        outFile.write("\nToDo: ");
        for (int i=0; i<numNodes; i++) {
            outFile.write(todo[i] + " ");
        }
    }

    public void printBest(BufferedWriter outFile) throws IOException{
        outFile.write("Below is Best Array: \n");
        outFile.write("Index: ");
        for (int i=0; i<numNodes; i++) {
            outFile.write(i + " ");
        }
        outFile.write("\nBest: ");
        for (int i=0; i<numNodes; i++) {
            outFile.write(best[i] + " ");
        }
    }

    public void printShortestPath(int currentNode, int sourceNode, BufferedWriter SSSFile) throws IOException {
        int father = parent[currentNode];
        SSSFile.write("The path from " + sourceNode + " to " + currentNode + ": " +
                currentNode);
        while(father != sourceNode) {
            SSSFile.write(" <- " + father);
            father = parent[father];
        }

        SSSFile.write(": cost = " + best[currentNode]);
    }
}

public class SinghM_Project4_Main {
    public static void main(String[] args) throws IOException{
        if (args.length != 3) {
            System.out.println("Error, you need 1 inFile, 1 SSSFile, and 1 logFile");
            System.exit(1);
        }

        Scanner inFile = null;
        BufferedWriter SSSFile = null;
        BufferedWriter logFile = null;
        try {
            inFile = new Scanner(new FileReader(args[0]));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error, cannot open inFile");
            System.exit(1);
        }

        try {
            SSSFile = new BufferedWriter(new FileWriter(args[1]));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error, cannot open SSSFile");
            System.exit(1);
        }

        try {
            logFile = new BufferedWriter(new FileWriter(args[2]));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error, cannot open logFile");
            System.exit(1);
        }

        DijktraSSS dijktra = new DijktraSSS();
        dijktra.loadCostMatrix(inFile);
        int sourceNode = 1;
        logFile.write("“*** The Source Node = " + sourceNode + " ***\n");

        dijktra.initBest(sourceNode);
        dijktra.initParent(sourceNode);
        dijktra.initToDo(sourceNode);

        while (!dijktra.checkToDo()) {
            int minNode = dijktra.findMinNode(logFile);
            dijktra.todo[minNode] = 0;
            dijktra.printToDo(logFile);
            dijktra.printParent(logFile);
            dijktra.printBest(logFile);

            int childNode = 0;
            while(childNode < dijktra.numNodes) {
                logFile.write("*** In main (), childNode is " + childNode + " ***\n");
                if (dijktra.todo[childNode] == 1) {
                    int newCost = dijktra.best[minNode] + dijktra.costMatrix[minNode][childNode];
                    if (newCost < dijktra.best[childNode]) {
                        logFile.write("*** In main (): " + newCost + " < " +
                                dijktra.best[childNode] + "\n");
                        dijktra.best[childNode] = newCost;
                        dijktra.parent[childNode] = minNode;
                        dijktra.printParent(logFile);
                        dijktra.printBest(logFile);
                    }
                }
                childNode++;
            }
        }



    }

}