package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Points History Response transfer object for restful api services
 *
 */
public class PointsHistoryResponse {

@SerializedName("kyc_status")
@Expose
public String kycStatus;
@SerializedName("result")
@Expose
public PointsHistoryResult result;
@SerializedName("status")
@Expose
public Boolean status;

}