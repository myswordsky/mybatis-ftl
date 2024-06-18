package cn.xzb.mybatis.gen.core.db.domain;


public class SQLUtils {

    public static void main(String[] args) {
        String anchor_record = lowerLineToHump("anchor_record");
        System.err.println(anchor_record);
        System.err.println(upperToLine(anchor_record));
    }

    /**
     * 转驼峰大写 anchor_record -> AnchorRecord
     */
    public static String lowerLineToHump(String lowerLine) {
        return lowerLineToHump(lowerLine, true);
    }
    public static String lowerLineToHump(String lowerLine, boolean iHeadUp) {
        String[] eachStr = lowerLine.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < eachStr.length; i++) {
            if(i == 0){
                if(iHeadUp){
                    sb.append(getStartStringUp(eachStr[i]));
                }else{
                    sb.append(eachStr[i]);
                }
                continue;
            }
            sb.append(getStartStringUp(eachStr[i]));
        }
        return sb.toString();
    }
    public static String getStartStringUp(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public static String getStartStringLow(String str){
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String upperToLine(String lowerLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lowerLine.length(); i++) {
            if(i == 0){
                sb.append(toLowerCase(lowerLine.charAt(i)));
                continue;
            }
            if(Character.isUpperCase(lowerLine.charAt(i))){
                sb.append("_").append(toLowerCase(lowerLine.charAt(i)));
            }else{
                sb.append(lowerLine.charAt(i));
            }
        }
        return sb.toString();
    }

    public static char toLowerCase(char c1){
        int b = (int) c1 +32;
        return (char)b;

    }
}
