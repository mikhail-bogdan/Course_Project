package sample.shared.packets.response;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class PacketGetUserPlaylistsResponse implements IByteConvertible {
    public final byte type = 16;
    public int size = -1;


    private short playlistsSize;
    public String[] playlistsNames;
    public LinkedList<LinkedList<Integer>> playlists;


    @Override
    public byte[] toBytes() {
        size = 2;
        for (LinkedList<Integer> l: playlists) {
            size += l.size() * 4 + 2;
        }
        for(String playlistName : playlistsNames) {
            size += playlistName.length() + 2;
        }
        playlistsSize = (short) playlistsNames.length;
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(ByteConverter.shortToBytes((short) playlistsSize), 0, data, 5, 2);
        int pos = 7;
        for(int i = 0; i < playlistsSize; i++) {
            LinkedList<Integer> playlist = playlists.get(i);
            System.arraycopy(ByteConverter.shortToBytes((short) playlistsNames[i].length()), 0, data, pos, 2);
            pos += 2;
            System.arraycopy(playlistsNames[i].getBytes(), 0, data, pos, playlistsNames[i].length());
            pos += playlistsNames[i].length();
            System.arraycopy(ByteConverter.shortToBytes((short) playlist.size()), 0, data, pos, 2);
            pos += 2;
            for (Integer value : playlist) {
                System.arraycopy(ByteConverter.intToBytes(value), 0, data, pos, 4);
                pos += 4;
            }
        }
        return data;
    }

    @Override
    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[4];
        stream.read(buffer);
        int size = ByteConverter.bytesToInt(buffer, 0);
        stream.read(buffer, 0, 2);
        playlistsSize = ByteConverter.bytesToShort(buffer, 0);
        playlistsNames = new String[playlistsSize];
        playlists = new LinkedList<>();
        for(int i = 0; i < playlistsSize; i++) {
            LinkedList<Integer> list = new LinkedList<>();
            stream.read(buffer, 0, 2);
            short playlistNameLen = ByteConverter.bytesToShort(buffer, 0);
            StringBuilder builder = new StringBuilder();
            for(int j = 0; j < playlistNameLen; j++)
                builder.append((char)stream.read());
            playlistsNames[i] = builder.toString();
            stream.read(buffer, 0, 2);
            short playlistSize = ByteConverter.bytesToShort(buffer, 0);
            for(int j = 0; j < playlistSize; j++) {
                stream.read(buffer);
                list.add(ByteConverter.bytesToInt(buffer, 0));
            }
            playlists.add(list);
        }
        return true;
    }
}
