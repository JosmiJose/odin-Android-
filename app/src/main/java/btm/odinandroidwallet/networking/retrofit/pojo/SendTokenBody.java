package btm.odinandroidwallet.networking.retrofit.pojo;

/**
 * Pojo of Send Token transfer object for restful api services
 *
 */

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendTokenBody {

    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("amount")
    @Expose
    public double amount;
    @SerializedName("toAddress")
    @Expose
    public String toAddress;
    @SerializedName("token_equivalent")
    @Expose
    public double tokenEquivalent;

    /**
     * No args constructor for use in serialization
     *
     */
    public SendTokenBody() {
    }

    /**
     *
     * @param amount
     * @param tokenEquivalent
     * @param token
     * @param toAddress
     */
    public SendTokenBody(String token, double amount, String toAddress, double tokenEquivalent) {
        super();
        this.token = token;
        this.amount = amount;
        this.toAddress = toAddress;
        this.tokenEquivalent = tokenEquivalent;
    }

}

