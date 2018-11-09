package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of Transaction history object transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionHistoryResponse {

@SerializedName("kyc_status")
@Expose
public String kycStatus;
@SerializedName("result")
@Expose
public HistoryResult result;
@SerializedName("status")
@Expose
public Boolean status;

}