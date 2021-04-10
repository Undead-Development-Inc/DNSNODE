import java.io.Serializable;
import java.util.ArrayList;

public class Package_Blocks implements Serializable {

    public ArrayList<Block> blockchain = new ArrayList<Block>();
    public ArrayList<Block> Newly_MinedBlocks = new ArrayList<>();
    public ArrayList<Transaction> Newly_CreatedTransactions = new ArrayList<>();

}