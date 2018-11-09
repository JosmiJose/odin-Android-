package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of History data transfer object for restful api services
 *
 */
public class HistoryValue {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("tx_hash")
    @Expose
    public Object txHash;
    @SerializedName("amount")
    @Expose
    public Double amount;
    @SerializedName("odin_token_equivalent")
    @Expose
    public Double odinTokenEquivalent;
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("toAddress")
    @Expose
    public String toAddress;
    @SerializedName("tx_timestamp")
    @Expose
    public String txTimestamp;
    @SerializedName("is_db_token")
    @Expose
    public Boolean isDbToken;
    @SerializedName("fromAddress")
    @Expose
    public String fromAddress;
    @SerializedName("is_premium")
    @Expose
    public Boolean isPremium;
    @SerializedName("status_msg")
    @Expose
    public String statusMsg;

}