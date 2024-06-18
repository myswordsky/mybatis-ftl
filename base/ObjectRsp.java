package cn.xzb.mybatis.gen.domain;

import lombok.Data;

/**
 * 控制层统一封装对象
 */
@Data
public class ObjectRsp<T> {
    private static final long serialVersionUID = -2312312431388541L;
    private int code;
    private String msg;

    /**
     * 返回的数据对象
     */
    private T data;

    public ObjectRsp() {
        this.msg = "success";
    }

    public ObjectRsp(T t) {
        this.data = t;
        this.msg = "success";
    }

    public static ObjectRsp<Object> ok() {
        return new ObjectRsp<>(null);
    }
}
