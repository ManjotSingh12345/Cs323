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

    public schedule(Scanner inFile1, Scanner inFile2, int numN, int numP) {
        numNodes = numN;
        numProcs = numP;

        jobTimeAry = new int[numNodes+1];
        jobStatus = new int[numNodes+1];
        Matrix = new int[numNodes+1][numNodes+1];
        totalJobTimes = loadJobTimeAry(inFile2);
        Table = new int[numProcs+1][totalJobTimes+1];
        for(int i=0;i<numNodes+1;i++) {
            jobStatus[i] = 1;
            for(int j=0;j<numNodes+1;j++) {
                Matrix[i][j] = 0;
            }
        }
        for(int i=0;i<numProcs+1;i++) {
            for(int j=0;j<totalJobTimes+1;j++) {
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
        logFile.write("Entering setMatrix() method\n");
        Matrix[0][0] = numNodes;
        int j = 1;
        while(j <= numNodes) {
            Matrix[0][j] = countParent(Matrix, j, logFile);
            Matrix[j][0] = countDependent(Matrix, j, logFile);
            j++;
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
        logFile.write("In countParent(), parent count is " + count + "\n");
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
        logFile.write("In countDependent(), dependent count is " + count + "\n");
        return count;
    }

    int loadJobTimeAry(Scanner inFile2) {
        int a = inFile2.nextInt();
        for(int i=0;i<numNodes+1;i++) {
            jobTimeAry[i] = 1;
        }
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
                toReturn = j;
            }
            j++;
        }
        return toReturn;
    }

    listNode findSpot(listNode head, listNode newNode) {
        listNode spot = head;
        while(spot.next != null && spot.next.jobTime <= newNode.jobTime) {
            spot = spot.next;
        }
        return spot;
    }

    int getNextProc(int currentTime) {
        int toReturn = -1;
        if(currentTime > totalJobTimes) return toReturn;
        for(int i=0; i<numProcs; i++) {
            if (Table[i][currentTime] == 0) return i;
        }
        return toReturn;
    }

    void OpenInsert(listNode toInsert) {
        listNode spot = findSpot(open, toInsert);
        toInsert.next = spot.next;
        spot.next = toInsert;
    }

    void fillOPEN(BufferedWriter logFile) throws IOException {
        logFile.write("Entering fillOPEN() method\n");
        int jobID = findOrphan();
        while(jobID != -1) {
            listNode newNode = new listNode(jobID, jobTimeAry[jobID]);
            listNode spot = findSpot(open, newNode);
            newNode.next = spot.next;
            spot.next = newNode;
            logFile.write("In fillOPEN(), printing OPEN\n");
            printOPEN(logFile);
            jobID = findOrphan();
        }

        logFile.write("Leaving fillOPEN() method\n");
    }

    void fillTable(BufferedWriter logFile) throws IOException {
        logFile.write("Entering fillTable() method\n");
        int availProc = getNextProc(currentTime);
        while(availProc >= 0 && open.next != null && procUsed < numProcs) {
            listNode newJob = removeOPEN();
            putJobOnTable(availProc, currentTime, newJob.jobID, newJob.jobTime, logFile);
            if (availProc >= procUsed) {
                procUsed++;
            }
            logFile.write("In fillTable(), printing the Table; availProc = " + availProc + "; procUsed = " + procUsed + "\n");
            printTable(logFile, currentTime);
            availProc = getNextProc(currentTime);
        }
        logFile.write("Leaving fillTable() method\n");
    }

    void putJobOnTable(int availProc, int currentTime, int jobId, int jobTime, BufferedWriter logFile) throws IOException{
        logFile.write("Entering putJobOnTable() method\n");
        int Time = currentTime;
        int EndTime = Time + jobTime;
        while(Time < EndTime) {
            Table[availProc][Time] = jobId;
            Time++;
        }

        logFile.write("Leaving putJobOnTable() method\n");
    }

    void printMatrix(BufferedWriter outFile) throws IOException {
        outFile.write("\n");
        for(int i=0; i<numNodes+1; i++) {
            for(int j=0; j<numNodes+1; j++) {
                outFile.write(Matrix[i][j] + "\t");
            }
            outFile.write("\n");
        }
        outFile.write("\n");
    }

    void printOPEN(BufferedWriter outFile) throws IOException {
        listNode currentNode = open;
        outFile.write("OPEN -> ");
        while(currentNode != null) {
            if(currentNode.next != null) {
                outFile.write("(" + currentNode.jobID + ", " + currentNode.jobTime + ", " + currentNode.next.jobID + ") -> ");
            }
            else {
                outFile.write("(" + currentNode.jobID + ", " + currentNode.jobTime + ", NULL) -> ");
            }

            currentNode = currentNode.next;
        }
        outFile.write("NULL\n");
    }

    listNode removeOPEN() {
        if(open.next != null) {
            listNode temp = open.next;
            open.next = temp.next;
            temp.next = null;
            return temp;
        }
        else {
            return null;
        }
    }

    void printTable(BufferedWriter outFile, int currentTime) throws IOException {
        outFile.write("Time:       ");
        for(int i=0; i<totalJobTimes; i++) {
            outFile.write(String.format("%-6d", i));
        }

        outFile.write("\n         ");
        for(int i=0;i<totalJobTimes; i++) {
            outFile.write("------");
        }
        outFile.write("-");
        for(int i=0; i< numProcs; i++) {
            outFile.write("\n");
            outFile.write(String.format("Proc " + "%-4d", (i+1)));
            for(int j=0; j < totalJobTimes; j++) {
                if(j <= currentTime) {
                    if(Table[i][j] != 0) {
                        outFile.write("");
                        outFile.write(String.format("| %-3d ", Table[i][j]));
                    }
                    else {
                        outFile.write("|  -  ");
                    }
                }
                else {
                    outFile.write("|     ");
                }
            }

            outFile.write("|\n         ");
            for(int j=0;j<totalJobTimes; j++) {
                outFile.write("------");
            }
            outFile.write("-");
        }
        outFile.write("\n");
    }

    void print1DAry(int[] Ary, BufferedWriter outFile) throws IOException{
        for(int i=0; i<Ary.length; i++) {
            outFile.write(i + "\t" + Ary[i] + "\n");
        }
    }

    boolean checkCycle() {
        boolean toReturn = true;
        if(open.next == null && Matrix[0][0] > 0) {
            for(int i=0; i<numProcs; i++) {
                if(Table[i][currentTime] != 0) {
                    toReturn = false;
                }
            }
        }
        else {
            toReturn = false;
        }
        return toReturn;
    }

    boolean isGraphEmpty() {
        return Matrix[0][0] == 0;
    }

    void deleteDoneJobs(BufferedWriter logFile) throws IOException {
        logFile.write("Entering deleteDoneJobs() method\n");

        for(int proc = 0; proc < numProcs; proc++) {
            if(currentTime <= totalJobTimes && Table[proc][currentTime] <= 0 && Table[proc][currentTime - 1] > 0) {
                int jobID = Table[proc][currentTime-1];
                deleteJob(jobID, logFile);
            }
        }

        logFile.write("Leaving deleteDoneJobs() method\n");
    }

    void deleteJob(int jobID, BufferedWriter logFile) throws IOException {
        logFile.write("Entering deleteJob() method\n");
        jobStatus[jobID] = 0;
        Matrix[0][0]--;
        procUsed--;
        int j = 1;
        while(j <= numNodes) {
            if(Matrix[jobID][j] > 0) {
                Matrix[0][j]--;
            }
            j++;
        }

        logFile.write("Leaving deleteJob() method\n");
    }
}


public class SinghM_Project6_Main {
    public static void main(String[] args) throws IOException {
        Scanner inFile1 = null;
        Scanner inFile2 = null;
        int numP = 0;
        BufferedWriter outFile = null;
        BufferedWriter logFile = null;

        if(args.length != 5) {
            System.out.println("Error, you need 2 input files, a processor count, a output file, and a logfile\n");
            System.exit(1);
        }
        try {
            inFile1 = new Scanner(new FileReader(args[0]));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error, you need 2 input files, a processor count, a output file, and a logfile\n");
            System.exit(1);
        }
        try {
            inFile2 = new Scanner(new FileReader(args[1]));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error, you need 2 input files, a processor count, a output file, and a logfile\n");
            System.exit(1);
        }
        try{
            numP = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Error, you need 2 input files, a processor count, a output file, and a logfile\n");
            System.exit(1);
        }
        try {
            outFile = new BufferedWriter(new FileWriter(args[3]));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error, you need 2 input files, a processor count, a output file, and a logfile\n");
            System.exit(1);
        }
        try {
            logFile = new BufferedWriter(new FileWriter(args[4]));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error, you need 2 input files, a processor count, a output file, and a logfile\n");
            System.exit(1);
        }

        int numN = inFile1.nextInt();

        if(numP <= 0) {
            System.out.println("Need 1 or more processors");
            System.exit(1);
        }
        else if(numP > numN) {
            numP = numN;
        }
        schedule sch = new schedule(inFile1, inFile2, numN, numP);
        sch.procUsed = 0;
        sch.currentTime = 0;

        sch.loadMatrix(inFile1);
        sch.setMatrix(sch.Matrix, logFile);

        outFile.write("***in main(), printing Matrix");
        sch.printMatrix(outFile);
        outFile.write("\n***in main(), printing jobTimeAry\n");
        sch.print1DAry(sch.jobTimeAry, outFile);
        outFile.write("\n***in main(), printing jobStatus\n");
        sch.print1DAry(sch.jobStatus, outFile);

        while(!sch.isGraphEmpty()) {
            outFile.write("\nin main(), printing OPEN before fillOPEN\n");
            sch.printOPEN(outFile);
            sch.fillOPEN(logFile);
            outFile.write("\nIn main(), printing OPEN after fillOPEN\n");
            sch.printOPEN(outFile);

            boolean cycle = sch.checkCycle();
            if (cycle) {
                outFile.write("\nThere is a cycle!");
                outFile.close();
                inFile1.close();
                inFile2.close();
                logFile.close();
                System.exit(1);
            }

            outFile.write("\nIn main(), printing Table, before fillTable\n");
            sch.printTable(outFile, sch.currentTime);
            sch.fillTable(logFile);
            outFile.write("\nIn main(), printing Table after fillTable\n");
            sch.printTable(outFile, sch.currentTime);

            sch.currentTime++;
            sch.deleteDoneJobs(logFile);
        }

        outFile.write("\nIn main() printing the final table\n");
        sch.printTable(outFile, sch.currentTime);

        outFile.close();
        inFile1.close();
        inFile2.close();
        logFile.close();
    }
}