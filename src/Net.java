import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Net {

    public static ArrayList<String> IPS = new ArrayList<>();


    public static void Net_STALK(String Request){ //NETWORK BLOCKCHAIN SERVER TALK
        try{
            Socket socket = new Socket("", Settings.API_NET_PORT);

            if(Request.matches("")){
                
            }

        }catch (Exception ex){

        }
    }

    public static void Net_Sync(){
        try {
            while(true) {
                ServerSocket serverSocket = new ServerSocket(Settings.NET_SYNC);
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

}
