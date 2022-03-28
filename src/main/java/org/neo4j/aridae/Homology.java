package org.neo4j.aridae;

// import java.util.Map;
// import java.util.HashMap;
import java.util.Arrays;
// import java.util.Set;
// import java.util.Collection;
// import java.util.Iterator;
// import java.util.HashSet;
// import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

import java.lang.Object;

import org.neo4j.logging.Log;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
// import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
// import org.neo4j.graphdb.ResourceIterable;
// import org.neo4j.graphdb.Relationship;
// import org.neo4j.graphdb.RelationshipType;
import org.neo4j.procedure.*;

public class Homology{

    @Context
    public GraphDatabaseService db;

    @Context
    public Transaction tx;

	@Context
	public Log log;


    @Procedure( name = "com.github.aridae.hello", mode = Mode.READ)
    @Description("Just a simple 'Hello World!'")
    public Stream<StringOutput> hello(@Name("nodeId") long nodeId) {

        log.info("entered hello procedure");
 		String nodeName = "Null";

        Node node = tx.getNodeById(nodeId);
		nodeName = node.getProperty("name", new String("Node ID not existing")).toString();
        String msg = "Hello " + nodeName + "!";
        log.info(msg);

        return Arrays.stream(new StringOutput[]{new StringOutput(msg)});
    }


    @Procedure(name = "com.github.aridae.homology.homologousPaths", mode = Mode.READ)
    @Description("homology search problem in de Bruijn Graph")
    public Stream<HomologyOutput> homologousPaths(@Name("reference") String referenceString, @Name("K") Long K) {

        log.info("entered homologousPaths procedure");
        long start = System.currentTimeMillis();

        Object KMerValue = referenceString.substring(0, K.intValue());
        Label KMerLabel = Label.label("KMer");
        Node source = tx.findNode(KMerLabel, "value", KMerValue);

        StringAligner stringAligner = new StringAligner(10, 2, 3, log);
        GraphAligner graphAligner = new GraphAligner(1000000000, stringAligner, K.intValue(), log);

        GraphAlignment alignment = graphAligner.DFSAlnSource(referenceString, source);
        List<Node> path = new ArrayList<Node>();
        for (int i = 0; i < alignment.path.size(); i++) {
            path.add(alignment.path.get(i).node);
        }

        long runningTime = System.currentTimeMillis() - start;

        return Arrays.stream(new HomologyOutput[]{new HomologyOutput(path, alignment.pathScore.lastElement(), runningTime, alignment.OPTIMAL)});
    }


    public static class StringOutput {
        public String output;

        public StringOutput(String output) {
            this.output = output;
        }
    }


    public static class HomologyOutput {
        public List<Node> homologousPath;
        public long score;
        public long runningTime_ms;
        public Boolean OPTIMAL;

        public HomologyOutput(List<Node> homologousPath, long score, long runningTime_ms, Boolean optimal) {
            this.homologousPath = homologousPath;
            this.score = score;
            this.runningTime_ms = runningTime_ms;
            this.OPTIMAL = optimal;
        }
    }	
}