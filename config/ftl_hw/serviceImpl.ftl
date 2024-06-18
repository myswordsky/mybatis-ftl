package ${baseInfo.packageName};

import ${serviceInterface.packageName}.${serviceInterface.fileName};
import ${tableClass.fullClassName};
import ${mapperInterface.packageName}.${mapperInterface.fileName};

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 【${tableClass.tableName}】的数据库操作Service实现
 *
 * @author ${author!}
 * @since ${.now?string('yyyy-MM-dd')}
 */
@Service
public class ${baseInfo.fileName} extends ServiceImpl<${mapperInterface.fileName}, ${tableClass.shortClassName}>
              implements ${serviceInterface.fileName} {
}