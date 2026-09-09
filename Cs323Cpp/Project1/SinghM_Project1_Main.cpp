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
                outFile << node.data << ", " << node.next->data << " ";
            }
            else {
                outFile << node.data << ", " << "NULL";
            }
        }
};

class LLStack {
    private:
        listNode* top;

    public:

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
        logFile << "Entering buildStack () !\n";
        string data;

        while(inFile >> data) {
            logFile << "input data is " << data << endl;
            listNode* newNode = new listNode(data);
            s.push(newNode);
        }

        logFile << "Leaving buildStack () !" << endl;
    }

    void printStack(ofstream& outFile) {
        listNode* temp = top;
        while(temp != nullptr) {
            temp->printNode(*temp, outFile);
            temp = temp->next;
        }
    }
};

class LLQueue {
    private:

    public: 
        listNode* head; //points to dummy node
        listNode* tail; //points to last node 

        LLQueue() {
            head = new listNode("dumnmy");
            tail = head;
        }

        void insertQ(listNode newNode) {

        }

};

int main(int argc, char** argv) {

    //Checking if all args correct
    if(argc != 6) {
        cout << "Error, tou need 1 input file, 3 output files, and 1 log file";
        return 1;
    }
    ifstream inFile(argv[1]);
    if(!inFile) {
        cout << "Error cannot open input text";
        return 1;
    }

    ofstream outFile1(argv[2]);
    if(!outFile1) {
        cout << "Error, cannot open output text 1";
        return 1;
    }
    ofstream outFile2(argv[3]);
    if(!outFile2) {
        cout << "Error, cannot open output text 2";
        return 1;
    }
    ofstream outFile3(argv[4]);
    if(!outFile3) {
        cout << "Error, cannot open output text 3";
        return 1;
    }
    ofstream logFile(argv[5]);
    if(!logFile) {
        cout << "Error, cannot open logfile";
        return 1;
    }

    //building Queue
    LLStack stack;
    stack.buildStack(stack, inFile, logFile);
    stack.printStack(outFile1);

    return 0;
}