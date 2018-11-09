package btm.odinandroidwallet.networking.retrofit.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Pre-ico company list result transfer object for restful api services
 *
 */
public class PreIcoCompanyListResult {

@SerializedName("user")
@Expose
public String user;
@SerializedName("values")
@Expose
public List<PreIcoCompanyListValue> values = null;

}