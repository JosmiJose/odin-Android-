package btm.odinandroidwallet.networking.retrofit.pojo;
/**
 * Pojo of team details transfer object for restful api services
 *
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeamDetail implements Serializable{

@SerializedName("picture")
@Expose
public String picture;
@SerializedName("nationality")
@Expose
public String nationality;
@SerializedName("employee_name")
@Expose
public String employeeName;
@SerializedName("company_id")
@Expose
public Integer companyId;
@SerializedName("id")
@Expose
public Integer id;
@SerializedName("linkedin_profile")
@Expose
public String linkedinProfile;
@SerializedName("about")
@Expose
public String about;

}