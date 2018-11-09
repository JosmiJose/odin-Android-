package btm.odinandroidwallet.networking.retrofit.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Portfolio result transfer object for restful api services
 *
 */
public class PortfolioResult {

@SerializedName("values")
@Expose
public List<PortfolioValue> values = null;

}