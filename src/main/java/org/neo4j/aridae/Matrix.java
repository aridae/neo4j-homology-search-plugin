package org.neo4j.aridae;

import java.util.Vector;

public class Matrix {
    int numRow = 0;          // number of actively used rows
    int numCol = 0;          // number of actively used columns
    Vector<Integer> data;    // actual data

    public Matrix(int numRow, int numCol) {
        this.numRow = numRow;
        this.numCol = numCol;
        this.data = new Vector<Integer>(numRow * numCol);
        for (int i = 0; i < this.data.capacity(); i++) {
            this.data.add(0);
        }
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < this.numRow; i++) {
            for (int j = 0; j < this.numCol; j++) {
                str += this.get(i, j).toString() + " ";
            }
            str += "\n";
        }
        return str; 
    }

    public void resize(int numRow, int numCol) {
        this.numRow = numRow;
        this.numCol = numCol;
        if (this.data.size() < (numRow * numCol)) {
            int gap = this.data.size() - numRow * numCol;
            for (int i = 0; i < gap; i++) {
                this.data.add(0);
            }
        }   
    }

    public Integer get(int i, int j) {
        return this.data.get(i * this.numCol + j);
    }

    public void set(int i, int j, Integer value) {
        this.data.set(i * numCol + j, value);
    }
}
