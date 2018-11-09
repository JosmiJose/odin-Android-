package btm.odinandroidwallet.networking.retrofit.pojo;



import com.google.gson.annotations.SerializedName;
/**
 * Pojo of Email transfer object for restful api
 *
 */
public class EmailResetBody {

    @SerializedName("email")
    public String email;
    public EmailResetBody(String email) {
        this.email = email;
    }
}


