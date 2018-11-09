package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of User profile response transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {

@SerializedName("result")
@Expose
public UserProfileResult result;
@SerializedName("kyc_status")
@Expose
public String kycStatus;
@SerializedName("status")
@Expose
public Boolean status;

}