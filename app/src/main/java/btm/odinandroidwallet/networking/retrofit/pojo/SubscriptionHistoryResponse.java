package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of subscription history response transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionHistoryResponse {

@SerializedName("result")
@Expose
public SubscriptionHistoryResult result;
@SerializedName("status")
@Expose
public Boolean status;
@SerializedName("kyc_status")
@Expose
public String kycStatus;

}