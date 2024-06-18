/*
* Copyright (c) Huawei Technologies Co., Ltd. ${.now?string('yyyy')}-${.now?string('yyyy')}. All rights reserved.
*/

package ${baseInfo.packageName};

<#assign isFirst = true>
<#list tableClass.allFields as field>
    <#if (field.fieldName == "createBy" || field.fieldName == "createTime" || field.fieldName == "updateBy" || field.fieldName == "updateTime") && isFirst>
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
        <#assign isFirst = false>
    </#if>
</#list>
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

<#assign isFirst = true>
<#list tableClass.allFields as field>
    <#if field.shortTypeName == "Date" && isFirst>
import java.time.LocalDateTime;
        <#assign isFirst = false>
    </#if>
</#list>

/**
 * ${tableClass.tableName}【<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】
 *
 * @author ${author!}
 * @since ${.now?string('yyyy-MM-dd')}
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ${tableClass.shortClassName} extends Model<${tableClass.shortClassName}> {
<#list tableClass.allFields as field>
    <#if field_index == 0>
    <#else>

    </#if>
    /**
     * ${field.remark!}
     */
    <#if field.fieldName == "id">
    @TableId
    </#if>
    <#if field.fieldName == "createBy" || field.fieldName == "createTime">
    @TableField(fill = FieldFill.INSERT)
    </#if>
    <#if field.fieldName == "updateBy" || field.fieldName == "updateTime">
    @TableField(fill = FieldFill.INSERT_UPDATE)
    </#if>
    private <#if field.shortTypeName == "Date" >LocalDateTime</#if><#if field.shortTypeName != "Date" >${field.shortTypeName}</#if> ${field.fieldName};
</#list>
}