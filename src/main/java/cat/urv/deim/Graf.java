package cat.urv.deim;

import cat.urv.deim.exceptions.ArestaNoTrobada;
import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;
import cat.urv.deim.exceptions.VertexNoTrobat;

public class Graf<K extends Comparable<K>, V, E> implements IGraf<K, V, E>, Comparable<Graf<K, V, E>> {

    final HashMapIndirecte<K, V> vertex;
    final HashMapIndirecte<K, LlistaNoOrdenada<Edge<K, E>>> arestes;

    public Graf() {
        vertex = new HashMapIndirecte<>();
        arestes = new HashMapIndirecte<>();
    }

    @Override
    // Metode per insertar un nou vertex al graf. El valor de K es l'identificador
    // del vertex i V es el valor del vertex
    public void inserirVertex(K key, V value) {
        vertex.inserir(key, value);
    }

    @Override
    // Metode per a obtenir el valor d'un vertex del graf a partir del seu
    // identificador
    public V consultarVertex(K key) throws VertexNoTrobat {
        try {
            return vertex.consultar(key);
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    public boolean existeixVertex(K vertex2) {
        // Get the list of vertex IDs
        ILlistaGenerica<K> vertexIDs = this.obtenirVertexIDs();

        // Iterate over all vertices in the graph
        for (int i = 0; i < vertexIDs.numElements(); i++) {
            K vertex = null;
            try {
                vertex = vertexIDs.get(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }

            // If the current vertex equals the vertex we're looking for, return true
            if (vertex.equals(vertex2)) {
                return true;
            }
        }
        // If we've checked all vertices and haven't found the vertex, return false
        return false;
    }

    @Override
    // Metode per a esborrar un vertex del graf a partir del seu identificador
    // Aquest metode tambe ha d'esborrar totes les arestes associades a aquest
    // vertex
    public void esborrarVertex(K key) throws VertexNoTrobat {
        boolean vertexEsborrat = false;
        try {
            vertex.esborrar(key);
            vertexEsborrat = true;

            ILlistaGenerica<K> veinsVertex = obtenirVeins(key);
            for (int i = 0; i < veinsVertex.numElements(); i++) {
                K idVertex;
                try {
                    idVertex = veinsVertex.consultar(i);
                } catch (PosicioForaRang e) {
                    e.printStackTrace();
                    continue;
                }
                esborrarAresta(key, idVertex);
            }
        } catch (ElementNoTrobat | ArestaNoTrobada e) {
            if (!vertexEsborrat) {
                throw new VertexNoTrobat();
            }
        }
    }

    @Override
    // Metode per a comprovar si hi ha algun vertex introduit al graf
    public boolean esBuida() {
        return vertex.esBuida();
    }

    @Override
    // Metode per a comprovar el nombre de vertexs introduits al graf
    public int numVertex() {
        return vertex.numElements();
    }

    @Override
    // Metode per a obtenir tots els ID de vertex de l'estrucutra
    public ILlistaGenerica<K> obtenirVertexIDs() {
        return vertex.obtenirClaus();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Operacions per a treballar amb les arestes

    @Override
    // Metode per a insertar una aresta al graf. Els valors de vertex 1 i vertex 2
    // son els vertex a connectar i E es el pes de la aresta
    // Si ja existeix l'aresta se li actualitza el seu pes
    public void inserirAresta(K v1, K v2, E pes) throws VertexNoTrobat {
        Edge<K, E> edge = new Edge<>(v1, v2, pes);
        LlistaNoOrdenada<Edge<K, E>> list;
        try {
            list = arestes.consultar(v1);
        } catch (ElementNoTrobat e) {
            list = new LlistaNoOrdenada<>();
            arestes.inserir(v1, list);
        }
        list.inserir(edge);

        try {
            list = arestes.consultar(v2);
        } catch (ElementNoTrobat e) {
            list = new LlistaNoOrdenada<>();
            arestes.inserir(v2, list);
        }
        list.inserir(edge);
    }

    @Override
    // Metode equivalent a l'anterior, afegint com a pes el valor null
    public void inserirAresta(K v1, K v2) throws VertexNoTrobat {
        inserirAresta(v1, v2, null);
    }

    @Override
    // Metode per a saber si una aresta existeix a partir dels vertex que connecta
    public boolean existeixAresta(K v1, K v2) throws VertexNoTrobat {
        try {
            LlistaNoOrdenada<Edge<K, E>> list = arestes.consultar(v1);
            for (int i = 0; i < list.numElements(); i++) {
                Edge<K, E> edge = list.consultar(i);
                if ((edge.getV1().equals(v1) && edge.getV2().equals(v2))
                        || (edge.getV1().equals(v2) && edge.getV2().equals(v1))) {
                    return true;
                }
            }
        } catch (ElementNoTrobat | PosicioForaRang e) {
            return false;
        }
        return false;
    }

    @Override
    // Metode per a obtenir el pes d'una aresta a partir dels vertex que connecta
    public E consultarAresta(K v1, K v2) throws VertexNoTrobat, ArestaNoTrobada {
        try {
            LlistaNoOrdenada<Edge<K, E>> list = arestes.consultar(v1);
            for (int i = 0; i < list.numElements(); i++) {
                Edge<K, E> edge = list.consultar(i);
                if ((edge.getV1().equals(v1) && edge.getV2().equals(v2))
                        || (edge.getV1().equals(v2) && edge.getV2().equals(v1))) {
                    return edge.getLabel();
                }
            }
        } catch (ElementNoTrobat | PosicioForaRang e) {
            throw new ArestaNoTrobada();
        }
        throw new ArestaNoTrobada();
    }

    @Override
    // Metode per a esborrar una aresta a partir dels vertex que connecta
    public void esborrarAresta(K v1, K v2) throws VertexNoTrobat, ArestaNoTrobada {
        if (!vertex.contains(v1) || !vertex.contains(v2)) {
            throw new VertexNoTrobat();
        }

        LlistaNoOrdenada<Edge<K, E>> arestesV1;
        try {
            arestesV1 = arestes.consultar(v1);
        } catch (ElementNoTrobat e) {
            throw new ArestaNoTrobada();
        }

        boolean arestaTrobada = false;
        for (int i = 0; i < arestesV1.numElements(); i++) {
            Edge<K, E> edge = null;
            try {
                edge = arestesV1.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            if ((edge.getV1().equals(v1) && edge.getV2().equals(v2))
                    || (edge.getV1().equals(v2) && edge.getV2().equals(v1))) {
                try {
                    arestesV1.esborrar(edge);
                } catch (ElementNoTrobat e) {
                    e.printStackTrace();
                }
                arestaTrobada = true;
                break;
            }
        }

        if (!arestaTrobada) {
            throw new ArestaNoTrobada();
        }
    }

    @Override
    // Metode per a comptar quantes arestes te el graf en total
    public int numArestes() {
        int totalArestes = 0;
        ILlistaGenerica<K> keys = arestes.obtenirClaus();
        for (int i = 0; i < keys.numElements(); i++) {
            try {
                K key = keys.consultar(i);
                totalArestes += arestes.consultar(key).numElements();
            } catch (ElementNoTrobat | PosicioForaRang e) {
                e.printStackTrace();
            }
        }
        return totalArestes;
    }

    @Override
    // Metode per a saber si un vertex te veins
    public boolean vertexAillat(K v1) throws VertexNoTrobat {
        if (!vertex.contains(v1)) {
            throw new VertexNoTrobat();
        }

        if (!arestes.contains(v1)) {
            return true;
        }

        LlistaNoOrdenada<Edge<K, E>> arestesV1;
        try {
            arestesV1 = arestes.consultar(v1);
        } catch (ElementNoTrobat e) {
            e.printStackTrace();
            return true;
        }
        return arestesV1.esBuida();
    }

    @Override
    // Metode per a saber quants veins te un vertex
    public int numVeins(K v1) throws VertexNoTrobat {
        if (!vertex.contains(v1)) {
            throw new VertexNoTrobat();
        }

        if (!arestes.contains(v1)) {
            return 0;
        }

        try {
            return arestes.consultar(v1).numElements();
        } catch (ElementNoTrobat e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    // Metode per a obtenir tots els ID de vertex veins d'un vertex
    public ILlistaGenerica<K> obtenirVeins(K v1) throws VertexNoTrobat {
        ILlistaGenerica<K> veins = new LlistaNoOrdenada<>();

        ILlistaGenerica<K> vertexKeys = vertex.obtenirClaus();

        for (int i = 0; i < vertexKeys.numElements(); i++) {
            K key = null;
            try {
                key = vertexKeys.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }

            if (!key.equals(v1) && existeixAresta(v1, key)) {
                veins.inserir(key);
            }
        }
        return veins;
    }

    @Override
    public ILlistaGenerica<K> obtenirNodesConnectats(K v1) throws VertexNoTrobat {
        ILlistaGenerica<K> connectedNodes = new LlistaNoOrdenada<>();

        HashMapIndirecte<K, Boolean> visited = new HashMapIndirecte<>();

        LlistaNoOrdenada<K> queue = new LlistaNoOrdenada<>();

        queue.inserir(v1);
        visited.inserir(v1, true);

        while (!queue.esBuida()) {
            try {
                K currentVertex = queue.consultar(0);
                queue.esborrar(currentVertex);
                connectedNodes.inserir(currentVertex);

                ILlistaGenerica<K> neighbors = obtenirVeins(currentVertex);

                for (int i = 0; i < neighbors.numElements(); i++) {
                    K neighbor = neighbors.consultar(i);
                    if (!visited.consultar(neighbor)) {
                        queue.inserir(neighbor);
                        visited.inserir(neighbor, true);
                    }
                }
            } catch (PosicioForaRang | ElementNoTrobat e) {
                e.printStackTrace();
            }
        }

        return connectedNodes;
    }

    private void obtenirNodesConnectatsRecursiu(K v1, ILlistaGenerica<K> groupIDs, HashMapIndirecte<K, Boolean> visited)
            throws VertexNoTrobat {
        if (v1 == null || visited.contains(v1)) {
            return;
        }

        visited.inserir(v1, true);
        groupIDs.inserir(v1);

        ILlistaGenerica<K> neighbors = obtenirVeins(v1);

        for (int i = 0; i < neighbors.numElements(); i++) {
            K neighbor = null;
            try {
                neighbor = neighbors.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }
            obtenirNodesConnectatsRecursiu(neighbor, groupIDs, visited);
        }
    }

    @Override
    // Metode per a obtenir els nodes que composen la Component Connexa mes gran del
    // graf
    public ILlistaGenerica<K> obtenirComponentConnexaMesGran() {
        int maxComponentSize = 0;
        ILlistaGenerica<K> largestConnectedComponent = new LlistaNoOrdenada<>();

        ILlistaGenerica<K> vertexIDs = vertex.obtenirClaus();

        for (int i = 0; i < vertexIDs.numElements(); i++) {
            K currentVertex = null;
            try {
                currentVertex = vertexIDs.consultar(i);
            } catch (PosicioForaRang e) {
                e.printStackTrace();
            }

            ILlistaGenerica<K> connectedComponent = null;
            try {
                connectedComponent = obtenirNodesConnectats(currentVertex);
            } catch (VertexNoTrobat e) {
                e.printStackTrace();
            }

            if (connectedComponent.numElements() > maxComponentSize) {
                maxComponentSize = connectedComponent.numElements();
                largestConnectedComponent = connectedComponent;
            }
        }
        return largestConnectedComponent;
    }

    public void printGraph() throws PosicioForaRang {
        try {
            System.out.println("Vertices:");
            ILlistaGenerica<K> vertexKeys = vertex.obtenirClaus();
            for (int i = 0; i < vertexKeys.numElements(); i++) {
                K vertexId = vertexKeys.consultar(i);
                System.out.println(vertexId + " -> " + vertex.consultar(vertexId));
            }

            System.out.println("\nArestes:");
            ILlistaGenerica<K> edgeKeys = arestes.obtenirClaus();
            for (int i = 0; i < edgeKeys.numElements(); i++) {
                K edgeKey = edgeKeys.consultar(i);
                ILlistaGenerica<Edge<K, E>> edges = arestes.consultar(edgeKey);
                for (int j = 0; j < edges.numElements(); j++) {
                    Edge<K, E> edge = edges.consultar(j);
                    System.out.println(edge.getV1() + " -- " + edge.getV2() + " -> " + edge.getLabel());
                }
            }
        } catch (ElementNoTrobat e) {
            System.out.println("Error: Element not found");
        }
    }

    public static class Edge<K extends Comparable<K>, E> implements Comparable<Edge<K, E>> {
        private final K v1;
        private final K v2;
        private final E label;

        public Edge(K v1, K v2, E label) {
            this.v1 = v1;
            this.v2 = v2;
            this.label = label;
        }

        public K getV1() {
            return v1;
        }

        public K getV2() {
            return v2;
        }

        public E getLabel() {
            return label;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Edge))
                return false;
            Edge<K, E> other = (Edge<K, E>) obj;
            return (v1 == other.v1 && v2 == other.v2) || (v1 == other.v2 && v2 == other.v1);
        }

        @Override
        public int compareTo(Edge<K, E> o) {
            int compareV1 = this.v1.compareTo(o.v1);
            if (compareV1 != 0) {
                return compareV1;
            }
            return this.v2.compareTo(o.v2);
        }
    }

    @Override
    public int compareTo(Graf<K, V, E> o) {
        int numVerticesThis = this.numVertex();
        int numVerticesOther = o.numVertex();
        return Integer.compare(numVerticesThis, numVerticesOther);
    }
}
