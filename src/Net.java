import com.sun.jdi.ArrayReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Net {

    public static ArrayList<String> Node_IPS = new ArrayList<>();
    public static ArrayList<String> IPS = new ArrayList<>();
    public static ArrayList<Transaction> New_Transactions = new ArrayList<>();
    public static ArrayList<Block> SUSPECT_BLOCK = new ArrayList<>(); // this contains the blocks that are claimed to be in an update from a node, but no push found
    public static int current_difficulty = 1;


    public static void Miner_API(){
        try{
            while (true){
                ServerSocket serverSocket = new ServerSocket(Settings.Miner_API);
                Socket socket = serverSocket.accept();

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                String Request = (String)  objectInputStream.readObject();

                if(Request.matches("Get_BCUPDATE")){
                    objectOutputStream.writeObject(Blockchain.BlockChain);
                    objectOutputStream.writeObject(Blockchain.MBlocks_NV);
                    objectOutputStream.writeObject(Blockchain.Mine_Transactions);
                }

                if(Request.matches("PUSH_NEWBLOCK")){
                    Block block = (Block) objectInputStream.readObject();
                    if(!Blockchain.MBlocks_NV.contains(block)){
                        if(!Blockchain.BlockChain.contains(block)){
                            Blockchain.MBlocks_NV.add(block);
                            System.out.println("Added New Block");
                        }
                    }
                }

                if(Request.matches("PUSH_NEWTRANSACTION")){
                    Transaction transaction = (Transaction) objectInputStream.readObject();
                    if(!New_Transactions.contains(transaction)){
                        if(!Blockchain.Mine_Transactions.contains(transaction)){
                            New_Transactions.add(transaction);
                            Blockchain.Mine_Transactions.add(transaction);
                            System.out.println("ADDED TRANSACTION: "+ transaction + " : "+ Blockchain.Mine_Transactions.contains(transaction));
                        }
                    }
                }
                objectOutputStream.close();
                objectInputStream.close();
                socket.close();
                serverSocket.close();
            }
        }catch (Exception ex){

        }
    }

    public static void C_Net_NODE_TALK(Block nmblock,String Request){
        ArrayList<String> IP_CURR_ON = new ArrayList<>();
        try {
                if (Node_IPS.isEmpty()) {
                    DB.DB_GETIP();
                    if(Node_IPS.isEmpty()){
                        throw new Exception("STILL NO NODE IPS");
                    }
                }

                for (String IP : Node_IPS) {
                    IP_CURR_ON.add(IP);
                    System.out.println("TRYING TO CONNECT TO DNS NODE IP: "+ IP);
                    Socket socket = new Socket(IP, Settings.NET_NODE_CLIENT);
                    System.out.println("CONNECTED TO DNS NODE: "+ IP);
                    IP_CURR_ON.remove(IP);
                    socket.setSoTimeout(10);

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                    if(Request.matches("PUSH_TRANSACTION")){
                        oos.writeObject(New_Transactions);
                        System.out.println("SENT NEW TRANSACTIONS: "+ New_Transactions);
                    }

                    if(Request.matches("GET_TRANSACTIONS")){
                        ArrayList<Transaction> node_transactions = (ArrayList<Transaction>) ois.readObject();
                        for(Transaction transaction: node_transactions){
                            if(!Blockchain.Mine_Transactions.contains(transaction)){
                                System.out.println("Added New Transaction: "+ transaction);
                                Blockchain.Mine_Transactions.add(transaction);
                            }
                        }
                    }

                    if(Request.matches("GET_BCUPDATE")){
                        ArrayList<Block> node_bc = (ArrayList<Block>) ois.readObject();
                        for(Block block: node_bc){
                            if(!Blockchain.BlockChain.contains(block)){
                                SUSPECT_BLOCK.add(block);
                                System.out.println("ADDED BLOCK TO SUSPECT_BLOCK ARRAY: "+ block);
                            }
                        }
                    }

                    if(Request.matches("PUSH_BLOCK")){
                        oos.writeObject(nmblock);
                        System.out.println("SENT OVER: "+nmblock);
                    }

                    oos.close();
                    ois.close();
                    socket.close();

                }

        }catch (Exception ex){
            System.out.println(ex);
            String IP_TOUT = IP_CURR_ON.get(1);
            System.out.println("ISSUE ON: "+ IP_TOUT);
            Node_IPS.remove(IP_TOUT);
        }
    }

    public static void S_Net_NODE_TALK(){
        try{
            while(true){
                System.out.println("Starting SERVER NODE TALK");
                ServerSocket serverSocket = new ServerSocket(Settings.S_NET_NODE_CLIENT);
                Socket socket = serverSocket.accept();

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                String Request = (String) ois.readObject();

                if(Request.matches("GET_BCUPDATE")){
                    oos.writeObject(Blockchain.BlockChain);
                    System.out.println("Sent BCUPDATE to: "+ socket.getInetAddress());
                }

                if(Request.matches("PUSH_BLOCK")){
                    Block newMine = (Block) ois.readObject();
                    if(!Blockchain.MBlocks_NV.contains(newMine)){
                        if(!Blockchain.BlockChain.contains(newMine)){
                            Blockchain.MBlocks_NV.add(newMine);
                            System.out.println("Got New Block: "+ newMine + " From: "+ socket.getInetAddress());
                        }
                    }
                }

                if(Request.matches("PUSH_TRANSACTION")){
                    ArrayList<Transaction> new_transactions = (ArrayList<Transaction>) ois.readObject();
                    for(Transaction transaction: new_transactions){
                        if(!Blockchain.Mine_Transactions.contains(transaction)){
                            System.out.println("GOT NEW TRANSACTION: "+ transaction + " From: " + socket.getInetAddress());
                            Blockchain.Mine_Transactions.add(transaction);
                        }
                    }
                }
                if(Request.matches("GET_TRANSACTIONS")){
                    oos.writeObject(New_Transactions);
                    System.out.println("Sent Transactions: "+ New_Transactions);
                }

            }
        }catch (Exception ex){

        }
    }

    public static void Net_STALK(String Request, Block block){ //NETWORK BLOCKCHAIN SERVER TALK
        try{
            Socket socket = new Socket("", Settings.API_NET_PORT);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            if(Request.matches("PUSH_MBLOCK")){
                oos.writeObject(block);
                System.out.println("Sent Block: "+ block +" To Server");
            }

            if(Request.matches("PUSH_N_TRANSACTIONS")){
                oos.writeObject(New_Transactions);
                System.out.println("Sent Transactions to server: ");
                for(Transaction transaction: New_Transactions){
                    System.out.println("Transaction: "+ transaction);
                }
            }

            if(Request.matches("Get_Difficulty")){
                int difficulty = (int) ois.readObject();
                current_difficulty += difficulty;
            }

            ois.close();
            oos.close();
            socket.close();
        }catch (Exception ex){

        }
    }

    public static void Net_Sync(){
        try {
            while(true) {
                ServerSocket serverSocket = new ServerSocket(Settings.NET_SYNC);
                System.out.println("Starting NET_SYNC Server");
                Socket socket = serverSocket.accept();


                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                Package_Blocks pack = (Package_Blocks) ois.readObject();

                Blockchain.BlockChain.clear();
                Blockchain.MBlocks_NV.clear();
                Blockchain.Mine_Transactions.clear();

                for (Block block : pack.blockchain) {
                    Blockchain.BlockChain.add(block);
                    System.out.println("Added new Block to Chain: " + block);
                }
                for (Block block : pack.Newly_MinedBlocks) {
                    if (!Blockchain.MBlocks_NV.contains(block)) {
                        Blockchain.MBlocks_NV.add(block);
                        System.out.println("Added NewMined Block"+block);
                    }
                }
                for (Transaction transaction : pack.Newly_CreatedTransactions) {
                    if (!Blockchain.Mine_Transactions.contains(transaction)) {
                        Blockchain.Mine_Transactions.add(transaction);
                        System.out.println("Added New Transaction To Be Mined: "+transaction.transhash);
                    }
                }

                ois.close();
                socket.close();
                serverSocket.close();
            }

        }catch (Exception ex){

        }
    }

    public static void Network_Manager(){
        ArrayList<String> commands = new ArrayList<String>();
        while(true){
            try{
                ServerSocket serverSocket = new ServerSocket(6500);
                Socket socket = serverSocket.accept();
                System.out.println("NETWORK MANAGER CONNECTED");

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                String Request = (String) objectInputStream.readObject();

                if(Request.matches("UPDATE_NODE")){
                    Process p = Runtime.getRuntime().exec("git pull");
                    p.waitFor();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line=reader.readLine();

                    while (line != null){
                        System.out.println(line);
                        line = reader.readLine();
                    }
                }
                if(Request.matches("REBOOT")){
                    Process p = Runtime.getRuntime().exec("reboot");
                    p.waitFor();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line=reader.readLine();

                    while (line != null){
                        System.out.println(line);
                        line = reader.readLine();
                    }
                }

                objectInputStream.close();
                objectOutputStream.close();
                socket.close();
                serverSocket.close();
            }catch (Exception ex){

            }
        }
    }

}
