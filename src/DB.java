import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class DB {

    public static void IP_NET(){
        while(true) {
            try {
                Socket socket = new Socket("127.0.0.1", 2000);

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                ArrayList<String> IPs = (ArrayList<String>) objectInputStream.readObject();

                for(String ip: IPs){
                    if(!Net.Node_IPS.contains(ip)){
                        Net.Node_IPS.add(ip);
                    }
                }

                objectInputStream.close();
                objectOutputStream.close();
                socket.close();
            } catch (Exception ex) {

            }
        }
    }
}
