package btm.odinandroidwallet.networking.retrofit.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Points history values transfer object for restful api services
 *
 */
public class PointsHistoryResult {

@SerializedName("values")
@Expose
public List<PointsHistoryValue> values = null;

}