import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;

public class Transaction implements Serializable {


    public PublicKey from_address;
    public String Recpt_address;
    public double value;
    public String transhash;
    public int verified;
    public Boolean ISmined;
    public float Fees;
    public byte[] Signature;
    public String BlockHash;
    public PublicKey transaction_MIN;
    public static ArrayList<Transaction> transaction_outputs = new ArrayList<>();
    public static ArrayList<Transaction> inputs = new ArrayList<>();


    public Transaction(Wallet from, String recpt, float value, PrivateKey fromkey){
        this.from_address = from.publicKey;
        this.Recpt_address = recpt;
        this.ISmined = false;
        this.verified = 1;
        this.value = value;
        Random random = new Random();
        this.transhash = StringUtil.applySha256(from + recpt + value + BlockHash);
        this.BlockHash = "";
        this.inputs = from.UTXO(from);
        System.out.println(this.inputs);



    }


}
