package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Login result transfer object for restful api services
 *
 */
public class LoginResult {

@SerializedName("key")
@Expose
public String key;
    @SerializedName("message")
    @Expose
    public String message;
}