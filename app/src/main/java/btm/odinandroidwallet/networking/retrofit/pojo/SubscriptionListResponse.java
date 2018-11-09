package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of subscription list response transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionListResponse {

@SerializedName("kyc_status")
@Expose
public String kycStatus;
@SerializedName("result")
@Expose
public SubscriptionResult result;
@SerializedName("status")
@Expose
public Boolean status;

}