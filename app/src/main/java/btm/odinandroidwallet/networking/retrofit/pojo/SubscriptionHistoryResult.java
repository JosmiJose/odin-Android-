package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of subscription history result transfer object for restful api services
 *
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionHistoryResult {

@SerializedName("values")
@Expose
public List<SubscriptionHistoryValue> values = null;

}