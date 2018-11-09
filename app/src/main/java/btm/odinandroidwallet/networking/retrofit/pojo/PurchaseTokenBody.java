package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of Purchase token body transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurchaseTokenBody {

    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("amount")
    @Expose
    public double amount;
    @SerializedName("odin_equivalent")
    @Expose
    public double tokenEquivalent;

    /**
     * No args constructor for use in serialization
     *
     */
    public PurchaseTokenBody() {
    }


    public PurchaseTokenBody(String token, double amount, double tokenEquivalent) {
        super();
        this.token = token;
        this.amount = amount;
        this.tokenEquivalent = tokenEquivalent;
    }

}

