package sample.server;


import java.util.LinkedList;

public class AccountManager {
    private static UserData[] users;

    public static void Init() {
        users = new UserData[1];
        users[0] = new UserData();
        users[0].id = 1;
        users[0].login = "misha";
        users[0].passwordHash = new byte[]{(byte)0x9f, (byte)0x86, (byte)0xd0, (byte)0x81, (byte)0x88, (byte)0x4c,
                                           (byte)0x7d, (byte)0x65, (byte)0x9a, (byte)0x2f, (byte)0xea, (byte)0xa0,
                                           (byte)0xc5, (byte)0x5a, (byte)0xd0, (byte)0x15, (byte)0xa3, (byte)0xbf,
                                           (byte)0x4f, (byte)0x1b, (byte)0x2b, (byte)0x0b, (byte)0x82, (byte)0x2c,
                                           (byte)0xd1, (byte)0x5d, (byte)0x6c, (byte)0x15, (byte)0xb0, (byte)0xf0,
                                           (byte)0x0a, (byte)0x08};
        users[0].playlists = new LinkedList<>();
        users[0].playlists.add(new LinkedList<>());
        users[0].playlists.get(0).add(0);
        users[0].playlists.get(0).add(1);
        users[0].playlistsNames = new LinkedList<>();
        users[0].playlistsNames.add("Test");
    }

    public static boolean IsUserExists(String login) {
        for (UserData user: users) {
            if(user.login.equals(login)) return true;
        }
        return false;
    }

    public static boolean IsUserExists(int userID) {
        for (UserData user: users) {
            if(user.id == userID) return true;
        }
        return false;
    }

    public static UserData GetUser(String login) {
        for (UserData user: users) {
            if(user.login.equals(login)) return user;
        }
        return null;
    }

    public static UserData GetUser(int userID) {
        for (UserData user: users) {
            if(user.id == userID) return user;
        }
        return null;
    }
}
