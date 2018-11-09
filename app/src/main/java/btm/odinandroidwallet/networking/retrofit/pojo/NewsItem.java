package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of News transfer object for restful api services
 *
 */
public class NewsItem {

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

/**
* No args constructor for use in serialization
* 
*/
public NewsItem() {
}

/**
* 
* @param updatedAt
* @param uploadImage
* @param body
* @param title
* @param newsId
* @param newFlag
* @param createdAt
* @param deletedAt
* @param subscribeId
*/
public NewsItem(Object uploadImage, Integer subscribeId, String body, String newFlag, String title, Object createdAt, String updatedAt, String deletedAt, Integer newsId) {
super();
this.uploadImage = uploadImage;
this.subscribeId = subscribeId;
this.body = body;
this.newFlag = newFlag;
this.title = title;
this.createdAt = createdAt;
this.updatedAt = updatedAt;
this.deletedAt = deletedAt;
this.newsId = newsId;
}

}