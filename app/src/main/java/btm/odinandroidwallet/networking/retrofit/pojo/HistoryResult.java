package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * Pojo of History transfer object for restful api services
 *
 */
public class HistoryResult {

@SerializedName("values")
@Expose
public List<HistoryValue> values = null;

}