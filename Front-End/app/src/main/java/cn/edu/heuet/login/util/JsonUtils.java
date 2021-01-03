package cn.edu.heuet.login.util;

import com.google.gson.Gson;

import cn.edu.heuet.login.bean.User;

public class JsonUtils {

    public String JsonUtils(String jsonData){
        //创建一个google的Gson对象
        Gson gson = new Gson();
        //根据主Activity传来的数据将数据拆分成Json格式的字符数组对象
        //拆分之后是一个个单独的字符串对象格式正好符合JSON格式的标准"{\"name\":\"小张\",\"age\":25,\"sex\":\"男\"}"   ；  {\"name\":\"小李子\",\"age\":41,\"sex\":\"女\"}
        String[] users=jsonData.split(";");
        for(int i=0;i<users.length;i++){
            //创建一个User类对象，第一个参数是json格式的数据，第二个参数是实现数据解析的类对象
            User user=gson.fromJson(users[i], User.class);
        }
        return jsonData;
    }

}
