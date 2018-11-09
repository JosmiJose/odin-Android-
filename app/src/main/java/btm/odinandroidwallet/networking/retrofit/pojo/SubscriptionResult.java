package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of subscription result transfer object for restful api services
 *
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionResult {

@SerializedName("values")
@Expose
public List<SubscriptionValue> values = null;

}