/*
* Copyright (c) Huawei Technologies Co., Ltd. ${.now?string('yyyy')}-${.now?string('yyyy')}. All rights reserved.
*/

package ${mapperInterface.packageName};

import ${tableClass.fullClassName};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 【${tableClass.tableName}】的数据库操作Mapper
 *
 * @author ${author!}
 * @since ${.now?string('yyyy-MM-dd')}
 */
public interface ${mapperInterface.fileName} extends BaseMapper<${tableClass.shortClassName}> {
}