package cn.xzb.mybatis.gen.core.contant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础类型
 */
@AllArgsConstructor
@Getter
public enum BaseGenFtlEnum {
    ENTITY("entity");

    private final String fileName;

    public static List<String> listBaseGenEnumName() {
        return Arrays.stream(BaseGenFtlEnum.values()).map(BaseGenFtlEnum::getFileName).collect(Collectors.toList());
    }
}
