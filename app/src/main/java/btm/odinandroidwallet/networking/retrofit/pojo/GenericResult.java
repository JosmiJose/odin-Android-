package btm.odinandroidwallet.networking.retrofit.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Pojo of general result transfer object for restful api services
 *
 */
public class GenericResult {

@SerializedName("message")
@Expose
public String message;

}