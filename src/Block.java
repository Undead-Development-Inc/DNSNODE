import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Block implements Serializable{
    /////////TRANSACTIONS IN BLOCK//////////////


    //Timestamp ts = new Timestamp(time);
    ///////////////////////////
    /////THIS IS BLOCK!!!
    public ArrayList<byte[]> Verified_By = new ArrayList<>(); //SIGNATURE OF SIGNER's
    private String PrevHash;
    private String blockHash = "";
    private int nonce;
    public ArrayList<Transaction> transactions;
    public String Merkleroot = ""; //Final Hash of ALL Transactions in Block
    public ArrayList<String> MR_HASHLIST = new ArrayList<>(); //List of All Transaction Hashes in Block
    public long timeStamp;
    public float BlockReward = new Block_Reward().Block_Rew(); //Total block reward
    public int diff = 1;
    public PublicKey miner;
    public Wallet Block_Wallet = new Wallet();


    public Block(ArrayList<Transaction> transactions, String previousHash, long timeStamp, PublicKey miner) throws NullPointerException{

        this.Merkleroot = Calculate_MerkleRoot();
        this.timeStamp = timeStamp;
        this.transactions = transactions;
        this.blockHash = calculateBlockHash();
        this.PrevHash = previousHash;
        this.miner = miner;
        this.Cl();


    }
    public String Calculate_MerkleRoot(){
        String MR = "";
        if(this.transactions == null){
            return "";
        }
        MR = StringUtil.applySha256(this.transactions.toString());
        return MR;
    }

    public String calculateBlockHash() {
        String dataToHash = this.PrevHash
                + Long.toString(timeStamp)
                + Integer.toString(nonce)
                + transactions;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!this.blockHash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            System.out.println(Settings.RED_BOLD + Settings.BLACK_BACKGROUND +"Trying: "+ this.blockHash);
            this.blockHash = calculateBlockHash();
        }
        System.out.println(Settings.GREEN_BOLD + Settings.BLACK_BACKGROUND +"FOUND: "+ this.blockHash);
        this.Block_Reward_transaction();
        return this.blockHash;
    }
    public void clear_used_transactions(String BlockHash){
        ArrayList<Transaction> Remove_TR_List = new ArrayList<>();
        for(Block block: Blockchain.BlockChain){
            for(Transaction transaction: block.transactions){
                for(Transaction transaction1: Blockchain.Mine_Transactions){
                    if(transaction == transaction1){
                        Remove_TR_List.add(transaction);
                    }
                }
            }
        }
        for(Transaction transaction: Blockchain.Mine_Transactions){
            System.out.println("ALREADY INCLUDED TRANSACTIONS: "+ transaction);
        }
        Blockchain.Mine_Transactions.removeAll(Remove_TR_List);
        return;
    }
    public String getPreviousHash() {
        return this.PrevHash;
    }
    public String getBlockHash(){
        return this.blockHash;
    }
    private void Block_Reward_transaction(){
        Block_Wallet = new Wallet();
        ArrayList<Transaction> fees = new ArrayList<>();
        Double fees_total = 0.00;
        for(Transaction transaction: this.transactions){
            fees.add(transaction);
            fees_total += transaction.Fees;
        }
        Transaction transaction = new Transaction(Block_Wallet, StringUtil.applySha256(this.miner.toString()), new Block_Reward().Block_Rew(), Block_Wallet.privateKey);
        transaction.inputs.addAll(fees);
        transaction.value += fees_total;
        transaction.transaction_outputs.add(transaction);
        transaction.verified =1;
        transaction.Signature = StringUtil.applyECDSASig(Block_Wallet.privateKey, transaction.toString());
        this.transactions.add(transaction);

        return;
    }

    public void setBlock(String hash, String PrevHash, String Merkleroot, int Difficulty, int Nonce, long timestamp, ArrayList<Transaction> Data, PublicKey miner){
        this.blockHash = hash;
        this.PrevHash = PrevHash;
        this.Merkleroot = Merkleroot;
        this.diff = Difficulty;
        this.nonce = Nonce;
        this.timeStamp = timestamp;
        this.transactions = transactions;
        this.miner = miner;
        return;
    }
    public void Cl(){
        ArrayList<Transaction> transactions_remove = new ArrayList<>();
        for(Transaction transaction: Blockchain.Mine_Transactions){
            if(transaction.BlockHash.equals(Blockchain.LastBlockHash())){
                transactions_remove.add(transaction);
                System.out.println(Settings.PURPLE + "REMOVING TRANSACTION: "+ transaction);
            }
        }
        for(Transaction transaction: transactions_remove){
            Blockchain.Mine_Transactions.remove(transaction);
        }
        return;
    }

    public float getBlockReward() {
        return BlockReward;
    }


    public int get_nonce(){
        return this.nonce;
    }
    public ArrayList<Transaction> get_DATA(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        for(Transaction transaction: Blockchain.Mine_Transactions){
            transactions.add(transaction);

        }
//        for(Transaction transaction: transactions){
//            transactions.add(transaction);
//        }

        return transactions;
    }
    public int getDiff(){
        return this.diff;
    }



}

