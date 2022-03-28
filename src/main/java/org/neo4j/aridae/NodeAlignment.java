package org.neo4j.aridae;

import org.neo4j.graphdb.Node;

public class NodeAlignment {
    Node node;

    // выравниваем к этому куску ноды
    int nodeSliceStart;
    int nodeSliceEnd;

    // этот кусок референса
    int refSliceStart;
    int refSliceEnd;

    // вот с этой скорой
    int score;
    float relativeScore;

    public NodeAlignment(Node node, int nodeS, int nodeE,  int refS, int refE, int score, float relScore) {
        this.node = node;
        this.nodeSliceStart = nodeS;
        this.nodeSliceEnd = nodeE;
        this.refSliceStart = refS;
        this.refSliceEnd = refE;
        this.score = score;
        this.relativeScore = relScore;
    }
}