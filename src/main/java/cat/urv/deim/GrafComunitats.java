package cat.urv.deim;

import cat.urv.deim.exceptions.ArestaNoTrobada;
import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;
import cat.urv.deim.exceptions.VertexNoTrobat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class GrafComunitats {

    private HashMapIndirecte<Integer, Graf<Integer, String, Integer>> comunitats;

    public GrafComunitats() {
        this.comunitats = new HashMapIndirecte<>();
    }

    public void identifyCommunities(Graf<Integer, String, Integer> graph, String filePath)
            throws ElementNoTrobat, PosicioForaRang {
        // Implement Louvain algorithm to detect communities
        LouvainAlgorithm louvain = new LouvainAlgorithm(graph);
        louvain.detectCommunities();

        // Get the detected communities from the Louvain algorithm
        HashMapIndirecte<Integer, ILlistaGenerica<Integer>> communityMap = louvain.getCommunities();

        // Convert each community to a subgraph and store in the comunitats field
        ILlistaGenerica<Integer> keys = communityMap.obtenirClaus();
        for (int i = 0; i < keys.numElements(); i++) {
            Integer key = null;
            try {
                key = keys.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
                continue;
            }
            ILlistaGenerica<Integer> vertices = null;
            try {
                vertices = communityMap.consultar(key);
            } catch (ElementNoTrobat e) {
                e.printStackTrace();
                continue;
            }
            Graf<Integer, String, Integer> communityGraph = createSubGraph(graph, vertices);
            this.comunitats.inserir(key, communityGraph);
        }

        // Calculate modularity
        double modularity = louvain.calculateModularity();

        // Print communities
        printCommunities(modularity, filePath);
    }

    private static Graf<Integer, String, Integer> createSubGraph(Graf<Integer, String, Integer> graph,
            ILlistaGenerica<Integer> vertices) {
        // Create a new graph for the community
        Graf<Integer, String, Integer> communityGraph = new Graf<>();

        // Add vertices to the community graph
        for (int i = 0; i < vertices.numElements(); i++) {
            Integer vertex = null;
            try {
                vertex = vertices.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            if (vertex != null) {
                try {
                    communityGraph.inserirVertex(vertex, graph.consultarVertex(vertex));
                } catch (VertexNoTrobat e) {
                    e.printStackTrace();
                }
            }
        }

        // Add edges to the community graph
        for (int i = 0; i < vertices.numElements(); i++) {
            Integer v1 = null;
            try {
                v1 = vertices.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            if (v1 != null) {
                for (int j = i + 1; j < vertices.numElements(); j++) {
                    Integer v2 = null;
                    try {
                        v2 = vertices.consultar(j);
                    } catch (PosicioForaRang e) {
                        e.printStackTrace();
                    }
                    if (v2 != null) {
                        try {
                            if (graph.existeixAresta(v1, v2)) {
                                communityGraph.inserirAresta(v1, v2, graph.consultarAresta(v1, v2));
                            }
                        } catch (VertexNoTrobat | ArestaNoTrobada e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return communityGraph;
    }

    public void printCommunities(double modularity, String filePath) throws PosicioForaRang {
        ILlistaGenerica<Integer> communityIDs = comunitats.obtenirClaus();

        // Extract filename and extension from the file path
        String fileName = Paths.get(filePath).getFileName().toString();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));

        String outputFilePath = fileName + "_" + modularity + ".clu";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("Modularity: " + modularity + "\n\n");

            for (int i = 0; i < communityIDs.numElements(); i++) {
                Integer communityID = communityIDs.get(i);
                Graf<Integer, String, Integer> community = null;
                try {
                    community = comunitats.consultar(communityID);
                } catch (ElementNoTrobat e) {
                    e.printStackTrace();
                    continue;
                }
                writer.write("Community ID: " + communityID + "\n");
                ILlistaGenerica<Integer> vertexIDs = community.obtenirVertexIDs();
                for (int j = 0; j < vertexIDs.numElements(); j++) {
                    Integer vertexID = vertexIDs.get(j);
                    writer.write("Vertex ID: " + vertexID + "\n");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
