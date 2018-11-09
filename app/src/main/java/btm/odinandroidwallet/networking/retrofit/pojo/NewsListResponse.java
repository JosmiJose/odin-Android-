package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of News list transfer object for restful api services
 *
 */
public class NewsListResponse {

@SerializedName("status")
@Expose
public Boolean status;
@SerializedName("result")
@Expose
public NewsListResult result;

}