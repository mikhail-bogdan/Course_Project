package sample.server;

import javafx.util.Pair;

import java.security.SecureRandom;
import java.util.LinkedList;

public class SessionManager {
    private static LinkedList<Pair<byte[], Integer>> sessions;


    public static void Init() {
        sessions = new LinkedList<>();
    }

    public static byte[] CreateSession(int userID) {
        byte[] session = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(session);
        sessions.add(new Pair<byte[], Integer>(session, userID));
        return session;
    }

    public static int GetUserID(byte[] session) {
        for (Pair<byte[], Integer> tmp_session: sessions) {
            if(isEquals(tmp_session.getKey(), session)) return tmp_session.getValue();
        }
        return -1;
    }

    public static boolean isEquals(byte[] session1, byte[] session2) {
        if(session1.length != session2.length) return false;
        else {
            for (int i = 0; i < session1.length; i++) {
                if(session1[i] != session2[i]) return false;
            }
        }
        return true;
    }

    public static void DeleteSession(byte[] session) {

    }
}
