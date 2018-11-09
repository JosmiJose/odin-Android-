package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Portfolio response transfer object for restful api services
 *
 */
public class PortfolioResponse {

@SerializedName("result")
@Expose
public PortfolioResult result;
@SerializedName("kyc_status")
@Expose
public String kycStatus;
@SerializedName("status")
@Expose
public Boolean status;

}