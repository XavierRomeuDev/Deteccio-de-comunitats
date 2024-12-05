package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;
import cat.urv.deim.exceptions.VertexNoTrobat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGrafComunitats {

    @Test
    public void testGrafComunitats() throws VertexNoTrobat {
        Graf<Integer, String, Integer> graph = new Graf<>();
        graph.inserirVertex(1, "v1");
        graph.inserirVertex(2, "v2");
        graph.inserirVertex(3, "v3");
        graph.inserirAresta(1, 2, 1);
        graph.inserirAresta(2, 3, 1);

        GrafComunitats grafComunitats = new GrafComunitats();

        assertDoesNotThrow(() -> grafComunitats.identifyCommunities(graph, "test_output.txt"));
    }

    @Test
    public void testLouvainAlgorithm() throws VertexNoTrobat {
        String filePath = "USAir97.net";

        Graf<Integer, String, Integer> graph = new Graf<>();
        GrafReader.readNetFile(filePath, graph);

        LouvainAlgorithm louvainAlgorithm = new LouvainAlgorithm(graph);

        assertDoesNotThrow(louvainAlgorithm::detectCommunities);

        assertDoesNotThrow(() -> {
            assertNotNull(louvainAlgorithm.getCommunities());
            assertEquals(15, louvainAlgorithm.getCommunities().numElements());
        });
    }

    @Test
    public void testDetectCommunitiesExecutionTime() throws ElementNoTrobat, PosicioForaRang, VertexNoTrobat {
        String filePath = "USAir97.net";

        Graf<Integer, String, Integer> graph = new Graf<>();
        GrafReader.readNetFile(filePath, graph);

        LouvainAlgorithm louvainAlgorithm = new LouvainAlgorithm(graph);

        double modularityWithMovement = calculateModularityWithMovement(louvainAlgorithm);

        System.out.println("Modularity with movement: " + modularityWithMovement);

        double modularityWithoutMovement = calculateModularityWithoutMovement(louvainAlgorithm);

        System.out.println("Modularity without movement: " + modularityWithoutMovement);

        long startTime = System.currentTimeMillis();
        louvainAlgorithm.detectCommunities();
        long endTime = System.currentTimeMillis();
        long executionTimeMillis = endTime - startTime;
        System.out.println("Execution time: " + executionTimeMillis + " milliseconds");

        assertTrue(executionTimeMillis <= 600000, "Execution time exceeds 10 minutes");
    }

    @Test
    public void testModularityDifference1() throws ElementNoTrobat, PosicioForaRang, VertexNoTrobat {
        String filePath = "ERDOS971.net";

        Graf<Integer, String, Integer> graph = new Graf<>();
        GrafReader.readNetFile(filePath, graph);

        LouvainAlgorithm louvainAlgorithm = new LouvainAlgorithm(graph);

        double modularityWithMovement = calculateModularityWithMovement(louvainAlgorithm);

        System.out.println("Modularity with movement: " + modularityWithMovement);

        double modularityWithoutMovement = calculateModularityWithoutMovement(louvainAlgorithm);

        System.out.println("Modularity without movement: " + modularityWithoutMovement);

        assertTrue(
                modularityWithMovement > modularityWithoutMovement,
                "Modularity with movement should be greater than without movement");
    }

    @Test
    public void testModularityDifference2() throws ElementNoTrobat, PosicioForaRang, VertexNoTrobat {
        String filePath = "celegans_metabolic.net";

        Graf<Integer, String, Integer> graph = new Graf<>();
        GrafReader.readNetFile(filePath, graph);

        LouvainAlgorithm louvainAlgorithm = new LouvainAlgorithm(graph);

        double modularityWithMovement = calculateModularityWithMovement(louvainAlgorithm);

        System.out.println("Modularity with movement: " + modularityWithMovement);

        double modularityWithoutMovement = calculateModularityWithoutMovement(louvainAlgorithm);

        System.out.println("Modularity without movement: " + modularityWithoutMovement);

        assertTrue(
                modularityWithMovement > modularityWithoutMovement,
                "Modularity with movement should be greater than without movement");
    }

    @Test
    public void testModularityDifference3() throws ElementNoTrobat, PosicioForaRang, VertexNoTrobat {
        String filePath = "USAir97.net";

        Graf<Integer, String, Integer> graph = new Graf<>();
        GrafReader.readNetFile(filePath, graph);

        LouvainAlgorithm louvainAlgorithm = new LouvainAlgorithm(graph);

        double modularityWithMovement = calculateModularityWithMovement(louvainAlgorithm);

        System.out.println("Modularity with movement: " + modularityWithMovement);

        double modularityWithoutMovement = calculateModularityWithoutMovement(louvainAlgorithm);

        System.out.println("Modularity without movement: " + modularityWithoutMovement);

        assertTrue(
                modularityWithMovement > modularityWithoutMovement,
                "Modularity with movement should be greater than without movement");
    }

    private double calculateModularityWithMovement(LouvainAlgorithm louvainAlgorithm)
            throws ElementNoTrobat, PosicioForaRang, VertexNoTrobat {
        louvainAlgorithm.detectCommunities();
        return louvainAlgorithm.getMaxModularity();
    }

    private double calculateModularityWithoutMovement(LouvainAlgorithm louvainAlgorithm)
            throws ElementNoTrobat, PosicioForaRang, VertexNoTrobat {
        louvainAlgorithm.detectCommunitiesWithoutMovements();
        return louvainAlgorithm.getMaxModularity();
    }
}
