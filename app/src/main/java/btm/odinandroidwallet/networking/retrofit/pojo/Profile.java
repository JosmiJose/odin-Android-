package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of profile transfer object for restful api services
 *
 */
public class Profile {

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
    @SerializedName("contact_number")
    @Expose
    public String contactNumber;
    @SerializedName("odin_holding")
    @Expose
    public double odinHolding;
    @SerializedName("profession")
    @Expose
    public String profession;
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
    @SerializedName("public_key")
    @Expose
    public String publicKey;
    @SerializedName("work_address")
    @Expose
    public String workAddress;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("kyc_flag")
    @Expose
    public String kycFlag;
    @SerializedName("phone_country_code")
    @Expose
    public String countryCode;
    @SerializedName("contact_number_code")
    @Expose
    public String contactNumberCode;
    @SerializedName("document_expiry")
    @Expose
    public String documentExpriy;
    @SerializedName("tax_id ")
    @Expose
    public String taxId;
    @SerializedName("industry")
    @Expose
    public int industry;
    @SerializedName("work_type")
    @Expose
    public int workType;
    @SerializedName("purpose_of_action")
    @Expose
    public int purposeOfAction;
    @SerializedName("planned_investment")
    @Expose
    public int plannedInvestment;


    /**
     * No args constructor for use in serialization
     *
     */
    public Profile() {
    }


    public Profile(String firstName, String middleName, String lastName, String dateOfBirth, String nationality, String countryOfResidence, String contactNumber, double odinHolding, String profession, String reason, String addressLine1, String addressLine2, String addressLine3, String city, String state, String postalCode, String countryHome, String documentType, String documentNumber, String publicKey, String workAddress, String gender, String kycFlag,String countryCode,String contactNumberCode,String documentExpiry,String taxId,int purposeOfAction,int industry,int workType,int plannedInvestment) {
        super();
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.countryOfResidence = countryOfResidence;
        this.contactNumber = contactNumber;
        this.odinHolding = odinHolding;
        this.profession = profession;
        this.reason = reason;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.countryHome = countryHome;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.publicKey = publicKey;
        this.workAddress = workAddress;
        this.gender = gender;
        this.kycFlag = kycFlag;
        this.countryCode=countryCode;
        this.contactNumberCode=contactNumberCode;
        this.documentExpriy=documentExpiry;
        this.taxId=taxId;
        this.industry=industry;
        this.workType=workType;
        this.plannedInvestment=plannedInvestment;
        this.purposeOfAction=purposeOfAction;
    }

}


