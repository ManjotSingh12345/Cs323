#include<iostream>
#include<string>
#include<fstream>
using namespace std;

class listNode {
    private:
    public:
    string data;
    listNode* next;

    listNode(string data) {
        this->data=data;
        next = nullptr;
    }
};

class hashTable {
    private:
    public:
    char op;
    string data;
    int bucketSize;
    listNode** hashTableArr;

    hashTable(int size) {
        this->bucketSize = size;
        hashTableArr = new listNode*[bucketSize];

        for(int i=0; i<bucketSize; i++) {
            hashTableArr[i] = new listNode("dummy");
        }
    }

    int Doit(string data, ofstream& logfile) {
        logfile << "*** Entering Doit() ***\n";
        long long value = 1;

        for(int i=0; i<data.size(); i++) {
            value = value * 16 + ((int) data[i]);
        }

        int index = value % bucketSize;
        logfile << "*** Doit returns index = " << index << " ***\n";
        logfile << "*** Leaving Doit() ***\n";
        return index;
    }

    listNode* findSpot(int index, string data, ofstream& logfile) {
        logfile << "*** entering findSpot method. Index = " << index << " Data = " << data << endl;
        listNode* spot = hashTableArr[index];
        
        while(spot->next != nullptr && spot->next->data < data) {
            spot = spot->next;
        }

        logfile << "*** leaving findSpot method and returning spot where spot’s data is " << spot->data << " ***\n";
        return spot;
    }

    void hashInsert(int index, string data, ofstream& logfile, ofstream& outFile) {
        logfile << "*** enter hashInsert method; index = " << index << " data = " << data << " ***\n";
        listNode* spot = findSpot(index, data, logfile);

        if(spot->next != nullptr && spot->next->data == data) {
            logfile << "*** Warning, data is already in the database! " << data << " ***\n";
            outFile << "*** Warning, data is already in the database! " << data << " ***\n";
        }
        else {
            logfile << "*** Printing list before hashInsert operation***\n";
            printList(index, logfile);
            listNode* newNode = new listNode(data);
            newNode->next = spot->next;
            spot->next = newNode;
            logfile << "*** Printing list after hashInsert operation ***\n";
            printList(index, logfile);
        }

        logfile << "*** Leaving hashInsert() ***\n";
    }

    void hashDelete(int index, string data, ofstream& logfile, ofstream& outFile) {
        logfile << "*** Inside hashDelete method. Index = " << index << " data = " << data << " ***\n";
        listNode* spot = findSpot(index, data, logfile);

        if(spot->next == nullptr or spot->next->data != data) {
            logfile << "*** Warning: data is *not* in the hashTable! " << data << " ***\n";
            outFile << "*** Warning: data is *not* in the hashTable! " << data << " ***\n";
        }
        else {
            logfile << "*** Printing list before hashDelete operation***\n";
            printList(index, logfile);

            listNode* temp = spot->next;
            spot->next = temp->next;
            temp->next = nullptr;
            delete temp;
            logfile << "*** Printing list after hashDelete operation ***\n";
            printList(index, logfile);
            logfile << "*** Leaving hashDelete() ***\n";
        }
    }

    void hashRetrieval(int index, string data, ofstream& logfile, ofstream& outFile) {
        logfile << "*** Inside hashRetrieval. Index = " << index << " data = " << data << " ***\n";

        listNode* spot = findSpot(index, data, logfile);
        if(spot->next == nullptr || spot->next->data != data) {
            outFile << "*** Warning, the record is *not* in the database! " << data << " ***\n";
            logfile << "*** Warning, the record is *not* in the database! " << data << " ***\n";
        }
        else {
            outFile << "Yes, the record is in the database! " << data << "\n";
            logfile << "Yes, the record is in the database! " << data << "\n";
        }
    }

    void printList(int index, ofstream& outFile) {
        outFile << "HashTable[" << index << "] -> ";

        listNode* temp = hashTableArr[index];
        while(temp != nullptr) {
            if(temp->next != nullptr)
            {
                outFile << "(" << temp->data << ", " << temp->next->data << ") -> ";
            }
            else {
                outFile << "(" << temp->data << ", NULL" << ") -> NULL\n";
            }
            temp = temp->next;
        }
    }

    void printHashTable(ofstream& outFile) {
        for(int i=0; i<bucketSize; i++) {
            printList(i, outFile);
        }
        outFile << "\n";
    }

    void deleteHashTable() {
        for(int i=0; i<bucketSize; i++) {
            listNode* current = hashTableArr[i];

            while (current != nullptr) {
                listNode* next = current->next;
                delete current;
                current = next;
            }
        }
        delete[] hashTableArr;
    }
};

int main(int argc, char** argv) {
 //Checking if all args correct
    if(argc != 5) {
        cout << "Error, you need 1 input file, bucketSize, 1 output files, and 1 log file";
        exit(1);
    }
    ifstream inFile(argv[1]);
    if(!inFile) {
        cout << "Error cannot open input text";
        exit(1);
    }

    int bucketSize = stoi(argv[2]);

    ofstream outFile(argv[3]);
    if(!outFile) {
        cout << "Error, cannot open output text";
        exit(1);
    }
    ofstream logFile(argv[4]);
    if(!logFile) {
        cout << "Error, cannot open logfile";
        exit(1);
    }

    hashTable hashTable1(bucketSize);
    char op;
    string data;

    while (inFile >> op >> data) {
        int index = hashTable1.Doit(data, logFile);
        logFile << "In main(): op = " << op << " data = " << data << " index = " << index << endl;

        if (op == '+') {
            logFile << "*** Calling hashInsert: index = " << index << "; data = " << data << " ***\n";
            hashTable1.hashInsert(index, data, logFile, outFile);
        }
        else if(op == '-') {
            logFile << "*** Calling hashDelete: index = " << index << "; data = " << data << " ***\n";
            hashTable1.hashDelete(index, data, logFile, outFile);
        }
        else if(op == '?') {
            logFile << "*** Calling hashRetrieval: index = " << index << "; data = " << data << " ***\n";
            hashTable1.hashRetrieval(index, data, logFile, outFile);
        }
        else {
            logFile << "op is a illegal operation! op: " << op << endl;
        }

        logFile << "*** In main () below is the hash Table after iteration index = " << index << " ***\n";
        hashTable1.printHashTable(logFile);
    }

    outFile << "*** In main () below is the final hash Table ***\n";
    hashTable1.printHashTable(outFile);
    hashTable1.deleteHashTable();

    inFile.close();
    outFile.close();
    logFile.close();
    exit(0);
}