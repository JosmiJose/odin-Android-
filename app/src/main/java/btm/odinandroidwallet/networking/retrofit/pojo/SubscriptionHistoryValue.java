package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of subscription history value transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionHistoryValue {

    @SerializedName("points_used")
    @Expose
    public Integer pointsUsed;
    @SerializedName("user_subscription_id")
    @Expose
    public Integer userSubscriptionId;
    @SerializedName("subscription_id")
    @Expose
    public Integer subscriptionId;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("subscription_date")
    @Expose
    public String subscriptionDate;
    @SerializedName("subscription_end_date")
    @Expose
    public String subscriptionEndDate;
}