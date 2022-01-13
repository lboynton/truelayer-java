package truelayer.java.http.interceptors;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@RequiredArgsConstructor
public class UserAgentInterceptor implements Interceptor {
    private final String name;
    private final String version;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        var newRequest = request.newBuilder()
                .header("User-Agent", String.format("%s/%s",
                        name,
                        version))
                .build();
        return chain.proceed(newRequest);
    }
}