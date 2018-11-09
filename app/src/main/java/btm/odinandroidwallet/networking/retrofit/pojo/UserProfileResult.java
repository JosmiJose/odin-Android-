package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of User profile result transfer object for restful api services
 *
 */
public class UserProfileResult {

    @SerializedName("user_detail_id")
    @Expose
    public Integer userDetailId;
    @SerializedName("wallet_id")
    @Expose
    public Object walletId;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("middle_name")
    @Expose
    public String middleName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("date_of_birth")
    @Expose
    public String dateOfBirth;
    @SerializedName("nationality")
    @Expose
    public String nationality;
    @SerializedName("country_of_residence")
    @Expose
    public String countryOfResidence;
    @SerializedName("phone_country_code")
    @Expose
    public String phoneCountryCode;
    @SerializedName("contact_number_code")
    @Expose
    public Object contactNumberCode;
    @SerializedName("contact_number")
    @Expose
    public String contactNumber;
    @SerializedName("odinBalance")
    @Expose
    public double odinHolding;
    @SerializedName("ethBalance")
    @Expose
    public double etherBalance;
    @SerializedName("odin_points")
    @Expose
    public Integer odinPoints;
    @SerializedName("purpose_of_action")
    @Expose
    public String purposeOfAction;
    @SerializedName("planned_investment")
    @Expose
    public String plannedInvestment;
    @SerializedName("industry")
    @Expose
    public String industry;
    @SerializedName("work_type")
    @Expose
    public String workType;
    @SerializedName("tax_id")
    @Expose
    public String taxId;
    @SerializedName("wallet_open_date")
    @Expose
    public String walletOpenDate;
    @SerializedName("reason")
    @Expose
    public String reason;
    @SerializedName("address_line_1")
    @Expose
    public String addressLine1;
    @SerializedName("address_line_2")
    @Expose
    public String addressLine2;
    @SerializedName("address_line_3")
    @Expose
    public String addressLine3;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("postal_code")
    @Expose
    public String postalCode;
    @SerializedName("country_home")
    @Expose
    public String countryHome;
    @SerializedName("document_type")
    @Expose
    public String documentType;
    @SerializedName("document_number")
    @Expose
    public String documentNumber;
    @SerializedName("document_expiry")
    @Expose
    public String documentExpiry;
    @SerializedName("img_front")
    @Expose
    public Object imgFront;
    @SerializedName("img_back")
    @Expose
    public Object imgBack;
    @SerializedName("img_selfie")
    @Expose
    public Object imgSelfie;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("work_address")
    @Expose
    public String workAddress;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("kyc_flag")
    @Expose
    public String kycFlag;
    @SerializedName("kyc_message")
    @Expose
    public String kycMessage;
    @SerializedName("kyc_update_date")
    @Expose
    public String kycUpdateDate;
    @SerializedName("is_subscribed")
    @Expose
    public Boolean isSubscribed;
}