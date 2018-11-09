package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Portfolio value transfer object for restful api services
 *
 */
public class PortfolioValue {

    @SerializedName("token_name")
    @Expose
    public String tokenName;
    @SerializedName("holdings")
    @Expose
    public Double holdings;
    @SerializedName("is_db_token")
    @Expose
    public Boolean isDbToken;
    @SerializedName("token_long_name")
    @Expose
    public String tokenLongName;
    @SerializedName("odin_equivalent")
    @Expose
    public Double odinValue;
    @SerializedName("token_img")
    @Expose
    public String tokenImage;
    @SerializedName("decimals")
    @Expose
    public Integer noOfDecimals;

}