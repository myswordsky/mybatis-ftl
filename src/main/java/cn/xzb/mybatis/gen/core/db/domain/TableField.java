
package cn.xzb.mybatis.gen.core.db.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.NotNull;
import cn.xzb.mybatis.gen.core.contant.DbColumnType;
import cn.xzb.mybatis.gen.core.db.DatabaseMetaDataWrapper;

/**
 * 表字段信息
 */
@Getter@Setter
public class TableField {
    private boolean keyFlag;
    /**
     * 主键是否为自增类型
     */
    private boolean keyIdentityFlag;

    /**
     * 字段名称
     */
    private String name;
    private String comment;
    /**
     * 数据库字段（关键字含转义符号）
     */
    private String columnName;
    /**
     * 字段Java类型
     */
    private String propertyName;
    private DbColumnType columnType;

    private String type;
    /**
     * 是否关键字
     */
    private boolean keyWords;

    /**
     * 字段元数据信息
     */
    private MetaInfo metaInfo;

    /**
     * 构造方法
     * @param name          数据库字段名称
     */
    public TableField(@NotNull String name, String remark) {
        this.name = SQLUtils.lowerLineToHump(name, false);
        this.columnName = name;
        this.comment = StringUtils.isNotBlank(remark) ? remark.replace("\"", "\\\"") : remark;
    }


    /**
     * 设置属性名称
     *
     * @param propertyName 属性名
     * @param columnType   字段类型
     */
    public void setPropertyName(@NotNull String propertyName, @NotNull DbColumnType columnType) {
        this.columnType = columnType;
        this.propertyName = propertyName;
    }

    public String getPropertyType() {
        if (null != columnType) {
            return columnType.getType();
        }
        return null;
    }

    /**
     * 按 JavaBean 规则来生成 get 和 set 方法后面的属性名称
     * 需要处理一下特殊情况：
     * <p>
     * 1、如果只有一位，转换为大写形式
     * 2、如果多于 1 位，只有在第二位是小写的情况下，才会把第一位转为小写
     * <p>
     * 我们并不建议在数据库对应的对象中使用基本类型，因此这里不会考虑基本类型的情况
     */
    public String getCapitalName() {
        if (propertyName.length() == 1) {
            return propertyName.toUpperCase();
        }
        if (Character.isLowerCase(propertyName.charAt(1))) {
            return Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        }
        return propertyName;
    }

    /**
     * 获取注解字段名称
     *
     * @return 字段
     * @since 3.3.2
     */
    public String getAnnotationColumnName() {
        if (keyWords) {
            if (columnName.startsWith("\"")) {
                return String.format("\\\"%s\\\"", name);
            }
        }
        return columnName;
    }

    /**
     * 设置主键
     *
     * @param autoIncrement 自增标识
     */
    public void primaryKey(boolean autoIncrement) {
        this.keyFlag = true;
        this.keyIdentityFlag = autoIncrement;
    }


    /**
     * 元数据信息
     */
    @Getter
    @ToString
    public static class MetaInfo {

        private int length;

        private boolean nullable;

        private String remarks;

        private String defaultValue;

        private int scale;

        private JdbcType jdbcType;

        public MetaInfo(DatabaseMetaDataWrapper.Column column) {
            if (column != null) {
                this.length = column.getLength();
                this.nullable = column.isNullable();
                this.remarks = column.getRemarks();
                this.defaultValue = column.getDefaultValue();
                this.scale = column.getScale();
                this.jdbcType = column.getJdbcType();
            }
        }
    }
}
