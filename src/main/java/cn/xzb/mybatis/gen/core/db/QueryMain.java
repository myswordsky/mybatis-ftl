package cn.xzb.mybatis.gen.core.db;


import cn.xzb.mybatis.gen.core.FreemarkerTemplateEngine;
import cn.xzb.mybatis.gen.core.contant.BaseGenFtlEnum;
import cn.xzb.mybatis.gen.core.contant.DbColumnType;
import cn.xzb.mybatis.gen.core.db.domain.GlobalConfig;
import cn.xzb.mybatis.gen.core.db.domain.SQLUtils;
import cn.xzb.mybatis.gen.core.db.domain.TableField;
import cn.xzb.mybatis.gen.core.db.domain.TableInfo;
import cn.xzb.mybatis.gen.domain.ConfigProperties;
import cn.xzb.mybatis.gen.handle.GenControllerFltSwing;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MySql 精简版，需要mysql-java-connector
 */
public class QueryMain {

    public List<String> listDBNames(DataSourceConfig config) {
        List<String> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword())) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> listTableNames(DataSourceConfig config) {
        DatabaseMetaDataWrapper databaseMetaDataWrapper = new DatabaseMetaDataWrapper(config);
        //获取所有表备注信息
        List<DatabaseMetaDataWrapper.Table> tables = databaseMetaDataWrapper.getTables(null, new String[]{"TABLE"});
        return tables.stream().map(DatabaseMetaDataWrapper.Table::getName).collect(Collectors.toList());
    }

    public static void gen(DataSourceConfig config, GlobalConfig globalConfig, String[] tablesName) throws Exception {
        if (globalConfig.getFtlInfoList().isEmpty()) {
            return;
        }

        DatabaseMetaDataWrapper databaseMetaDataWrapper = new DatabaseMetaDataWrapper(config);
        // 获取所有表备注信息
        List<DatabaseMetaDataWrapper.Table> tables = databaseMetaDataWrapper.getTables(null, new String[]{"TABLE"});
        Map<String, DatabaseMetaDataWrapper.Table> map = tables.stream().collect(Collectors.toMap(DatabaseMetaDataWrapper.Table::getName, e -> e, (e1, e2) -> e1));
        // System.err.println(tables);
        // 组装table信息
        for (String tableName : tablesName) {
            TableInfo tableInfo = new TableInfo(tableName);
            tableInfo.setComment(map.get(tableName).getRemarks());
            Map<String, DatabaseMetaDataWrapper.Column> columnsInfoMap = databaseMetaDataWrapper.getColumnsInfo(tableName, true);
            columnsInfoMap.forEach((k, columnInfo) -> {
                TableField.MetaInfo metaInfo = new TableField.MetaInfo(columnInfo);
                String columnName = columnInfo.getName();
                TableField field = new TableField(columnName, columnInfo.getRemarks());
                JdbcType jdbcType = metaInfo.getJdbcType();
                DbColumnType byName = DbColumnType.getByName(jdbcType.name());
                field.setPropertyName(byName.getType(), byName);
                field.setMetaInfo(metaInfo);

                // 处理ID
                if (columnInfo.isPrimaryKey()) {
                    field.primaryKey(columnInfo.isAutoIncrement());
                    tableInfo.setHavePrimaryKey(true);
                    tableInfo.getPkFields().add(field);
                    if (field.isKeyIdentityFlag()) {
                        System.err.printf("当前表[%s]的主键为自增主键，会导致全局主键的ID类型设置失效!%n", tableName);
                    }
                }
                tableInfo.addField(field);
            });
            for (GlobalConfig.FtlInfo ftlInfo : globalConfig.getFtlInfoList()) {
                String genUrl = ftlInfo.getOutputSrc().replaceAll("\\\\", "/");
                String sourceSrc = ftlInfo.getSourceSrc().replaceAll("\\\\", "/");

                // 生成对象信息
                Map<String, Object> objectMap = getObjectMap(tableInfo, config, globalConfig, ftlInfo);

                String ftlDir = sourceSrc.substring(0, sourceSrc.lastIndexOf("/"));
                String ftlFileName = sourceSrc.substring(sourceSrc.lastIndexOf("/") + 1);
                FreemarkerTemplateEngine template = new FreemarkerTemplateEngine(ftlDir);
                template.outputFile(new File(genUrl), objectMap, ftlFileName, globalConfig.isFileOverride());

                gitAdd(ftlInfo.getOutputDir(), ftlInfo.getFileNameSuffix());
            }
        }
    }

    @NotNull
    private static Map<String, Object> getObjectMap(TableInfo tableInfo, DataSourceConfig config, GlobalConfig globalConfig, GlobalConfig.FtlInfo ftlInfo) {
        Map<String, Object> objectMap = new HashMap<>();
        // 额外包名列表
        objectMap.put("package", new ArrayList<>());
        objectMap.put("swagger", globalConfig.isSwagger());
        objectMap.put("springdoc", globalConfig.isSpringdoc());
        objectMap.put("author", globalConfig.getAuthor());

        // 启用 schema 处理逻辑
        String schemaName;
        schemaName = config.getSchemaName();
        if (StringUtils.isNotBlank(schemaName)) {
            schemaName += ".";
        }
        objectMap.put("schemaName", schemaName);
        objectMap.put("table", tableInfo);
        objectMap.put("entityTable", tableInfo.getEntityLowName());
        objectMap.put("EntityTable", tableInfo.getEntityName());

        // 兼容MybatisX
        mybatisX(objectMap, tableInfo, globalConfig, ftlInfo);
        return objectMap;
    }

    private static void mybatisX(Map<String, Object> objectMap, TableInfo tableInfo, GlobalConfig globalConfig, GlobalConfig.FtlInfo ftlInfo) {
        ConfigProperties properties = globalConfig.getProperties();

        String packageName = ftlInfo.getThisClassGenPackageName();
        String genFileName = ftlInfo.getGenThisFileName();
        // 基础类的额外字段
        // entityReq.packageName--mapperDao.packageName--mapperDao.fileName--mapperInterface.packageName--mapperInterface.fileName
        // baseInfo：自己的信息 不共享
        objectMap.put("baseInfo", new HashMap<String, Object>() {{
            put("packageName", packageName);
            put("fileName", genFileName);
        }});

        // cn.xzb.upload.demo.entity.ShopProduct
        String entityFullClassName = getFullClassName(globalConfig, tableInfo.getName(), properties.getSqlEntity());
        objectMap.put(BaseGenFtlEnum.ENTITY.getFileName(), new HashMap<String, Object>() {{
            put("packageName", getPackageNameByFull(entityFullClassName));
        }});

        // 动态添加其它ftl名称
        List<String> strings = BaseGenFtlEnum.listBaseGenEnumName();
        List<GlobalConfig.FtlInfo> ftlInfoList = globalConfig.getFtlInfoList();
        for (GlobalConfig.FtlInfo ftlInfoConfig : ftlInfoList) {
            String ftlName = ftlInfoConfig.getFtlName();
            if (strings.contains(ftlName)) {
                continue;
            }
            String fullClassName = getFullClassName(globalConfig, tableInfo.getName(), ftlName);
            String shortClassName = getShortClassName(globalConfig, tableInfo.getName(), ftlName);
            objectMap.put(ftlName, new HashMap<String, Object>() {{
                put("packageName", getPackageNameByFull(fullClassName));
                put("fileName", shortClassName);
            }});
        }

        // 基础字段
        Map<String, Object> tableClass = new HashMap<>();
        tableClass.put("importList", new ArrayList<>());    //导包
        tableClass.put("remark", tableInfo.getComment());   //表注释
        tableClass.put("tableName", tableInfo.getName());   //表名

        tableClass.put("fullClassName", entityFullClassName);
        String shortClassName = getShortClassName(globalConfig, tableInfo.getName(), properties.getSqlEntity());
        tableClass.put("shortClassName", shortClassName);//表大驼峰名
        tableClass.put("lowShortClassName", SQLUtils.getStartStringLow(shortClassName));//表大驼峰名
        tableClass.put("pojoPackage", "entity");

        List<FieldInfo> allFields = new ArrayList<>();//所有字段信息
        List<FieldInfo> pkFields = new ArrayList<>(); //主键字段信息
        List<String> pkCollect = tableInfo.getPkFields().stream().map(TableField::getColumnName).collect(Collectors.toList());
        for (TableField field : tableInfo.getFields()) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setRemark(field.getComment());
            fieldInfo.setShortTypeName(field.getPropertyName());
            fieldInfo.setFieldName(field.getName());
            fieldInfo.setColumnName(field.getColumnName());
            fieldInfo.setJdbcType(field.getMetaInfo().getJdbcType().name());
            if (pkCollect.contains(field.getColumnName())) {
                pkFields.add(fieldInfo);
                fieldInfo.setIsId(1);
            } else {
                fieldInfo.setIsId(0);
            }
            allFields.add(fieldInfo);
        }
        // xml文件的额外补充
        // baseBlobFields--pkFields--baseFields--allFields
        tableClass.put("allFields", allFields);
        tableClass.put("pkFields", pkFields);
        List<FieldInfo> baseFields = allFields.stream().filter(e -> !pkCollect.contains(e.getColumnName())).collect(Collectors.toList());
        tableClass.put("baseFields", baseFields);
        tableClass.put("baseBlobFields", baseFields);
        objectMap.put("tableClass", tableClass);
    }

    /**
     * ex:cn.xzb.upload.demo.entity.ShopProduct
     */
    public static String getFullClassName(GlobalConfig globalConfig, String tableName, String sqlClass) {
        if (StringUtils.isBlank(sqlClass)) {
            return "";
        }
        Properties ftlProperty = globalConfig.getFtlProperty();
        String dir = ftlProperty.get(sqlClass).toString();
        dir = dir.endsWith(File.separator) ? dir.substring(0, dir.length() - 1) : dir;
        return globalConfig.getFullPackageUrl(dir, getShortClassName(globalConfig, tableName, sqlClass));
    }

    public static String getShortClassName(GlobalConfig globalConfig, String tableName, String sqlClass) {
        if (StringUtils.isBlank(sqlClass)) {
            return "";
        }
        Properties ftlProperty = globalConfig.getFtlProperty();
        Object suffixObject = ftlProperty.get(sqlClass + GenControllerFltSwing.SUFFIX);
        String suffix = suffixObject == null ? "" : suffixObject.toString().trim();
        return SQLUtils.lowerLineToHump(tableName) + suffix;
    }

    public static String getPackageNameByFull(String fullClassName) {
        if (StringUtils.isBlank(fullClassName)) {
            return "";
        }
        return fullClassName.substring(0, fullClassName.lastIndexOf("."));
    }

    public static void gitAdd(String propertyValue, String fileNameSuffix) throws Exception {
        Thread.sleep(100);
        Runtime runtime = Runtime.getRuntime();
        String cmd = "cmd.exe /c cd /d \"" + propertyValue + "\" & git add " + fileNameSuffix;
        System.err.println(cmd);
        runtime.exec(cmd);
    }

    /**
     * MybatisX字段信息
     */
    @Data
    public static class FieldInfo implements Serializable {
        private String remark;          // 注释
        private String shortTypeName;   // Java类型
        private String fieldName;       // Java字段名
        private String columnName;      // 表字段名
        private String jdbcType;        // SQL字段类型
        private Integer isId;           // 是否主键
    }
}
