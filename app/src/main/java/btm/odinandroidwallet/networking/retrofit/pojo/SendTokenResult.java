package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of Send Token Result transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendTokenResult {

    @SerializedName("message")
    @Expose
    public String message;
}
