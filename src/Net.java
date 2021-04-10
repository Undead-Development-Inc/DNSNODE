import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Net {

    public static ArrayList<String> IPS = new ArrayList<>();


    public static void Net_Sync(){
        try {
            ServerSocket serverSocket = new ServerSocket(9000);
            Socket socket = serverSocket.accept();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());


        }catch (Exception ex){

        }
    }

}
