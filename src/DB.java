import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class DB {

    public static void DB_GETIP(){
        try{
            //Create mysql connection
            String myDriver = "com.mysql.cj.jdbc.Driver";
            String URL = "jdbc:mysql://undeadinc.ca/u433204257_IPMGR";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(URL, Settings.DB_USR, Settings.DB_PWS);

            String query = "SELECT * FROM Nodes";//Create a Query for the DB

            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            while (rs.next())
            {
                String IP = rs.getString("IP");
                Double Ver = rs.getDouble("Ver");
                Net.Node_IPS.add(IP);
                // print the results
                System.out.format("%s, %s, \n", IP, Ver);
            }
            st.close();
            conn.close();


            return;

        }catch (Exception ex){
            System.out.println("EX: "+ ex);
        }
    }
    public static void PING_PORT(){
        while(true){
            try{
                System.out.println("WAITING FOR PING\n");
                ServerSocket serverSocket = new ServerSocket(20);
                Socket socket = serverSocket.accept();
                System.out.println("GOT PING!!\n");
                socket.close();
                serverSocket.close();
            }catch (Exception ex){

            }
        }
    }
    public static void SET_IP(){
        try{
            System.out.println("ATTEMPTING SETUP");
            Socket socket = new Socket("3.85.168.65", 10000);
            System.out.println("NODE SETUP NETWORKING FINISHED");
            socket.close();

        }catch (Exception ex){

        }
    }
}
