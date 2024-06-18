package ${baseInfo.packageName};


import ${entityDTO.packageName}.${EntityTable}DTO;
import ${entityReq.packageName}.${EntityTable}Req;

import java.util.List;

public interface ${EntityTable}Service {
    PageRsp<${EntityTable}${"DTO"}> list${EntityTable}(${EntityTable}Req req);

    void save${EntityTable}(${EntityTable}DTO ${entityTable}DTO);

    void update${EntityTable}(${EntityTable}DTO ${entityTable}DTO);

    void remove${EntityTable}(List<${"Integer"}> ids);
}
