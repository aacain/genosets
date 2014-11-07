/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.mugsyannotator;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author aacain
 */
public class Graph {

    private HashMap<String, Contig> contigLookup; //contigId, contig

    public Graph(HashMap<String, Contig> contigLookup) {
        this.contigLookup = contigLookup;
    }

    public void addEdge(String pContigId, int pPosition, boolean pIsPositive, String nContigId, int nPosition, boolean nIsPositive) {
        Edge e = new Edge();
        e.previous = contigLookup.get(pContigId);
        e.pLocation = pPosition;
        e.pPositive = pIsPositive;
        e.next = contigLookup.get(nContigId);
        e.nLocation = nPosition;
        e.nPositive = nIsPositive;   
    }

    public static class Contig {

        protected String seqId;
        protected int length;
        protected LinkedList<Edge> previousEdges;
        protected LinkedList<Edge> nextEdges;

        public void addPreviousEdge(Edge edge) {
            if (previousEdges == null) {
                previousEdges = new LinkedList<Edge>();
            }
            previousEdges.add(edge);
        }

        public void addNextEdge(Edge edge) {
            if (nextEdges == null) {
                nextEdges = new LinkedList<Edge>();
            }
            nextEdges.add(edge);
        }
    }

    public static class Edge {

        protected Contig previous;
        protected Contig next;
        protected int pLocation;
        protected boolean pPositive;
        protected int nLocation;
        protected boolean nPositive;
    }
}
