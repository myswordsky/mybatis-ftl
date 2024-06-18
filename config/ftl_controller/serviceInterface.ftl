package ${baseInfo.packageName};

import ${tableClass.fullClassName};
<#if baseService??&&baseService!="">
import ${baseService};
    <#list baseService?split(".") as simpleName>
        <#if !simpleName_has_next>
            <#assign serviceSimpleName>${simpleName}</#assign>
        </#if>
    </#list>
</#if>
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 【${tableClass.tableName}】的数据库操作Service
 *
 * @author ${author!}
 * @since ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
public interface ${baseInfo.fileName} extends IService<${tableClass.shortClassName}> {
}
