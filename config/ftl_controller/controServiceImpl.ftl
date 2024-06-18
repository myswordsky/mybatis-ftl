package ${baseInfo.packageName};

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ${serviceInterface.packageName}.${serviceInterface.fileName};
import ${tableClass.fullClassName};
import ${entityDTO.packageName}.${EntityTable}DTO;
import ${entityReq.packageName}.${EntityTable}Req;
import ${controService.packageName}.${EntityTable}Service;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ${EntityTable}ServiceImpl implements ${EntityTable}Service {
    private final ${EntityTable}Dao ${entityTable}Dao;

    @Override
    public PageRsp<${EntityTable}${"DTO"}> list${EntityTable}(${EntityTable}Req req) {
        ${tableClass.shortClassName} entity = new ${tableClass.shortClassName}();
        BeanUtils.copyProperties(req, entity);
        LambdaQueryWrapper<${tableClass.shortClassName}> wrapper = new LambdaQueryWrapper<>();
        wrapper.setEntity(entity);

        Page<${tableClass.shortClassName}> page = Page.of(req.getPageNumber(), req.getPageSize());
        Page<${tableClass.shortClassName}> list = ${entityTable}Dao.page(page, wrapper);
        List<${EntityTable}${"DTO"}> records = list.getRecords().stream().map(e -> {
            ${EntityTable}DTO dto = new ${EntityTable}DTO();
            BeanUtils.copyProperties(e, dto);
            return dto;
        }).collect(Collectors.toList());

        PageRsp<${EntityTable}${"DTO"}> result = new PageRsp<>();
        result.setData(records);
        result.setTotal(list.getTotal());
        return result;
    }

    @Override
    public void save${EntityTable}(${EntityTable}DTO ${entityTable}DTO) {
        ${tableClass.shortClassName} entity = new ${tableClass.shortClassName}();
        BeanUtils.copyProperties(${entityTable}DTO, entity);
        ${entityTable}Dao.saveOrUpdate(entity);
    }

    @Override
    public void update${EntityTable}(${EntityTable}DTO ${entityTable}DTO) {
        ${tableClass.shortClassName} entity = new ${tableClass.shortClassName}();
        BeanUtils.copyProperties(${entityTable}DTO, entity);
        ${entityTable}Dao.saveOrUpdate(entity);
    }

    @Override
    public void remove${EntityTable}(List<${"Integer"}> ids) {
        ${entityTable}Dao.removeBatchByIds(ids);
    }
}