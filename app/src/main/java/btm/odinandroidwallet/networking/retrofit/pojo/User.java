package btm.odinandroidwallet.networking.retrofit.pojo;

/**
 * Pojo of User transfer object for restful api services
 *
 */

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}


