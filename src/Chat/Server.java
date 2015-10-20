package Chat;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Created by AlexanderFalk on 18/10/15.
 */
public class Server extends NetworkConnection
{

    private int port;

    public Server (int port, Consumer<Serializable> onRecieveCallback)
    {
        super(onRecieveCallback);
        this.port = port;

    }

    @Override
    protected boolean isServer() {
        return true;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
