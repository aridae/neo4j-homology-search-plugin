package org.neo4j.aridae;

import java.util.Vector;

public class GraphAlignment {
    Vector<NodeAlignment> path;
    Vector<Integer> pathScore; 
    Boolean OPTIMAL = false;

    public GraphAlignment() {
        this.path = new Vector<NodeAlignment>();
        this.pathScore = new Vector<Integer>();
        this.OPTIMAL = false;
    }

    public void updatePath(NodeAlignment nodeAln, int depth) {
        path.add(nodeAln);
        int prevPathScore = (depth > 1) ? pathScore.get(depth-2) : 0;
        pathScore.add(prevPathScore + nodeAln.score);
    }

    public int getAlignedLength() {
        return (path.size() == 0) ? 0 : path.lastElement().refSliceEnd;
    }

    public int getScore() {
        return (path.size() == 0)  ? 0 : pathScore.lastElement();
    }

}
