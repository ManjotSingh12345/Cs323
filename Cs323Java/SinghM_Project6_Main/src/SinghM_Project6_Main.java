import java.util.*;
import java.io.*;

class listNode {
    int jobID;
    int jobTime;
    listNode next;

    public listNode(int jobID, int jobTime) {
        this.jobID = jobID;
        this.jobTime = jobTime;
        next = null;
    }
}

class schedule {
    int numNodes; //num of nodes in graph
    int numProcs; //lim of processors
    int procUsed;
    int currentTime;
    int totalJobTimes;
    int[] jobTimeAry;
    int[] jobStatus;
    int[][] Matrix;
    int[][] Table;
    listNode open;

    public schedule(Scanner inFile1, Scanner inFile2, int procs) {
        numNodes = inFile1.nextInt();
        numProcs = procs;
        procUsed = 0;
        currentTime = 0;
        totalJobTimes = 100; //To be changed
        jobTimeAry = new int[numNodes+1];
        jobStatus = new int[numNodes+1];
        Matrix = new int[numNodes+1][numNodes+1];
        Table = new int[numProcs+1][totalJobTimes + 1];
        for(int i=0;i<numNodes+1;i++) {
            jobTimeAry[i] = 1;
            jobStatus[i] = 1;
            for(int j=0;j<numNodes+1;j++) {
                Matrix[i][j] = 0;
                Table[i][j] = 0;
            }
        }
        open = new listNode(-1,0);
    }

    public void loadMatrix(Scanner inFile1) {
        while(inFile1.hasNextInt()) {
            int ni = inFile1.nextInt();
            int nj = inFile1.nextInt();
            Matrix[ni][nj] = 1;
        }
    }

    public void setMatrix(int[][] Matrix, BufferedWriter logFile) throws IOException {
        logFile.write("Entering ssetMatrix method\n");
        Matrix[0][0] = numNodes;
        int j = 1;
        while(j > numNodes) {
            Matrix[0][j] = countParent(Matrix, j, logFile);
            Matrix[j][0] = countDependent(Matrix, j, logFile);
        }
        logFile.write("Leaving setMatrix()\n\n");
    }

    public int countParent (int[][] Matrix, int j, BufferedWriter logFile) throws IOException {
        logFile.write("Entering countParent() method\n");
        int count = 0;
        int i=1;
        while(i<= numNodes) {
            if (Matrix[i][j] >0) {
                count++;
            }
            i++;
        }
        logFile.write("In countParent(), parent count is " + count);
        return count;
    }

    public int countDependent(int[][] Matrix, int j, BufferedWriter logFile) throws IOException {
        logFile.write("Entering countDependent() method\n");
        int count = 0;
        int i=1;
        while(i<= numNodes) {
            if (Matrix[j][i] >0) {
                count++;
            }
            i++;
        }
        logFile.write("In countDependent(), dependent count is " + count);
        return count;
    }

    int loadJobTimeAry(Scanner inFile2) {
        int a = inFile2.nextInt();
        int totalJobTimes = 0;
        while(inFile2.hasNextInt()) {
            int jobID = inFile2.nextInt();
            int time = inFile2.nextInt();
            jobTimeAry[jobID] = time;
            totalJobTimes += time;
        }
        return totalJobTimes;
    }

    int findOrphan() {
        int toReturn = -1;
        int j = 1;
        while(j < numNodes+1 && toReturn == -1) {
            if(Matrix[0][j] == 0 && jobStatus[j] == 1) {
                jobStatus[j] = 2;
                return j;
            }
        }
        return toReturn;
    }

    listNode findSpot(listNode head, listNode newNode) {
        listNode spot = head;
        if(spot.next != null && spot.next.jobTime < newNode.next.jobTime) {
            spot = spot.next;
        }
        return spot;
    }
}


public class SinghM_Project6_Main {
    public static void main(String[] args) {

    }
}