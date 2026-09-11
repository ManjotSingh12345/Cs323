#include <fstream>
#include <string>
#include <iostream>
using namespace std;

class listNode {
    private: 
    public:
        string data;
        listNode* next;

        listNode(string data) {
            this->data = data;
            next = nullptr;
        }

        void printNode(listNode node, ofstream& outFile) {
            if(node.next != nullptr) { 
                outFile << "(" << node.data << ", " << node.next->data << ") -> ";
            }
            else {
                outFile << "(" << node.data << ", " << "NULL" << ") ";
            }
        }
};

class LLStack {
    private:
    public:
    listNode* top;

    LLStack() {
        top = new listNode("dummy");  
    }

    void push(listNode* newNode) {
        newNode->next = top->next;
        top->next = newNode;
    }

    bool isEmpty() {
        if(top->next == nullptr) return true;
        else return false;
    }

    listNode* pop() {
        if (top->next != nullptr) {
            listNode* temp = top->next;
            top->next = temp->next;
            temp->next = nullptr;
            return temp;
        }
        else return nullptr;
    }

    void buildStack(LLStack& s, ifstream& inFile, ofstream& logFile) {
        logFile << "Entering buildStack()!\n";
        string data;

        while(inFile >> data) {
            logFile << "input data is " << data << endl;
            listNode* newNode = new listNode(data);
            s.push(newNode);
        }

        logFile << "Leaving buildStack()!\n\n";
    }

    void printStack(ofstream& outFile) {
        outFile << "Printing Stack: TOP -> ";
        listNode* temp = top;
        while(temp != nullptr) {
            temp->printNode(*temp, outFile);
            temp = temp->next;
        }
        outFile << "-> NULL\n";
    }
};

class LLQueue {
    private:
    public: 
        listNode* head; //points to dummy node
        listNode* tail; //points to last node

        LLQueue() {
            head = new listNode("dummy");
            tail = head;
        }

        void insertQ(listNode* newNode) {
            //Insert at tail
            tail->next = newNode;
            tail = newNode;
        }

        listNode* deleteQ(LLQueue& q, ofstream& outFile, ofstream& logFile) {
            //delete at head
            logFile << "Entering deleteQ ()\n";
            if(q.isEmpty()) {
                outFile << "Q is empty\n";
                logFile << "Q is empty\n";
                return nullptr;
            }
            else {
                listNode* temp = q.head->next;
                if(temp == q.tail) q.tail = q.head;
                q.head->next = temp->next;
                temp->next = nullptr;
                logFile << temp->data << endl;
                logFile << "Leaving deleteQ () \n\n";
                return temp;
            }
        }

        bool isEmpty() {
            if(head == tail) return true;
            else return false;
        }

        void buildQueue(LLStack& s, LLQueue& q, ofstream& outFile, ofstream& logFile) {
            logFile << "Entering buildQueue()!\n";
            outFile << "Printing pop: ";

            listNode* newNode;
            while(!s.isEmpty()) { 
                newNode = s.pop();
                outFile << " " << newNode->data;
                logFile << "After pop stack, newNode's data is " << newNode->data << endl;
                q.insertQ(newNode);
            }

            logFile << "Leaving buildQueue()!\n\n";
            outFile << "\n";
        }

        void printQueue(ofstream& outFile) {
            listNode* temp = head;
            outFile << "Printing Queue: head -> ";
            while(temp != nullptr) {
                temp->printNode(*temp, outFile);
                temp = temp->next;
            }
            outFile << "<- tail\n";
        }
};

class LLlist {
    private:
    public:
        listNode* head;

        LLlist() {
            head = new listNode("dummy");
        }

        listNode* findSpot(LLlist& list, listNode& newNode, ofstream& logFile) {
            logFile << "Entering findSpot()\n";

            listNode* spot = head;
            while (spot->next != nullptr && spot->next->data.compare(newNode.data) < 0) {
                spot = spot->next;
            }

            logFile << "Spot's data is " << spot->data << endl;
            logFile << "Leaving findSpot()\n\n";
            return spot;
        }

        void insertOneNode(listNode* newNode, ofstream& logFile) {
            listNode* spot = findSpot(*this, *newNode, logFile);
            newNode->next = spot->next;
            spot->next = newNode;
        }

        void buildList(LLQueue& q, LLlist& list, ofstream& outFile, ofstream& logFile) {
            logFile << "Entering buildList()\n";
            outFile << "Printing deleteQ: ";
            while(!q.isEmpty()) {
                listNode* newNode = q.deleteQ(q, outFile, logFile);
                outFile << " " << newNode->data;
                logFile << "Deleted a node from queue, newNode's data is " << 
                    newNode->data << endl;
                insertOneNode(newNode, logFile);
            }

            logFile << "Leaving buildList()\n\n";
            outFile << "\n";
        }

        void printList(ofstream& outFile) { 
            listNode* temp = head;
            outFile << "Printing List: listHead -> ";
            while(temp != nullptr) {
                temp->printNode(*temp, outFile);
                temp = temp->next;
            }
            outFile << "-> NULL\n";
        }
};

int main(int argc, char** argv) {
    //Checking if all args correct
    if(argc != 6) {
        cout << "Error, you need 1 input file, 3 output files, and 1 log file";
        exit(1);
    }
    ifstream inFile(argv[1]);
    if(!inFile) {
        cout << "Error cannot open input text";
        exit(1);
    }

    ofstream outFile1(argv[2]);
    if(!outFile1) {
        cout << "Error, cannot open output text 1";
        exit(1);
    }
    ofstream outFile2(argv[3]);
    if(!outFile2) {
        cout << "Error, cannot open output text 2";
        exit(1);
    }
    ofstream outFile3(argv[4]);
    if(!outFile3) {
        cout << "Error, cannot open output text 3";
        exit(1);
    }
    ofstream logFile(argv[5]);
    if(!logFile) {
        cout << "Error, cannot open logfile";
        exit(1);
    }

    //building Stack
    LLStack stack;
    stack.buildStack(stack, inFile, logFile);
    stack.printStack(outFile1);

    //Building Queue
    LLQueue queue;
    queue.buildQueue(stack, queue, outFile1, logFile);
    queue.printQueue(outFile2);

    //building list
    LLlist list;
    list.buildList(queue, list, outFile2, logFile);
    list.printList(logFile);
    list.printList(outFile3);
    
    //Closing streams and exiting
    inFile.close();
    outFile1.close();
    outFile2.close();
    outFile3.close();
    logFile.close();
    exit(0);
}