package org.neo4j.aridae;

import org.neo4j.logging.Log;

public class StringAligner {

    private int M;                       // match score
    private int I;                       // mismatch penalty
    private int G;                       // gap score

    private Log log;


    public StringAligner(int M, int I, int G, Log log) {
        this.log = log;
        this.M = M;
        this.I = I;
        this.G = G;
    }

    private int S(char a, char b){
        if (a == b)
                return M;
        return I;
    }

    public int getGapScore() {
        return G;
    }

    public int getMaxPossibleScore(int l) {
        return M * l;
    }

    public StringAlignment align(String s1, String s2) {

        log.info(String.format("4-x trying to align %s with %s...", s1, s2));
        
        // reserve memory (+1 for first/row column with gap penalties)
        Matrix denseScoreMatrix = new Matrix(s1.length() + 1, s2.length() + 1); 
        
        log.info(String.format("4-x score matrix with sizes %o x %o [%o] created", denseScoreMatrix.numRow, denseScoreMatrix.numCol, denseScoreMatrix.data.size()));
        

        // initialize the borders of the matrix
        for (int i = 0; i <= s1.length(); i++)
                denseScoreMatrix.set(i, 0, i * G);

        // initialize the borders of the matrix
        for (int j = 0; j <= s2.length(); j++)
                denseScoreMatrix.set(0, j, j * G);

        // initialize the rest of the bulk of the matrix
        for (int i = 1; i <= (int)s1.length(); i++) {
            for (int j = 1; j <= (int)s2.length(); j++) {
                int diag = denseScoreMatrix.get(i-1, j-1) + S(s1.charAt(i-1), s2.charAt(j-1));
                int up   = denseScoreMatrix.get(i-1, j) + G;
                int left = denseScoreMatrix.get(i, j-1) + G;

                denseScoreMatrix.set(i, j, Math.max(diag, Math.max(up, left)));
            }
        }

        log.info("4-x matrix: " + denseScoreMatrix.toString());

        int score = denseScoreMatrix.get(s1.length(), s2.length()).intValue();
        log.info(String.format("4-x aligned %s to %s with score %o", s1, s2, score));
        return new StringAlignment(s1, s2, s1.length(), s2.length(), score, denseScoreMatrix);
    }

    public StringAlignment trimTrailingGaps(StringAlignment alnRes){

        log.info(String.format("5-x trimming trainling gaps: %s [%o-%o], %s [%o-%o]...", 
            alnRes.s1, alnRes.s1.length(), alnRes.s1len,
            alnRes.s2, alnRes.s2.length(), alnRes.s2len
        ));

        int i = alnRes.s1len;
        int j = alnRes.s2len;

        // remove gaps at the end of s2, if any
        while ( (i > 0) && (alnRes.scoreMtx.get(i, j) == alnRes.scoreMtx.get(i-1, j) + G) )
                i--;
        if (i < alnRes.s1len) {
            StringAlignment aln = new StringAlignment(alnRes.s1.substring(0, i), alnRes.s2.substring(0, j), i, j, alnRes.scoreMtx.get(i, j), alnRes.scoreMtx);
            log.info(String.format("5-x DONE 1 - trimming trainling gaps: %s [%o-%o], %s [%o-%o]...", 
                aln.s1, aln.s1.length(), aln.s1len,
                aln.s2, aln.s2.length(), aln.s2len
            ));
            return aln;
        }

        // remove gaps at the end of s1, if any
        // while ( (j > 0) && (alnRes.scoreMtx.get(i, j) == alnRes.scoreMtx.get(i, j-1) + G) )
        //         j--;
        // if (j < alnRes.s2len) {
        //     StringAlignment aln = new StringAlignment(alnRes.s1.substring(0, i), alnRes.s2.substring(0, j),  i, j, alnRes.scoreMtx.get(i, j), alnRes.scoreMtx);
        //     log.info(String.format("5-x DONE 2 - trimming trainling gaps: %s [%o-%o], %s [%o-%o]...", 
        //         aln.s1, aln.s1.length(), aln.s1len,
        //         aln.s2, aln.s2.length(), aln.s2len
        //     ));
        //     return aln;
        // }

        // there were no trailing gaps, return the global aln score
        log.info(String.format("5-x DONE 3 - trimming trainling gaps: %s [%o-%o], %s [%o-%o]...", 
            alnRes.s1, alnRes.s1.length(), alnRes.s1len,
            alnRes.s2, alnRes.s2.length(), alnRes.s2len
        ));
        return alnRes;
    }

}
