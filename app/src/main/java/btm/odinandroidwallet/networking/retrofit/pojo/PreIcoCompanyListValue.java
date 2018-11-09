package btm.odinandroidwallet.networking.retrofit.pojo;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Pre-ico company list value transfer object for restful api services
 *
 */
public class PreIcoCompanyListValue implements Serializable {

    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;
    @SerializedName("established_date")
    @Expose
    public String establishedDate;
    @SerializedName("ico_status")
    @Expose
    public String icoStatus;
    @SerializedName("company_name")
    @Expose
    public String companyName;
    @SerializedName("created_at")
    @Expose
    public Object createdAt;
    @SerializedName("wallet_id")
    @Expose
    public String walletId;
    @SerializedName("total_supply")
    @Expose
    public Integer totalSupply;
    @SerializedName("white_paper")
    @Expose
    public String whitePaper;
    @SerializedName("pre_ico_name")
    @Expose
    public String preIcoName;
    @SerializedName("company_logo")
    @Expose
    public String companyLogo;
    @SerializedName("contract_source_code")
    @Expose
    public String contractSourceCode;
    @SerializedName("contract_abi")
    @Expose
    public String contractAbi;
    @SerializedName("date_of_token_release")
    @Expose
    public String dateOfTokenRelease;
    @SerializedName("remain_premium_supply")
    @Expose
    public Double remainPremiumSupply;
    @SerializedName("website")
    @Expose
    public String website;
    @SerializedName("odin_equivalent")
    @Expose
    public Double odinEquivalent;
    @SerializedName("company_id")
    @Expose
    public Integer companyId;
    @SerializedName("company_review")
    @Expose
    public String companyReview;
    @SerializedName("industry_type")
    @Expose
    public String industryType;
    @SerializedName("pre_ico_ticker")
    @Expose
    public String preIcoTicker;
    @SerializedName("ico_start_date")
    @Expose
    public String icoStartDate;
    @SerializedName("contract_address")
    @Expose
    public String contractAddress;
    @SerializedName("no_of_decimals")
    @Expose
    public Integer noOfDecimals;
    @SerializedName("company_rating")
    @Expose
    public Integer companyRating;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("premium_supply")
    @Expose
    public Integer premiumSupply;
    @SerializedName("remain_supply")
    @Expose
    public Double remainSupply;
    @SerializedName("ico_end_date")
    @Expose
    public String icoEndDate;
    @SerializedName("company_user_id")
    @Expose
    public Integer companyUserId;
    @SerializedName("team_details")
    @Expose
    public List<TeamDetail> teamDetails = null;
    @SerializedName("company_overview")
    @Expose
    public String companyOverview;
    @SerializedName("odin_holdings")
    @Expose
    public Double odinHoldings;
}