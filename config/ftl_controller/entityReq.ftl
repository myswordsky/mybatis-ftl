package ${baseInfo.packageName};

<#assign isFirst = true>
<#list tableClass.allFields as field>
    <#if field.shortTypeName == "Date" && isFirst>
import com.fasterxml.jackson.annotation.JsonFormat;
        <#assign isFirst = false>
    </#if>
</#list>
import lombok.Data;
import lombok.EqualsAndHashCode;
<#assign isFirst = true>
<#list tableClass.allFields as field>
    <#if field.shortTypeName == "Date" && isFirst>
import org.springframework.format.annotation.DateTimeFormat;
        <#assign isFirst = false>
    </#if>
</#list>

import java.io.Serializable;
<#assign isFirst = true>
<#list tableClass.allFields as field>
    <#if field.shortTypeName == "Date" && isFirst>
import java.util.Date;
        <#assign isFirst = false>
    </#if>
</#list>

/**
* ${tableClass.shortClassName} 请求类
*
* @author ${author!}
* @since ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class ${EntityTable}Req extends PageReq implements Serializable {
<#list tableClass.allFields as field>
    <#if field_index == 0>
    <#else>

    </#if>
    /**
    * ${field.remark!}
    */
    <#if field.shortTypeName == "Date" >
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    </#if>
    private ${field.shortTypeName} ${field.fieldName};
</#list>
}