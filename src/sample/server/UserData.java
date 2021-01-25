package sample.server;

import java.util.LinkedList;

public class UserData {
    int id;
    String login;
    byte[] passwordHash;
    LinkedList<String> playlistsNames;
    LinkedList<LinkedList<Integer>> playlists;
}
