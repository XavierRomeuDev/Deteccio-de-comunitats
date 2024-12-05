package cat.urv.deim;

import cat.urv.deim.exceptions.ArestaNoTrobada;
import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;
import cat.urv.deim.exceptions.VertexNoTrobat;

public class LouvainAlgorithm {

    private Graf<Integer, String, Integer> graph;
    private HashMapIndirecte<Integer, Integer> nodeCommunityMap; // Map to store community assignment for each node
    private HashMapIndirecte<Integer, Integer> communitySizes; // Map to store size of each community
    private double modularityThreshold = 0.0001; // Threshold for modularity improvement to stop iterations
    private double maxModularity = Double.MIN_VALUE; // Maximum modularity achieved
    private HashMapIndirecte<Integer, ILlistaGenerica<Integer>> finalCommunities; // Final communities detected

    public LouvainAlgorithm(Graf<Integer, String, Integer> graph) {
        this.graph = graph;
        this.nodeCommunityMap = new HashMapIndirecte<>();
        this.communitySizes = new HashMapIndirecte<>();
        this.finalCommunities = new HashMapIndirecte<>();
    }

    public void detectCommunities() throws ElementNoTrobat, PosicioForaRang {
        initializeCommunities();

        boolean continueIteration = true;
        double previousModularity = calculateModularity();

        while (continueIteration) {
            ILlistaGenerica<Integer> vertexIDs = graph.obtenirVertexIDs();
            for (int i = 0; i < vertexIDs.numElements(); i++) {
                Integer currentNode = vertexIDs.consultar(i);
                moveNodeToBestCommunity(currentNode);
            }

            double currentModularity = calculateModularity();
            double modularityDifference = currentModularity - previousModularity;

            if (modularityDifference <= modularityThreshold) {
                continueIteration = false;
            }

            if (currentModularity > maxModularity) {
                maxModularity = currentModularity;
                updateFinalCommunities();
            }

            previousModularity = currentModularity;
            maxModularity = previousModularity;
        }
    }

    public void detectCommunitiesWithoutMovements() throws ElementNoTrobat, PosicioForaRang {
        initializeCommunities();

        boolean continueIteration = true;
        double previousModularity = calculateModularity();

        while (continueIteration) {
            ILlistaGenerica<Integer> vertexIDs = graph.obtenirVertexIDs();
            for (int i = 0; i < vertexIDs.numElements(); i++) {
                Integer currentNode = vertexIDs.consultar(i);
                moveNodeToBestCommunityWithoutMovements(currentNode);
            }

            double currentModularity = calculateModularity();
            double modularityDifference = currentModularity - previousModularity;
            continueIteration = false;

            if (currentModularity > maxModularity) {
                maxModularity = currentModularity;
                updateFinalCommunities();
            }

            previousModularity = currentModularity;
            maxModularity = previousModularity;
        }
    }

    private void initializeCommunities() {
        for (int i = 0; i < graph.numVertex(); i++) {
            Integer currentNode = null;
            try {
                currentNode = graph.obtenirVertexIDs().get(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            if (currentNode != null) {
                nodeCommunityMap.inserir(currentNode, currentNode);
                communitySizes.inserir(currentNode, 1);
            }
        }
    }

    private void moveNodeToBestCommunityWithoutMovements(Integer node) throws ElementNoTrobat {
        ILlistaGenerica<Integer> neighbors = null;
        try {
            neighbors = graph.obtenirVeins(node);
        } catch (VertexNoTrobat e) {
            e.printStackTrace();
        }

        double maxModularityGain = 0;
        Integer bestCommunity = nodeCommunityMap.consultar(node);

        for (int i = 0; i < neighbors.numElements(); i++) {
            Integer neighbor = null;
            try {
                neighbor = neighbors.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            if (neighbor != null) {
                double modularityGain = calculateModularityGain(node, neighbor);
                if (modularityGain > maxModularityGain) {
                    maxModularityGain = modularityGain;
                    bestCommunity = nodeCommunityMap.consultar(neighbor);
                }
            }
        }

        if (!bestCommunity.equals(nodeCommunityMap.consultar(node))) {
            nodeCommunityMap.inserir(node, bestCommunity);
            communitySizes.inserir(bestCommunity, communitySizes.consultar(bestCommunity) + 1);
            communitySizes.inserir(nodeCommunityMap.consultar(node),
                    communitySizes.consultar(nodeCommunityMap.consultar(node)) - 1);

        }
    }

    private void moveNodeToBestCommunity(Integer node) throws ElementNoTrobat {
        int maxMovements = 1;
        int maxConsecutiveImprovements = 1;

        int movements = 0;
        int consecutiveImprovements = 0;

        while (movements < maxMovements && consecutiveImprovements < maxConsecutiveImprovements) {
            ILlistaGenerica<Integer> neighbors = null;
            try {
                neighbors = graph.obtenirVeins(node);
            } catch (VertexNoTrobat e) {
                e.printStackTrace();
            }

            double maxModularityGain = 0;
            Integer bestCommunity = nodeCommunityMap.consultar(node);

            for (int i = 0; i < neighbors.numElements(); i++) {
                Integer neighbor = null;
                try {
                    neighbor = neighbors.consultar(i);
                } catch (PosicioForaRang e) {
                    e.printStackTrace();
                }
                if (neighbor != null) {
                    double modularityGain = calculateModularityGain(node, neighbor);
                    if (modularityGain > maxModularityGain) {
                        maxModularityGain = modularityGain;
                        bestCommunity = nodeCommunityMap.consultar(neighbor);
                    }
                }
            }

            if (!bestCommunity.equals(nodeCommunityMap.consultar(node))) {
                nodeCommunityMap.inserir(node, bestCommunity);
                communitySizes.inserir(bestCommunity, communitySizes.consultar(bestCommunity) + 1);
                communitySizes.inserir(nodeCommunityMap.consultar(node),
                        communitySizes.consultar(nodeCommunityMap.consultar(node)) - 1);

                consecutiveImprovements++;
            } else {
                consecutiveImprovements = 0;
            }

            movements++;
        }
    }

    public double calculateModularity() throws ElementNoTrobat {
        double modularity = 0;
        double m = graph.numArestes();

        ILlistaGenerica<Integer> vertices = graph.obtenirVertexIDs();
        for (int i = 0; i < vertices.numElements(); i++) {
            Integer node1 = null;
            try {
                node1 = vertices.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            if (node1 != null) {
                ILlistaGenerica<Integer> neighbors = null;
                try {
                    neighbors = graph.obtenirVeins(node1);
                } catch (VertexNoTrobat e) {
                    e.printStackTrace();
                }
                if (neighbors != null) {
                    for (int j = 0; j < neighbors.numElements(); j++) {
                        Integer node2 = null;
                        try {
                            node2 = neighbors.consultar(j);
                        } catch (PosicioForaRang e) {
                            e.printStackTrace();
                        }
                        if (node2 != null) {
                            int community1 = nodeCommunityMap.consultar(node1);
                            int community2 = nodeCommunityMap.consultar(node2);
                            if (community1 == community2) {
                                double Aij = 1.0;
                                try {
                                    Aij = graph.consultarAresta(node1, node2);
                                } catch (VertexNoTrobat | ArestaNoTrobada e) {
                                    e.printStackTrace();
                                }
                                modularity += Aij - (communitySizes.consultar(community1)
                                        * communitySizes.consultar(community2) / (2 * m));
                            }
                        }
                    }
                }
            }
        }

        return modularity / (2 * m);
    }

    private double calculateModularityGain(Integer node, Integer neighbor) throws ElementNoTrobat {
        double modularityGain = 0;
        double m = graph.numArestes();

        int community1 = nodeCommunityMap.consultar(node);
        int community2 = nodeCommunityMap.consultar(neighbor);

        double Aij = 1.0;
        try {
            Aij = graph.consultarAresta(node, neighbor);
        } catch (VertexNoTrobat | ArestaNoTrobada e) {
            e.printStackTrace();
        }

        modularityGain = 2 * Aij
                - (communitySizes.consultar(community1) + communitySizes.consultar(community2))
                        * (communitySizes.consultar(community1) + communitySizes.consultar(community2)) / (4 * m)
                - (communitySizes.consultar(community1) * communitySizes.consultar(community1) / (4 * m))
                - (communitySizes.consultar(community2) * communitySizes.consultar(community2) / (4 * m));

        return modularityGain;
    }

    private void updateFinalCommunities() throws ElementNoTrobat {
        finalCommunities.clear();

        ILlistaGenerica<Integer> vertices = graph.obtenirVertexIDs();
        for (int i = 0; i < vertices.numElements(); i++) {
            Integer node = null;
            try {
                node = vertices.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            if (node != null) {
                int community = nodeCommunityMap.consultar(node);
                if (!finalCommunities.contains(community)) {
                    finalCommunities.inserir(community, new LlistaNoOrdenada<>());
                }
                finalCommunities.consultar(community).inserir(node);
            }
        }
    }

    public double getMaxModularity() {
        return maxModularity;
    }

    public HashMapIndirecte<Integer, ILlistaGenerica<Integer>> getCommunities() {
        return finalCommunities;
    }
}
