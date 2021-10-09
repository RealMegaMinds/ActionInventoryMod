/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.ping;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MinecraftServerPinger {

    public static PingResponse ping(String serverAddress, int serverPort, int timeout) throws IOException, PingParseException {
        try (Socket socket = openSocket(serverAddress, serverPort)) {
            socket.setSoTimeout(timeout);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Handshake packet
            ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
            DataOutputStream handshakeOut = new DataOutputStream(handshakeBytes);
            handshakeOut.writeByte(0x00); // Packet ID
            writeVarInt(handshakeOut, 4); // Protocol version
            writeString(handshakeOut, serverAddress);
            handshakeOut.writeShort(serverPort);
            writeVarInt(handshakeOut, 1); // Next state: status request
            writeByteArray(out, handshakeBytes.toByteArray());

            // Status request packet
            writeByteArray(out, new byte[]{0x00}); // Packet ID

            // Response packet
            readVarInt(in); // Packet size
            readVarInt(in); // Packet ID
            String responseJson = readString(in);

            return PingResponse.fromJson(responseJson);
        }
    }

    private static Socket openSocket(String serverAddress, int serverPort) throws IOException {
        return new Socket(serverAddress, serverPort);
    }

    private static String readString(DataInputStream in) throws IOException {
        byte[] bytes = readByteArray(in);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void writeString(DataOutputStream out, String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeByteArray(out, bytes);
    }

    private static byte[] readByteArray(DataInputStream in) throws IOException {
        int length = readVarInt(in);
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return bytes;
    }

    private static void writeByteArray(DataOutputStream out, byte[] bytes) throws IOException {
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((k & 0x80) != 0x80) {
                return i;
            }
        }
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while ((paramInt & 0xFFFFFF80) != 0x0) {
            out.writeByte((paramInt & 0x7F) | 0x80);
            paramInt >>>= 7;
        }
        out.writeByte(paramInt);
    }

}
