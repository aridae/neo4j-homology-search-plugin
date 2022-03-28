package org.neo4j.aridae;

// import java.util.Stack;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import org.neo4j.logging.Log;

public class GraphAligner {
    private int boundMaxNodeVisits;
    private StringAligner stringAligner;
    private int K = 4;
    private Log log;

    public class NodeResult {
        public Node node;

        public NodeResult(Relationship item) {            
            this.node = item.getEndNode();
        }
    }

    public GraphAligner(int maxVisits, StringAligner stringAligner, int K, Log log) {
        this.log = log;
        this.boundMaxNodeVisits = maxVisits;
        this.stringAligner = stringAligner;
        this.K = K;
    }

    public GraphAlignment DFSAlnSource(String referenceString, Node sourceNode) {

        this.log.info(String.format(" 1 - aligning %s to path with source - [%s, %o] node...", 
        referenceString, sourceNode.getProperty("value").toString(), sourceNode.getId()));

        GraphAlignment bestAlignment = new GraphAlignment();
        int nodeVisits = 0;
        NodeAlignment sourceAln = this.alignNode(referenceString, 0, sourceNode);


        NodeAlignment currNA = sourceAln;
        int currDepth = 1;
        int nextStart = currNA.refSliceEnd - K + 1;

        while (nodeVisits <= this.boundMaxNodeVisits) {

            log.info("updatnig path...");
            bestAlignment.updatePath(currNA, currDepth);

            if (referenceString.substring(nextStart).length() >= this.K) {

                // выбираем следующую ноду
                Vector<NodeAlignment> possibleAlignments = new Vector<NodeAlignment>();

                // все ребра "Precedes", которые выходят из этой ноды 
                Iterable<Relationship> relsIt = currNA.node.getRelationships(Direction.OUTGOING, RelationshipType.withName("Precedes"));
                ArrayList<Relationship> rels = new ArrayList<Relationship>();
                relsIt.forEach(rels::add);

                for (int i = 0; i < rels.size(); i++) {
                    possibleAlignments.add(alignNode(referenceString, nextStart, rels.get(i).getEndNode()));   
                }

                log.info("2-x we have possible alignments...");
                Collections.sort(possibleAlignments, (a, b) -> b.score - a.score);
                currNA = possibleAlignments.get(0);
                currDepth++;
                nextStart = currNA.refSliceEnd - K + 1;
            } else {
                break;
            }
        }

        return bestAlignment;
    }

    NodeAlignment alignNode(String referenceString, int startIndex, Node node){

        this.log.info(String.format("3-x - aligning %s to node - [%s, %o] node...", 
        referenceString.substring(startIndex), node.getProperty("value").toString(), node.getId()));

        // мы выравниваем кусок референсной строки точно после старт индекса
        int referenceAlignmentLen = referenceString.length() - startIndex;

        // пробуем выравнивать ко всей ноде целиком
        int nodeAlignmentLen = this.K;
        
        int alignmentLen = nodeAlignmentLen;
        if (referenceAlignmentLen < nodeAlignmentLen) {
            alignmentLen = referenceAlignmentLen;
        }

        this.log.info("LEN %o", alignmentLen);

        String nodeAlignmentString = node.getProperty("value").toString();
        String alignmentString;
        StringAlignment alignment;

        alignmentString = referenceString.substring(startIndex, startIndex + alignmentLen);
        this.log.info("LEN %o STRING %s", alignmentLen, alignmentString);
        alignment = this.stringAligner.align(nodeAlignmentString, alignmentString);
        alignment = this.stringAligner.trimTrailingGaps(alignment);
        alignmentLen = alignment.s2len;

        this.log.info(String.format("3x - we have node alignment %s [%o-%o] : %s [%o-%o] (%o of %o)", 
            alignment.s1, alignment.s1.length(), alignment.s1len, 
            alignment.s2, alignment.s2.length(), alignment.s2len,
            alignment.score,
            stringAligner.getMaxPossibleScore(alignment.s2len)
        ));
        
        float relScore = (float)alignment.score / (float) stringAligner.getMaxPossibleScore(alignment.s2len);
        return new NodeAlignment(node, 0, K, startIndex, startIndex + alignment.s2len, alignment.score, relScore);
    }


    public int getMaxPossibleScore(String refeString, GraphAlignment ga, int shortestPath) {

        // if the shortest path to the dst is longer than the unaligned pattern
        int unalnPattLen = refeString.length() - ga.getAlignedLength();
        int gapPenalty = (shortestPath > unalnPattLen) ? stringAligner.getGapScore() * (shortestPath - unalnPattLen) : 0;

        // upper bound to the score that can be attained
        return ga.getScore() + stringAligner.getMaxPossibleScore(unalnPattLen) + gapPenalty;
    }
}
