package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Pre-Ico company list response transfer object for restful api services
 *
 */
public class PreIcoCompanyListResponse {

@SerializedName("result")
@Expose
public PreIcoCompanyListResult result;
@SerializedName("status")
@Expose
public Boolean status;
@SerializedName("kyc_status")
@Expose
public String kycStatus;

}