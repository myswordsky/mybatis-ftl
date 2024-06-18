package cn.xzb.mybatis.gen.core.db.domain;


import cn.xzb.mybatis.gen.domain.ConfigProperties;
import lombok.*;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

@Getter
@Setter
@Builder
public class GlobalConfig {
    /**
     * entity outputDir 目录没有文件名
     */
    private String entityDir;
    private String author;
    /**
     * 是否覆盖已有文件
     */
    private boolean fileOverride;

    /**
     * 开启 swagger 模式（默认 false 与 springdoc 不可同时使用）
     */
    private boolean swagger;
    /**
     * 开启 springdoc 模式（默认 false 与 swagger 不可同时使用）
     */
    private boolean springdoc;

    private ConfigProperties properties;
    private Properties ftlProperty;

    private List<FtlInfo> ftlInfoList;

    @Data
    @AllArgsConstructor
    @Builder
    public static class FtlInfo {
        /**
         * 输出目录
         */
        private String outputDir;
        /**
         * fltUrl
         */
        private String sourceSrc;
        /**
         * ex:UserEntity UserDao UserDaoImpl
         */
        private String genThisFileName;
        /**
         * ex:entity mapperInterface
         */
        private String ftlName;
        private boolean xml;

        /**
         * 获取包名
         */
        public String getThisClassGenPackageName() {
            String outputDirPck = this.getOutputDir();
            if(isXml()){
                return "";
            }else{
                return outputDirPck.replaceAll(Matcher.quoteReplacement(File.separator), ".").split(srcName)[1];
            }
        }

        public String getOutputSrc(){
            String outputDir = getOutputDir() + File.separator + genThisFileName;
            return isXml() ? outputDir + ".xml" : outputDir + ".java";
        }

        public String getFileNameSuffix(){
            return isXml() ? genThisFileName + ".xml" : genThisFileName + ".java";
        }
    }

    private static final String srcName = "src.main.java.";
    private static final String resourceName = "src.main.resources.";

    /**
     * ex:cn.xzb.upload.demo.entity.ShopProduct
     */
    public String getFullPackageUrl(String dir, String shortName) {
        String[] split = dir.replaceAll(Matcher.quoteReplacement(File.separator), ".").split(srcName);
        //xml处理
        if(split.length == 1){
            return "";
        }
        String s = split[1];
        if (s.endsWith(".")) {
            return s + shortName;
        }
        return s + "." + shortName;
    }
}
