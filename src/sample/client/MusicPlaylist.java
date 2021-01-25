package sample.client;

import java.util.ArrayList;

public class MusicPlaylist {
    private ArrayList<Integer> clips;

    public MusicPlaylist() {
        clips = new ArrayList<>();
    }

    public int Size() {
        return clips.size();
    }

    public void AddToEnd(int clipID) {
        clips.add(clipID);
    }

    public void Remove(int index) {
        clips.remove(index);
    }

    public void Remove(ClipData clipData) {
        clips.remove(clipData.id);
    }

    public int Get(int index) {
        return clips.get(index);
    }

    public void AddAll(int[] indexes) {
        for (int index : indexes) AddToEnd(index);
    }
}
