package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Login response transfer object for restful api services
 *
 */
public class LoginResponse {

    @SerializedName("kyc_status")
    @Expose
    public String kycStatus;
    @SerializedName("is_email_verified")
    @Expose
    public Boolean isEmailVerified;
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("result")
    @Expose
    public LoginResult result;

}