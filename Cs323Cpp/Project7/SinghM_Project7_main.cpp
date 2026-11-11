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
            outFile << "<5, " << node->NW->color << ", " << node->NE->color << ", " << node->SW->color << ", " << node->SE->color << "> -> ";
        }
        else if(node->color == 0 || node->color == 1) {
            outFile << "<" << node->color << ", null, null, null, null> -> ";
        }
    }
};

class QtTree {
    private:
    public:
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

    int computePower2(int numRows, int numCols, ofstream& logFile) {
        logFile << "** Entering computePower2 method\n";
        int size = max(numRows, numCols);
        power2Size = 2;
        while (size > power2Size) {
            power2Size *= 2;
        }
        logFile << "“** Leaving computePower2 method, power2 is " << power2Size << endl;
        return power2Size;
    }

    void loadImage(ifstream& inFile, int** imgAry) {
        for(int r=0; r<power2Size; r++) {
            for(int c=0; c<power2Size; c++) {
                inFile >> imgAry[r][c];
            }
        }
    }

    void zero2DAry(int** imgAry) {
        for(int i=0;i<power2Size; i++) {
            for(int j=0;j<power2Size; j++) {
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
            newQtNode->NW = buildQuadTree(imgAry, upR, upC, halfSize, logFile);
            newQtNode->NE = buildQuadTree(imgAry, upR, upC + halfSize, halfSize, logFile);
            newQtNode->SW = buildQuadTree(imgAry, upR + halfSize, upC, halfSize, logFile);
            newQtNode->SE = buildQuadTree(imgAry, upR + halfSize, upC + halfSize, halfSize, logFile);

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
        for(int r=0; r<power2Size; r++) {
            for(int c=0; c<power2Size; c++) {
                outFile << intAry[r][c] << " ";
            }
            outFile << endl;
        }
    }

    void preOrder(QtNode* Qt, ofstream& outFile) {
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

    void postOrder(QtNode* Qt, ofstream& outFile) {
        if (isLeaf(Qt)) {
            Qt->printQtNode(Qt, outFile);
        }
        else {
            postOrder(Qt->NW, outFile);
            postOrder(Qt->NE, outFile);
            postOrder(Qt->SW, outFile);
            postOrder(Qt->SE, outFile);
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
        while(i < R + sz) {
            int j = C;

            while(j < C + sz) {
                newImgAry[i][j] = color;
                j++;
            }
            i++;
        }
        
        logFile << "**Leaving fillnewImgAry method\n";
    }

    void getLeaf(QtNode* Qt, int** newImgAry, ofstream& logFile) {
        logFile << "** Entering getLeaf method\n";
        if (isLeaf(Qt)) {
            fillNewImgAry(Qt, newImgAry, logFile);
        }
        else {
            getLeaf(Qt->NW, newImgAry, logFile);
            getLeaf(Qt->NE, newImgAry, logFile);
            getLeaf(Qt->SW, newImgAry, logFile);
            getLeaf(Qt->SE, newImgAry, logFile);
        }

        logFile << "** Leaving getLeaf method\n";
    }
};

int main(int argc, char** argv) {
    if(argc != 4) {
        cout << "error, you need 1 input file, 1 log file, and 1 output file";
        exit(1);
    }

    ifstream inFile(argv[1]);
    ofstream outFile(argv[2]);
    ofstream logFile(argv[3]);

    if(!inFile) {
        cout << "Error, cannot open input file";
        exit(1);
    }
    if(!outFile) {
        cout << "Error, cannot open output file";
        exit(1);
    }
    if(!logFile) {
        cout << "Error, cannot open log file";
        exit(1);
    }

    QtTree tree;
    inFile >> tree.numRows >> tree.numCols >> tree.minVal >> tree.maxVal;
    
    tree.power2Size = tree.computePower2(tree.numRows, tree.numCols, logFile);
    outFile << "*** In main(), power2Size = " << tree.power2Size << "\n";

    tree.imgAry = new int*[tree.power2Size];
    for(int i=0; i<tree.power2Size; i++) {
        tree.imgAry[i] = new int[tree.power2Size];
    }
    tree.zero2DAry(tree.imgAry);
    tree.newImgAry = new int*[tree.power2Size];
    for(int i=0; i<tree.power2Size; i++) {
        tree.newImgAry[i] = new int[tree.power2Size];
    }
    tree.zero2DAry(tree.newImgAry);

    tree.loadImage(inFile, tree.imgAry);
    outFile << "*** Below is the re-sized input image\n";
    tree.Ary2File(tree.imgAry, outFile);

    tree.QtRoot = tree.buildQuadTree(tree.imgAry, 0, 0, tree.power2Size, logFile);

    outFile << "*** Below is the pre-order traversal of the Quad tree\n";
    tree.preOrder(tree.QtRoot, outFile);

    outFile << "\n*** Below is the post-order traversal of the Quad tree\n";
    tree.postOrder(tree.QtRoot, outFile);

    logFile << "*** In Main(): calling getLeaf (..) to recreate the image from quadtree\n";

    tree.getLeaf(tree.QtRoot, tree.newImgAry, logFile);

    outFile << "\n*** Below is the re-created image from Quad tree\n";
    outFile << tree.numRows << " " << tree.numCols << " " << tree.minVal << " " << tree.maxVal << "\n";
    tree.Ary2File(tree.newImgAry, outFile);

    inFile.close();
    outFile.close();
    logFile.close();
}