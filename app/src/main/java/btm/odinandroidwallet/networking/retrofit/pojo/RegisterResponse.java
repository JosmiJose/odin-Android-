package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of Register Response transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

@SerializedName("status")
@Expose
public Boolean status;
@SerializedName("kyc_status")
@Expose
public String kycStatus;
@SerializedName("result")
@Expose
public RegisterResult result;

}