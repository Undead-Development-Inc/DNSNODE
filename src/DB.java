import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class DB {

    public static void IP_NET(){
        while(true) {
            try {
                System.out.println("TRYING TO CONNECT TO IPMGR");
                Socket socket = new Socket("35.175.113.12", 2000);
                System.out.println("CONNECTED TO IPMGR");

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
            } catch (SocketTimeoutException ex) {
                System.out.println("FAILED TO CONNECT TO IPMGR");
                System.exit(100);
            }
            catch (IOException IOE){
                System.out.println("IOEXEPTION1");
            }
            catch (ClassNotFoundException CNFE){
                System.out.println("Class Not Found Exeption");
            }
        }
    }
}
