package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of Register result transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterResult {

@SerializedName("message")
@Expose
public String message;

}