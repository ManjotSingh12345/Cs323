import java.io.*;

class HtreeNode {
    String chStr = "";
    int count = 0;
    String code = "";
    HtreeNode next = null;
    HtreeNode left = null;
    HtreeNode right = null;

    public HtreeNode(String chStr, int count, HtreeNode next, HtreeNode left, HtreeNode right) {
        this.chStr = chStr;
        this.count = count;
        this.next = next;
        this.right = right;
        this.left = left;
    }

    public void printNode(HtreeNode T, BufferedWriter ofile) throws IOException {
        ofile.write("(" + T.chStr + ", " + T.count + ", " + T.code);

        if(T.left != null) {ofile.write(", " + T.left.chStr);}
        else {ofile.write(", NULL" );}

        if(T.right != null) {ofile.write(", " + T.right.chStr);}
        else {ofile.write(", NULL" );}

        if(T.next != null) {ofile.write(", " + T.next.chStr);}
        else {ofile.write(", NULL" );}

        ofile.write(") -> ");
    }
}

class HuffmanCoding {
    HtreeNode root = null;
    HtreeNode listHead;
    int[] charCountArr = new int[256];
    String[] codeTable = new String[256];

    public HuffmanCoding() {
        for(int i=0; i<256; i++) {
            charCountArr[i] = 0;
            codeTable[i] = "";
        }
        listHead = new HtreeNode("dummy", 0,null, null, null);
    }

    public void computeCharCounts(BufferedReader inFile) throws IOException {
        int input = inFile.read();
        while(input != -1) {
            charCountArr[(int) input]++;
            input = inFile.read();
        }
    }

    public void printCountArr(BufferedWriter outFile) throws IOException {
        outFile.write("Ascii\tcharacter\t\tcount\n================\n");
        for(int i=0;i<256;i++){
            if(charCountArr[i] > 0) {
                outFile.write(i + "\t" + (char) i + "\t\t" + charCountArr[i] + "\n");
            }
        }
    }

    public void constructHuffmanLList (HtreeNode listHead, int[] charCountArr, BufferedWriter logFile) throws IOException {
        logFile.write("*** Entering constructHuffmanLList Method ***\n");

        int index = 0;
        while(index < 256) {
            if(charCountArr[index] > 0) {
                HtreeNode newNode = new HtreeNode( String.valueOf((char) index), charCountArr[index], null, null, null);
                logFile.write("*** In construct LList, printing newNode: ");
                newNode.printNode(newNode, logFile);
                logFile.write(" ***\n");

                HtreeNode spot = findSpot(listHead, newNode, logFile);
                listInsert(spot, newNode);
                logFile.write("*** In construct LList, printing list after inserting newNode: \n");
                printList(listHead, logFile);
                logFile.write(" ***\n");
            }
            index++;
        }
        logFile.write("*** Leaving constructHuffmanLList Method ***\n");
    }

    public HtreeNode findSpot(HtreeNode listHead, HtreeNode newNode, BufferedWriter logFile) throws IOException {
        logFile.write("*** Entering FindSpot Method ***\n");
        HtreeNode spot = listHead;
        while(spot.next != null && spot.next.count < newNode.count) {
            spot = spot.next;
            if(spot.next != null) {
                logFile.write("Spot.next's frequency is " + spot.next.count + " and newNode frequency is" + newNode.count + "\n");
            }
        }
        logFile.write("*** Leaving FindSpot Method ***\n");
        return spot;
    }

    public void listInsert(HtreeNode spot, HtreeNode newNode) {
        newNode.next = spot.next;
        spot.next = newNode;
    }

    public void printList(HtreeNode listHead, BufferedWriter outFile) throws IOException{
        HtreeNode current = listHead;
        while (current != null) {
            current.printNode(current, outFile);
            current = current.next;
        }
        outFile.write("NULL");
    }

    public boolean isLeaf (HtreeNode node) {
        return (node.left == null && node.right == null);
    }

    public HtreeNode constructHuffmanBinTree(HtreeNode listHead, BufferedWriter outfile, BufferedWriter logFile) throws IOException{
        logFile.write("*** Entering constructHuffmanBinTree Method ***\n");
        while(listHead.next.next != null) {
            HtreeNode newNode= new HtreeNode("", 0, null, null, null);
            HtreeNode node1 = listHead.next;
            HtreeNode node2 = listHead.next.next;

            newNode.count = node1.count + node2.count;
            newNode.chStr = node1.chStr + node2.chStr;
            newNode.code = "";
            newNode.left = node1;
            newNode.right = node2;
            newNode.next = null;

            newNode.printNode(newNode, logFile);
            logFile.write("\n");

            HtreeNode spot = findSpot(listHead, newNode, logFile);
            listInsert(spot, newNode);
            listHead.next = listHead.next.next.next;

            logFile.write("*** In constructHuffmanBinTree method, printing the list after inserting newNode: ");
            printList(listHead, logFile);
            logFile.write(" ***\n");
        }

        return listHead.next;
    }
}

public class SinghM_Project3_Main {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}