package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;
import cat.urv.deim.exceptions.VertexNoTrobat;

public class Main {

    public static void main(String[] args) throws VertexNoTrobat, PosicioForaRang {

        long startTime = System.nanoTime();

        // Choose the file path you want to process
        // String filePath = "celegans_metabolic.net";
        // String filePath = "PGPgiantcompo.net"; TARDA MES DE 10 MIN
        // String filePath = "ERDOS971.net";
        // String filePath = "ERDOS982.net";
        String filePath = "test.net";

        Graf<Integer, String, Integer> graf = new Graf<>();
        GrafReader.readNetFile(filePath, graf);

        // graf.printGraph();

        GrafComunitats grafComunitats = new GrafComunitats();

        try {
            grafComunitats.identifyCommunities(graf, filePath);

        } catch (ElementNoTrobat e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long durationInMillis = (endTime - startTime) / 1000000;

        long minutes = durationInMillis / (60 * 1000);
        long seconds = (durationInMillis % (60 * 1000)) / 1000;

        System.out.println("Execution time: " + minutes + " minutes " + seconds + " seconds");
    }
}
