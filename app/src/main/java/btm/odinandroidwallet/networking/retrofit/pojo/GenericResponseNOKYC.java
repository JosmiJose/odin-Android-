package btm.odinandroidwallet.networking.retrofit.pojo;

/**
 * Created by Waleed on 4/12/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of general response if there is no KYC transfer object for restful api services
 *
 */
public class GenericResponseNOKYC {
    @SerializedName("result")
    @Expose
    public GenericResult result;
    @SerializedName("status")
    @Expose
    public Boolean status;
}


