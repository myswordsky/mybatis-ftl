package cn.xzb.mybatis.gen.core.db.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 表信息，关联到当前字段信息
 */
@Getter
@Setter
public class TableInfo {

    /**
     * 表名称
     */
    private String name;
    /**
     * 表注释
     */
    private String comment;
    /**
     * 实体名称(转驼峰后的名称头字母大写)
     */
    private String entityName;


    /**
     * 表字段
     */
    private final List<TableField> fields = new ArrayList<>();

    /**
     * 是否有主键
     */
    private boolean havePrimaryKey;

    /**
     * 字段名称集
     */
    private String fieldNames;

    /**
     * 主键字段
     */
    private final List<TableField> pkFields = new ArrayList<>();


    /**
     * 构造方法
     * @param name          表名
     */
    public TableInfo(@NotNull String name) {
        this.name = name;
        setEntityName(SQLUtils.lowerLineToHump(name));
    }

    /**
     * @param entityName 实体名称
     */
    public void setEntityName(@NotNull String entityName) {
        this.entityName = entityName;
    }

    /**
     * 实体名称(转驼峰后的名称头字母小写)
     */
    public String getEntityLowName() {
        return SQLUtils.getStartStringLow(this.entityName);
    }

    /**
     * 添加字段
     * @param field 字段
     */
    public void addField(@NotNull TableField field) {
        this.fields.add(field);
    }

    /**
     * 转换filed实体为 xml mapper 中的 base column 字符串信息
     */
    public String getFieldNames() {
        if (StringUtils.isBlank(fieldNames)) {
            this.fieldNames = this.fields.stream().map(TableField::getColumnName).collect(Collectors.joining(", "));
        }
        return this.fieldNames;
    }


    public TableInfo setComment(String comment) {
        this.comment = StringUtils.isNotBlank(comment) ? comment.replace("\"", "\\\"") : comment;
        return this;
    }

    public TableInfo setHavePrimaryKey(boolean havePrimaryKey) {
        this.havePrimaryKey = havePrimaryKey;
        return this;
    }

}
