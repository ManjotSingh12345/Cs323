#include <iostream>
#include <fstream>
using namespace std;

class QtNode {
    private:
    public:
    int color;
    int upperR;
    int upperC;
    int size;
    QtNode* NW;
    QtNode* NE;
    QtNode* SW;
    QtNode* SE;

    QtNode(int color, int upperR, int upperC, int size, QtNode* NW, QtNode* NE, QtNode* SW, QtNode* SE) {
        this->color = color;
        this->upperR = upperR;
        this->upperC = upperC;
        this->size = size;
        this->NW = NW;
        this->NE = NE;
        this->SW = SW;
        this->SE = SE;
    }

    void printQtNode(QtNode* node, ofstream& outFile) {
        if (node->color == 5) {
            outFile << "<5, " << ", " << node->NW->color << ", " << node->NE->color << ", " << node->SW->color << ", " << node->SE->color << "> -> ";
        }
        else if(node->color == 0 || node->color == 1) {
            outFile << "<" << node->color << ", null, null, null, null> -> ";
        }
    }
};

class QtTree {
    QtNode* QtRoot;
    int numRows;
    int numCols;
    int minVal;
    int maxVal;
    int power2Size;
    int** imgAry;
    int** newImgAry;

    QtTree() {

    }

    int computePower2(int numRows, int numCols, ofstream logFile) {
        logFile << "** Entering computePower2 method\n";
        int size = max(numRows, numCols);
        power2Size = 2;
        while (size > power2Size) {
            power2Size *= 2;
        }
        logFile << "“** Leaving computePower2 method, power2 is " << power2Size << endl;
    }

    void loadImage(ifstream& inFile) {
        inFile >> numRows >> numCols >> minVal >> maxVal;
        imgAry = new int*[numRows];
        for(int i=0; i<numRows; i++) {
            imgAry[i] = new int[numCols];
        }

        for(int r=0; r<numRows; r++) {
            for(int c=0; c<numCols; c++) {
                inFile >> imgAry[r][c];
            }
        }
    }

    void zero2DAry() {
        for(int i=0;i<numRows; i++) {
            for(int j=0;j<numCols; j++) {
                imgAry[i][j] = 0;
            }
        }
    }

    QtNode* buildQuadTree(int** imgAry, int upR, int upC, int size, ofstream& logFile) {
        logFile << "** Entering buildQuadTree: upR=" << upR << " upC=" << upC << " and size=" << size << "\n";
        QtNode* newQtNode = new QtNode(-1, upR, upC, size, nullptr, nullptr, nullptr, nullptr);

        if(size == 1) {
            newQtNode->color = imgAry[upR][upC];
        }
        else {
            int halfSize = size/2;
            newQtNode->NW = buildQuadTree(imgAry, upR, upC, size, logFile);
            newQtNode->NE = buildQuadTree(imgAry, upR, upC + halfSize, size, logFile);
            newQtNode->SW = buildQuadTree(imgAry, upR + halfSize, upC, size, logFile);
            newQtNode->SE = buildQuadTree(imgAry, upR + halfSize, upC + halfSize, size, logFile);

            int sumColor = addKidsColor(newQtNode);
            logFile << "** the sumColor 4 children’s colors is " << sumColor << endl;

            if (sumColor == 0) {newQtNode->color = 0;}
            else if (sumColor == 4) {newQtNode->color = 1;}
            else {newQtNode->color = 5;}
        }

        logFile << "** Printing newQtNode below\n";
        newQtNode->printQtNode(newQtNode, logFile);
        logFile << "** leaving buildQuadTree\n";
        return newQtNode;
    }

    int addKidsColor(QtNode* node) {
        return node->NW->color + node->NE->color + node->SW->color + node->SE->color;
    }

    bool isLeaf(QtNode* node) {
        if(node->color == 1 || node->color == 0) {return true;}
        else {return false;}
    }

    void Ary2File(int** intAry, ofstream& outFile) {
        for(int r=0; r<numRows; r++) {
            for(int c=0; c<numCols; c++) {
                outFile << intAry[r][c] << " ";
            }
            outFile << endl;
        }
    }

    QtNode* preOrder(QtNode* Qt, ofstream& outFile) {
        if (isLeaf(Qt)) {
            Qt->printQtNode(Qt, outFile);
        }
        else {
            Qt->printQtNode(Qt, outFile);
            preOrder(Qt->NW, outFile);
            preOrder(Qt->NE, outFile);
            preOrder(Qt->SW, outFile);
            preOrder(Qt->SE, outFile);
        }
    }

    QtNode* postOrder(QtNode* Qt, ofstream& outFile) {
        if (isLeaf(Qt)) {
            Qt->printQtNode(Qt, outFile);
        }
        else {
            preOrder(Qt->NW, outFile);
            preOrder(Qt->NE, outFile);
            preOrder(Qt->SW, outFile);
            preOrder(Qt->SE, outFile);
            Qt->printQtNode(Qt, outFile);
        }
    }

    void fillNewImgAry(QtNode* qt, int** newImgAry, ofstream& logFile) {
        logFile << "** Entering fillnewImgAry method\n";
        int color, R, C, sz;

        color = qt->color;
        R = qt->upperR;
        C = qt->upperC;
        sz = qt->size;

        int i = R;
        int j = C;
        while(i < R + sz) {
            while(j < C + sz) {
                newImgAry[i][j] = color;
                j++;
            }
        }
        
        logFile << "**Leaving fillnewImgAry method\n";
    }

    QtNode* getLeaf(QtNode* Qt, int** newImgAry, ofstream& logFile) {
        logFile << "** Entering getLeaf method\n";
        if (isLeaf(Qt)) {
            fillNewImgAry(Qt, newImgAry, logFile);
        }
        else {
            fillNewImgAry(Qt->NW, newImgAry, logFile);
            fillNewImgAry(Qt->NE, newImgAry, logFile);
            fillNewImgAry(Qt->SW, newImgAry, logFile);
            fillNewImgAry(Qt->SE, newImgAry, logFile);
        }

        logFile << "** Leaving getLeaf method\n";
    }
};

int main(int argc, char** argv) {

}