package sample.server;

import javafx.util.Pair;

public class AudioDatabase {
    private static Pair<Integer, String[]>[] audiosData;

    public static void Init() {
        audiosData = new Pair[3];
        audiosData[0] = new Pair<>(0, new String[]{"Syd Matters", "Obstacles"});
        audiosData[1] = new Pair<>(1, new String[]{"Remi Gallego", "Malware Injection"});
        audiosData[2] = new Pair<>(2, new String[]{"Metric", "Risk"});
    }

    public static String[] GetAudioTextData(int audioID) {
        for(Pair<Integer, String[]> p : audiosData) {
            if(audioID == p.getKey()) return p.getValue();
        }
        return null;
    }

    public static int[] GetAllAudios() {
        int[] audios = new int[audiosData.length];
        for(int i = 0; i < audios.length; i++) {
            audios[i] = audiosData[i].getKey();
        }
        return audios;
    }
}
