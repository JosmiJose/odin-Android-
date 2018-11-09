package btm.odinandroidwallet.networking.retrofit.pojo;

/**
 * Pojo of Password change transfer object for restful api services
 *
 */

import com.google.gson.annotations.SerializedName;

public class PasswordChange {

    @SerializedName("old_password")
    public String password;
    @SerializedName("new_password1")
    public String newPassword;
    @SerializedName("new_password2")
    public String newPassword2;

    public PasswordChange(String password, String newPassword,String newPassword2) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPassword2=newPassword2;
    }
}


