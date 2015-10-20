package Chat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.*;
import java.util.function.Consumer;

/**
 * Created by AlexanderFalk on 18/10/15.
 */
public abstract class NetworkConnection
{
    private ConnectionThread connectionThread = new ConnectionThread();
    private Consumer<Serializable> onRecieveCallback;

    public NetworkConnection(Consumer<Serializable> onRecieveCallback)
    {
        this.onRecieveCallback = onRecieveCallback;
        connectionThread.setDaemon(true);
    }

    public void startConnection() throws Exception
    {
        connectionThread.start();
    }

    public void sendMessage(Serializable data) throws Exception
    {
        connectionThread.output.writeObject(data);

    }
    public void closeConnection() throws Exception
    {
        connectionThread.socket.close();
    }

    protected abstract boolean isServer();
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread
    {
        private Socket socket;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        @Override
        public void run()
        {
            try(ServerSocket server = isServer() ? new ServerSocket(getPort()) : null;
            Socket socket = isServer() ? server.accept() : new Socket(getIP(), getPort());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream()))
            {
                this.socket = socket;
                this.output = output;
                socket.setTcpNoDelay(true);
                onRecieveCallback.accept("Connection established!");

                while(true)
                {
                    Serializable data = (Serializable) input.readObject();
                    onRecieveCallback.accept(data);
                }

            }
            catch (Exception e)
            {
                onRecieveCallback.accept("Connection closed...");
            }
        }
    }

}
