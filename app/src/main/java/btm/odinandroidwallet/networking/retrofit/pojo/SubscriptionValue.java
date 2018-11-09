package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of subscription value transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionValue {
    @SerializedName("no_of_days")
    @Expose
    public Integer noOfDays;
    @SerializedName("subscribe_id")
    @Expose
    public Integer subscribeId;

    @SerializedName("manager_id")
    @Expose
    public Integer managerId;
    @SerializedName("created_at")
    @Expose
    public Object createdAt;
    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;
    @SerializedName("points")
    @Expose
    public Integer points;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;


}


