package ca.cmpt276.walkinggroup.dataobjects;

import ca.cmpt276.walkinggroup.app.LoginActivity;
import ca.cmpt276.walkinggroup.app.R;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


public class Session {
    private WGServerProxy proxy;
    private static Session instance;
    private User user;
    private String token;
    String API_KEY = "394ECE0B-5BF9-41C4-B9F6-261B07678ED23";

    private Session(){
        proxy = ProxyBuilder.getProxy(API_KEY);
        user = new User();
        token = "";

    }
    public static Session getInstance(){
        if(instance == null){
            instance = new Session();
        }
        return instance;
    }

    public void setProxy(String token){
        proxy = ProxyBuilder.getProxy(API_KEY,token);
    }

    public WGServerProxy getProxy() {
        return this.proxy;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
