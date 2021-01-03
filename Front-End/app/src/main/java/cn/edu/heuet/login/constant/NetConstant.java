package cn.edu.heuet.login.constant;

/**
 * 前端与后端的连接由该文件实现
 * */

public class NetConstant {
    public static final String baseService = "http://364051j4t2.qicp.vip:59175";    //花生壳内网映射
    //public static final String baseService = "http://localhost:8090";

    private static final String getOtpCodeURL     = "/user/getOtp";
    private static final String loginURL          = "/user/login";
    private static final String registerURL       = "/user/register";
    private static final String getUserInfoURL    = "/user/getUserInfo?tel=";
    private static final String createItemURL     = "/item/create";
    private static final String getItemListURL    = "/item/list";
    private static final String submitOrderURL    = "/order/createorder";

    private static final String getNewsListURL = "/news/list";

    private static final String getNewsByIdURL = "/news/detail/id?id=";

    private static final String getNewsByTitleURL = "/news/detail/title?title=";

    public static String getGetOtpCodeURL() {
        return getOtpCodeURL;
    }

    public static String getLoginURL() {
        return loginURL;
    }

    public static String getRegisterURL() {
        return registerURL;
    }

    public static String getCreateItemURL() {
        return createItemURL;
    }

    public static String getGetItemListURL() {
        return getItemListURL;
    }

    public static String getSubmitOrderURL() {
        return submitOrderURL;
    }

    public static String getNewsListURL() {
        return getNewsListURL;
    }

    public static String getNewsByIdURL() {
        return getNewsByIdURL;
    }

    public static String getNewsByTitleURL() {
        return getNewsByTitleURL;
    }

    public static String getUserInfoURL() {
        return getUserInfoURL;
    }
}
