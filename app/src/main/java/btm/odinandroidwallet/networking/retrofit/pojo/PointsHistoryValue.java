package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Point history value transfer object for restful api services
 *
 */
public class PointsHistoryValue {

@SerializedName("point_added")
@Expose
public Integer pointAdded;
@SerializedName("point_date")
@Expose
public String pointDate;
@SerializedName("user_id")
@Expose
public Integer userId;
@SerializedName("points_history_id")
@Expose
public Integer pointsHistoryId;

}