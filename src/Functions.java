public class Functions {

    public static void main(String[] args){
        Start_Miner_APIServer();
        Start_Node_Server();
        Notify_Node();
    }
    public static void Start_Miner_APIServer(){
        Thread Miner_Thread = new Thread(Net::Miner_API);
        Miner_Thread.start();
        return;
    }

    public static void Start_Node_Server(){
        Thread Server_Thread = new Thread(Net::S_Net_NODE_TALK);
        Server_Thread.start();
        return;
    }


    public static void Notify_Node(){
        while (true) {
            try {
                if(!Net.New_Transactions.isEmpty()){
                    Net.C_Net_NODE_TALK(null, "PUSH_TRANSACTION");
                    Net.Net_STALK("PUSH_N_TRANSACTIONS", null);
                }
                if(!Blockchain.MBlocks_NV.isEmpty()){
                    for(Block block: Blockchain.MBlocks_NV){
                        Net.C_Net_NODE_TALK(block, "PUSH_BLOCK");
                        Net.Net_STALK("PUSH_MBLOCK", block);
                    }
                }
                Net.Net_STALK("Get_Difficulty", null);
                Net.C_Net_NODE_TALK(null, "GET_TRANSACTIONS");
                Net.C_Net_NODE_TALK(null, "GET_BCUPDATE");

            } catch (Exception ex) {

            }
        }
    }
}
