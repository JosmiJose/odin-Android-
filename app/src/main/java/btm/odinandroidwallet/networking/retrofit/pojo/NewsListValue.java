package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
/**
 * Pojo of News list value transfer object for restful api services
 *
 */
public class NewsListValue  implements Serializable {

@SerializedName("upload_image")
@Expose
public Object uploadImage;
@SerializedName("subscribe_id")
@Expose
public Integer subscribeId;
@SerializedName("body")
@Expose
public String body;
@SerializedName("new_flag")
@Expose
public String newFlag;
@SerializedName("title")
@Expose
public String title;
@SerializedName("description")
@Expose
public String description;
@SerializedName("created_at")
@Expose
public Object createdAt;
@SerializedName("updated_at")
@Expose
public String updatedAt;
@SerializedName("deleted_at")
@Expose
public String deletedAt;
@SerializedName("news_id")
@Expose
public Integer newsId;

}