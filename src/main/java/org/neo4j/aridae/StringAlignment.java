package org.neo4j.aridae;

public class StringAlignment {

    String s1; 
    String s2;

    int s1len;
    int s2len;
    int score;

    Matrix scoreMtx;

    public StringAlignment() {
        s1len = 0;
        s2len = 0; 
        score = 0;
    }

    public StringAlignment(int s1len, int s2len, int score, Matrix scoreMtx) {
        this.s1len = s1len;
        this.s2len = s2len;
        this.score = score;
        this.scoreMtx = scoreMtx;
    }

    public StringAlignment(String s1, String s2, int s1len, int s2len, int score, Matrix scoreMtx) {
        this.s1 = s1;
        this.s2 = s2;
        this.s1len = s1len;
        this.s2len = s2len;
        this.score = score;
        this.scoreMtx = scoreMtx;
    }
}
