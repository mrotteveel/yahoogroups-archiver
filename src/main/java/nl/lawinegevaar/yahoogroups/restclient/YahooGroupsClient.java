package nl.lawinegevaar.yahoogroups.restclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface YahooGroupsClient {

    @GET("api/v1/groups/{group}/messages?count=1&sortOrder=desc&direction=-1")
    Call<ResponseBody> getLatestMessage(@Path("group") String group);

    @GET("api/v1/groups/{group}/messages/{messageId}")
    Call<ResponseBody> getMessage(@Path("group") String group, @Path("messageId") int messageId);

    @GET("api/v1/groups/{group}/messages/{messageId}/raw")
    Call<ResponseBody> getRawMessage(@Path("group") String group, @Path("messageId") int messageId);
}
