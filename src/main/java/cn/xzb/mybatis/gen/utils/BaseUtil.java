package cn.xzb.mybatis.gen.utils;


public class BaseUtil {

    public static boolean isJar() {
        String runType = String.valueOf(BaseUtil.class.getResource(""));
        return runType != null && runType.startsWith("jar:");

    }

}
