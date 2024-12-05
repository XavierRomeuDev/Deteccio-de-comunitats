package cat.urv.deim;

import java.io.*;

import cat.urv.deim.exceptions.VertexNoTrobat;

public class GrafReader {
    private static final String VERTICES_SECTION = "*Vertices";
    private static final String EDGESLIST_SECTION = "*Edgeslist";
    private static final String ARCS_SECTION = "*Arcs";
    private static final String EDGES_SECTION = "*Edges";

    public static void readNetFile(String filePath, Graf<Integer, String, Integer> graf) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean readingVertices = false;
            boolean readingEdgesList = false;
            boolean readingArcs = false;
            boolean readingEdges = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (line.startsWith(VERTICES_SECTION)) {
                    readingVertices = true;
                    readingEdgesList = false;
                    readingArcs = false;
                    readingEdges = false;
                    continue;
                } else if (line.startsWith(EDGESLIST_SECTION)) {
                    readingVertices = false;
                    readingEdgesList = true;
                    readingArcs = false;
                    readingEdges = false;
                    continue;
                } else if (line.startsWith(ARCS_SECTION)) {
                    readingVertices = false;
                    readingEdgesList = false;
                    readingArcs = true;
                    readingEdges = false;
                    continue;
                } else if (line.startsWith(EDGES_SECTION)) {
                    readingVertices = false;
                    readingEdgesList = false;
                    readingArcs = false;
                    readingEdges = true;
                    continue;
                }

                if (readingVertices) {
                    processVertex(line, graf);
                } else if (readingEdgesList) {
                    processEdgesList(line, graf);
                } else if (readingArcs || readingEdges) {
                    processConnection(line, graf, readingArcs ? "Arc" : "Edge");
                }
            }
        } catch (IOException | VertexNoTrobat e) {
            e.printStackTrace();
        }
    }

    private static void processVertex(String line, Graf<Integer, String, Integer> graf) {
        String[] parts = line.split("\\s+");
        int vertexId = Integer.parseInt(parts[0].trim());
        String vertexLabel = parts[1].replace("\"", "").trim();

        graf.inserirVertex(vertexId, vertexLabel);
    }

    private static void processEdgesList(String line, Graf<Integer, String, Integer> graf) throws VertexNoTrobat {
        String[] parts = line.trim().split("\\s+");
        int source = Integer.parseInt(parts[0].trim());

        for (int i = 1; i < parts.length; i++) {
            int target = Integer.parseInt(parts[i].trim());
            graf.inserirAresta(source, target, 1);
        }
    }

    private static void processConnection(String line, Graf<Integer, String, Integer> graf, String type)
            throws VertexNoTrobat {
        String[] parts = line.trim().split("\\s+");
        int source = Integer.parseInt(parts[0].trim());
        int target = Integer.parseInt(parts[1].trim());
        int weight = 1;

        graf.inserirAresta(source, target, weight);
    }
}
