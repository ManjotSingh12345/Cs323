#include<iostream>
#include<string>
#include<fstream>
using namespace std;

class edge{
    private:
    public:
    int nU;
    int nW;
    int cost;
    edge* next;

    edge(int n1, int n2, int cost) {
        nU = n1;
        nW = n2;
        this->cost = cost;
        next = nullptr;
    }

    void printEdge(edge* e, ofstream& outFile) {
        outFile << "(" << e->nU << ", " << e->nW << ", " << e->cost << ") -> \n";
    }
};

class KruskalMST{
    private:
    public:
    int N;
    int* whichSet;
    int numSets;
    int totalMSTCost;
    edge* edgeList;
    edge* mstList;

    KruskalMST() {

    }

    void insertEdge(edge* listHead, edge* newEdge) {
        edge* spot = findSpot(listHead, newEdge);
        newEdge->next = spot->next;
        spot->next = newEdge;
    }

    edge* findSpot(edge* listHead, edge* newEdge) {
        edge* spot = listHead;
        while(spot->next != nullptr && spot->next->cost < newEdge->cost) {
            spot = spot->next;
        }

        return spot;
    }

    edge* removeEdge(edge* listHead) {
        edge* temp = listHead->next;
        listHead->next = listHead->next->next;
        temp->next = nullptr;
        return temp;
    }

    void merge2Sets(int Ni, int Nj) {
        if (whichSet[Ni] < whichSet[Nj]) {
            updateWhichSet(whichSet[Nj], whichSet[Ni]);
        }
        else {
            updateWhichSet(whichSet[Ni], whichSet[Nj]);
        }
    }

    void updateWhichSet(int a, int b) {
        for (int i = 1; i<= N; i++) {
            if (whichSet[i] == a) {
                whichSet[i] = b;
            }
        }
    }

    void printAry(int list[], ofstream& outFile) {
        outFile << "nodeId\twhich set\n===================================\n";
        for(int i=1; i<N+1; i++) {
            outFile << i << "\t\t" << list[i] << endl;
        }
        outFile << endl;
    }

    void printList(edge* list, ofstream& outFile) {
        outFile << "listHead";
        edge* temp = list;
        while(temp != nullptr) {
            outFile << " -> <" << temp->nU << ", " << temp->nW << ", " << temp->cost << ">";
            temp = temp->next;
        }
        outFile << endl;
    }

    void printMST(ofstream& outFile) {
        outFile << N << endl;
        edge* temp = mstList->next;
        while(temp != nullptr) {
            outFile << temp->nU << " " << temp->nW << " " << temp->cost << endl;
            temp = temp->next;
        }
        outFile << "*** The total cost of a Kruskal’s MST is: " << totalMSTCost << endl;
    }
};

int main(int argc, char** argv) {
    if (argc != 4) {
        cout << "Error, you need 1 input file, 1 output files, and 1 log file";
        exit(1);
    }

    ifstream inFile(argv[1]);
    ofstream outFile(argv[2]);
    ofstream logFile(argv[3]);

    if(!inFile) {
        cout << "Error cannot open input text";
        exit(1);
    }
    if(!outFile) {
        cout << "Error, cannot open output text";
        exit(1);
    }
    if(!logFile) {
        cout << "Error, cannot open logFile text";
        exit(1);
    }

    KruskalMST MST;
    inFile >> MST.N;
    MST.numSets = MST.N;
    MST.whichSet = new int[MST.N + 1];
    MST.edgeList = new edge(0,0,0);
    MST.mstList = new edge(0,0,0);
    MST.totalMSTCost = 0;
    for(int i=1; i<MST.N + 1; i++) {
        MST.whichSet[i] = i;
    }

    int u,w,cost;
    while (inFile >> u >> w >> cost) {
        edge* newEdge = new edge(u,w,cost);
        logFile << "*** In main(), newEdge from inFile is ";
        newEdge->printEdge(newEdge, logFile); 
        logFile << endl;

        MST.insertEdge(MST.edgeList, newEdge);
        logFile << "*** In main () Printing edgeList after insert the new edge: \n";
        MST.printList(MST.edgeList, logFile);
    }

    while (MST.numSets > 1) {
        edge* nextEdge = MST.removeEdge(MST.edgeList);
        while(MST.whichSet[nextEdge->nU] == MST.whichSet[nextEdge->nW]) {
            nextEdge = MST.removeEdge(MST.edgeList);
        }

        logFile << "*** in main() te nextEdge is ";
        nextEdge->printEdge(nextEdge, logFile);
        MST.insertEdge(MST.mstList, nextEdge);
        MST.totalMSTCost += nextEdge->cost;
        MST.merge2Sets(nextEdge->nU, nextEdge->nW);
        MST.numSets--;
        logFile << "In main (): after merge two sets: numSets is " << MST.numSets << endl;

        logFile << "*** In main (): Printing whichSet array\n";
        MST.printAry(MST.whichSet, logFile);
        logFile << "*** In main (): Printing the remaining of edgeList\n";
        MST.printList(MST.edgeList, logFile);
        logFile << "*** In main (): Print the growing MST list\n";
        MST.printList(MST.mstList, logFile);
    }

    outFile << "*** In main (): At the end, printing the final whichSet array\n";
    MST.printAry(MST.whichSet, outFile);
    outFile << "*** A Kruskal’s MST of the input graph is given below: *** \n";
    MST.printMST(outFile);

    inFile.close();
    outFile.close();
    logFile.close();
}