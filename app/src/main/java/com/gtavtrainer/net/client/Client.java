package com.gtavtrainer.net.client;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.gtavtrainer.net.data.EventData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Manoj Khanna
 */

public class Client implements Parcelable {

    public static final Creator<Client> CREATOR = new Creator<Client>() {

        @Override
        public Client createFromParcel(Parcel source) {
            return new Client(source);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }

    };

    private final String mIpAddress, mPort, mPassword;

    private Socket mSocket;
    private Reader mReader;
    private Writer mWriter;

    public Client(String ipAddress, String port, String password) {
        mIpAddress = ipAddress;
        mPort = port;
        mPassword = password;
    }

    private Client(Parcel parcel) {
        mIpAddress = parcel.readString();
        mPort = parcel.readString();
        mPassword = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIpAddress);
        dest.writeString(mPort);
        dest.writeString(mPassword);
    }

    public boolean connect() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(mIpAddress, Integer.parseInt(mPort)), 5000);
            socket.setSoTimeout(5000);

            Writer writer = new Writer(socket.getOutputStream());
            writer.writeLine(mPassword);

            Reader reader = new Reader(socket.getInputStream());
            String line = reader.readLine();
            if (line == null || line.equals("false")) {
                socket.close();
                reader.close();
                writer.close();

                return false;
            }

            mSocket = socket;
            mReader = reader;
            mWriter = writer;

            write(new EventData("client", "connected"));

            return true;
        } catch (IOException e) {
            Log.e(Client.class.getName(), e.getMessage(), e);

            return false;
        }
    }

    public void disconnect() {
        write(new EventData("client", "disconnected"));

        try {
            mSocket.close();
            mReader.close();
            mWriter.close();
        } catch (IOException e) {
            Log.e(Client.class.getName(), e.getMessage(), e);
        }
    }

    public EventData read() {
        String line = mReader.readLine();
        if (line != null) {
            return new EventData(line);
        }

        return null;
    }

    public void write(EventData eventData) {
        mWriter.writeLine(eventData.toString());
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public String getPort() {
        return mPort;
    }

    public String getPassword() {
        return mPassword;
    }

}
