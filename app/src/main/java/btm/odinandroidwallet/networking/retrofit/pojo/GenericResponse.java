package btm.odinandroidwallet.networking.retrofit.pojo;

/**
 * Created by Waleed on 4/12/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of general response transfer object for restful api services
 *
 */
public class GenericResponse {

    @SerializedName("status")
    public boolean status;
    @SerializedName("kyc_status")
    @Expose
    public String kycStatus;
    @SerializedName("result")
    @Expose
    public GenericResult result;
}


