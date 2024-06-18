package cn.xzb.mybatis.gen.domain;


import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Data
public class ConfigProperties {
    private String jdbc;
    private String username;
    private String password;

    private String dir;
    private String author;

    private String sqlEntity;
    private String sqlMapper;
    private String sqlService;

    public static ConfigProperties buildProperties(String url) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(url));
        return JSON.parseObject(JSON.toJSONString(properties), ConfigProperties.class);
    }
}
