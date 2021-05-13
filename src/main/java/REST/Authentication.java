package REST;

public class Authentication {
    private static String token = null;
    private static boolean login=false;

    public static void setLogin(boolean login){
        Authentication.login=login;
    }

    public static boolean isLoggedIn(){
        return login;
    }

    public static void setToken(String token) {
        Authentication.token = token;
    }

    public  static String getToken(){
        return token;
    }

    public static void clearToken(){
        token =null;
    }
}
