package ${baseInfo.packageName};

import ${entityDTO.packageName}.${EntityTable}DTO;
import ${entityReq.packageName}.${EntityTable}Req;
import ${controService.packageName}.${EntityTable}Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${tableClass.shortClassName} 控制器接口
 *
 * @author ${author!}
 * @since ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${entityTable}")
public class ${EntityTable}Controller {
    private final ${EntityTable}Service ${entityTable}Service;

    @GetMapping("list")
    public PageRsp<${EntityTable}${"DTO"}> list${EntityTable}(@RequestParam ${EntityTable}Req req) {
        return ${entityTable}Service.list${EntityTable}(req);
    }

    @PostMapping("save")
    public ObjectRsp<${"Object"}> save${EntityTable}(@RequestBody ${EntityTable}DTO ${entityTable}DTO) {
        ${entityTable}Service.save${EntityTable}(${entityTable}DTO);
        return ObjectRsp.ok();
    }

    @PostMapping("update")
    public ObjectRsp<${"Object"}> update${EntityTable}(@RequestBody ${EntityTable}DTO ${entityTable}DTO) {
        ${entityTable}Service.update${EntityTable}(${entityTable}DTO);
        return ObjectRsp.ok();
    }

    @PostMapping("remove")
    public ObjectRsp<${"Object"}> remove${EntityTable}(@RequestBody List<${"Integer"}> ids) {
        ${entityTable}Service.remove${EntityTable}(ids);
        return ObjectRsp.ok();
    }
}