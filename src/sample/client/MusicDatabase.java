package sample.client;

import java.sql.*;

public class MusicDatabase {
    private static Connection connection = null;

    public static void Init() {
        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:file:data/test.db", "SA", "");
            connection.setAutoCommit(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void AddClipData(ClipData clipData) {
        AddClipData(clipData.id, clipData.author, clipData.clipName, clipData.isClipSaved);
    }

    public static void AddClipData(int id, String author, String clipName, boolean isClipSaved) {
        try {
            PreparedStatement ps = connection.prepareStatement("insert into MUSIC_DATABASE VALUES(" + id + ", '" + author + "', '" + clipName + "', " + isClipSaved + ");");
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ClipData GetClipData(int clipId) {
        try {
            PreparedStatement ps = connection.prepareStatement("select * from MUSIC_DATABASE where AUDIOID = " + clipId + " ;");
            ResultSet set = ps.executeQuery();
            set.next();
            ClipData clipData = new ClipData();
            clipData.id = set.getInt(1);
            clipData.author = set.getString(2);
            clipData.clipName = set.getString(3);
            clipData.isClipSaved = set.getBoolean(4);
            return clipData;
        } catch (SQLException throwables) {
            return null;
        }
    }
}
