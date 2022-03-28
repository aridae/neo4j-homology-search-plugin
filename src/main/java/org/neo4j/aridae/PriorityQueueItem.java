package org.neo4j.aridae;

public class PriorityQueueItem {
    NodeAlignment na;
    int depth;

    public PriorityQueueItem(NodeAlignment na, int d) {
        this.na = na;
        this.depth = d;
    }
}
