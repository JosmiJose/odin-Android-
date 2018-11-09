package btm.odinandroidwallet.networking.retrofit;

import java.util.Map;

import btm.odinandroidwallet.networking.retrofit.pojo.EmailResetBody;
import btm.odinandroidwallet.networking.retrofit.pojo.GenericResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.GenericResponseNOKYC;
import btm.odinandroidwallet.networking.retrofit.pojo.LoginResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.NewsListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PasswordChange;
import btm.odinandroidwallet.networking.retrofit.pojo.PointsHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PurchaseTokenBody;
import btm.odinandroidwallet.networking.retrofit.pojo.RegisterResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SendTokenBody;
import btm.odinandroidwallet.networking.retrofit.pojo.SendTokenResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.TransactionHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.User;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface APIInterface {
    /**
     * Function/Module Name : getNews
     * Purpose : is a GET Api endpoint that return list of news
     * Input: Authorization token
     * Output :  List of news
     **/
    @GET("/api/user/activity/news/")
    Call<NewsListResponse> getNews(@Header("Authorization") String auth);

    /**
     * Function/Module Name : createUser
     * Purpose : is a POST Api endpoint that register a user
     * Input: User
     * Output :  Register Response
     **/
    @POST("/api/users/register/")
    Call<RegisterResponse> createUser(@Body User user);

    /**
     * Function/Module Name : changePassword
     * Purpose : is a POST Api endpoint that change the user's password
     * Input: new password,  Authorization token
     * Output :  Register Response
     **/
    @POST("/api/users/password/change/")
    Call<RegisterResponse> changePassword(@Body PasswordChange passwordChange, @Header("Authorization") String auth);

    /**
     * Function/Module Name : login
     * Purpose : is a POST Api endpoint that login a user
     * Input: User
     * Output :  Login Response
     **/
    @POST("/api/users/login/")
    Call<LoginResponse> login(@Body User user);

    /**
     * Function/Module Name : resetPassword
     * Purpose : is a POST Api endpoint that rest the user's password
     * Input: EmailResetBody
     * Output :  Generic Response
     **/
    @POST("/api/users/password/reset/")
    Call<GenericResponse> resetPassword(@Body EmailResetBody email);

    /**
     * Function/Module Name : logout
     * Purpose : is a POST Api endpoint that logout a user
     * Input: Authorization token
     * Output :  Generic Response
     **/
    @POST("/api/users/logout/")
    Call<GenericResponse> logout(@Header("Authorization") String auth);

    /**
     * Function/Module Name : createProfile
     * Purpose : is a PUT Api endpoint that update a user info
     * Input: Authorization token, front-image , back-image, selfie image, User details , numbers map
     * Output :  Generic Response
     **/
    @Multipart
    @PUT("/api/users/details/update/")
    Call<GenericResponse> createProfile(@Header("Authorization") String auth, @Part MultipartBody.Part fImage, @Part MultipartBody.Part bImage, @Part MultipartBody.Part sImage, @PartMap() Map<String, RequestBody> partMap, @PartMap() Map<String, Integer> numberPartMap);

    /**
     * Function/Module Name : getUserProfile
     * Purpose : is a GET Api endpoint that update a user info
     * Input: Authorization token
     * Output :  User profile Response
     **/
    @GET("/api/users/details/get/")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String auth);

    /**
     * Function/Module Name : sendToken
     * Purpose : is a POST Api endpoint that send token to another user
     * Input: Authorization token, Send Token Body (user)
     * Output :  Send Token Response
     **/
    @POST("/api/user/transaction/sendToken/")
    Call<SendTokenResponse> sendToken(@Header("Authorization") String auth, @Body SendTokenBody user);

    /**
     * Function/Module Name : purchaseToken
     * Purpose : is a POST Api endpoint that purchase a token from another user
     * Input: Authorization token, Purchase Token Body (user)
     * Output :  Send Token Response
     **/
    @POST("api/user/transaction/purchase/")
    Call<SendTokenResponse> purchaseToken(@Header("Authorization") String auth, @Body PurchaseTokenBody user);

    /**
     * Function/Module Name : getHistoryItems
     * Purpose : is a GET Api endpoint that get list of history transactions
     * Input: Authorization token
     * Output :  Transaction History Response
     **/
    @GET("/api/user/transaction/history/")
    Call<TransactionHistoryResponse> getHistoryItems(@Header("Authorization") String auth);

    /**
     * Function/Module Name : getSubscriptionItems
     * Purpose : is a GET Api endpoint that get list of subscriptions
     * Input: Authorization token
     * Output :  Subscription List Response
     **/
    @GET("/api/user/activity/subscribe/get/all/")
    Call<SubscriptionListResponse> getSubscriptionItems(@Header("Authorization") String auth);

    /**
     * Function/Module Name : subscripeUser
     * Purpose : is a GET Api endpoint that subscribe user to a subscription
     * Input: Authorization token, id
     * Output :  Generic Response NO KYC
     **/
    @GET("/api/user/activity/subscription/{id}")
    Call<GenericResponseNOKYC> subscripeUser(@Header("Authorization") String auth, @Path("id") String id);

    /**
     * Function/Module Name : getPointsHistory
     * Purpose : is a GET Api endpoint that get  the points history
     * Input: Authorization token
     * Output :  Points History Response
     **/
    @GET("/api/user/activity/point/history/")
    Call<PointsHistoryResponse> getPointsHistory(@Header("Authorization") String auth);

    /**
     * Function/Module Name : getSubscriptionHistory
     * Purpose : is a GET Api endpoint that get  the subscriptions history
     * Input: Authorization token
     * Output :  Subscription History Response
     **/
    @GET("/api/user/activity/subscription/history/")
    Call<SubscriptionHistoryResponse> getSubscriptionHistory(@Header("Authorization") String auth);

    /**
     * Function/Module Name : getPointsHistory
     * Purpose : is a GET Api endpoint that get  the profile tokens
     * Input: Authorization token
     * Output :  Portfolio Response
     **/
    @GET("/api/user/transaction/tokens/")
    Call<PortfolioResponse> getPortfolioTokens(@Header("Authorization") String auth);

    /**
     * Function/Module Name : getCompanyList
     * Purpose : is a GET Api endpoint that get the profile company list
     * Input: Authorization token
     * Output :  PreIcoCompanyListResponse
     **/
    @GET("/api/user/activity/company/list/")
    Call<PreIcoCompanyListResponse> getCompanyList(@Header("Authorization") String auth);

    /**
     * Function/Module Name : getPrivateKey
     * Purpose : is a GET Api endpoint that get the profile company list
     * Input: Authorization token
     * Output :  Generic Response
     **/
    @GET("api/settings/user/key/")
    Call<GenericResponse> getPrivateKey(@Header("Authorization") String auth);

    /**
     * Function/Module Name : getSeed
     * Purpose : is a GET Api endpoint that get seed info
     * Input: Authorization token
     * Output :  Generic Response
     **/
    @GET("api/settings/user/seed/")
    Call<GenericResponse> getSeed(@Header("Authorization") String auth);
}