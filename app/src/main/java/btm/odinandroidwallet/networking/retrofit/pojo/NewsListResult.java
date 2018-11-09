package btm.odinandroidwallet.networking.retrofit.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of News List Result transfer object for restful api services
 *
 */
public class NewsListResult {

@SerializedName("values")
@Expose
public List<NewsListValue> values = null;

}