package ${baseInfo.packageName};

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
<#assign isFirst = true>
<#list tableClass.allFields as field>
    <#if field.shortTypeName == "Date" && isFirst>
import java.util.Date;
<#assign isFirst = false>
    </#if>
</#list>
<#list tableClass.importList as fieldType>${"\n"}import ${fieldType};</#list>
/**
 * 【<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】
 *
 * @author ${author!}
 * @since ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Data
@Accessors(chain = true)
@TableName(value = "${tableClass.tableName}", autoResultMap = true)
public class ${tableClass.shortClassName} implements Serializable {
<#assign isFirst = true>
<#list tableClass.allFields as field>
    <#if isFirst>
    <#else>

    </#if>
    <#assign isFirst = false>
    /**
     * ${field.remark!}
     */
    <#if field.fieldName == "id">
    @TableId(type = IdType.AUTO)
    </#if>
    private ${field.shortTypeName} ${field.fieldName};
</#list>
}