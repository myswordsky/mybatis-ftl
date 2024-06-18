package cn.xzb.mybatis.gen.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class SpringBeanUtils {

    public static <T> T copyProperties(Object source, T target) throws BeansException {
        BeanUtils.copyProperties(source, target);
        return target;
    }


    public static <T, E> List<E> copyList(List<T> list, Class<E> dtoClass) {
        return copyList(list, dtoClass, null);
    }

    @SuppressWarnings("unchecked")
    public static <T, E> List<E> copyList(List<T> list, Class<E> dtoClass, Consumer<E> consumer) {
        List<E> dtos = new ArrayList<>();
        for (T entity : list) {
            try {
                E e = (E) Class.forName(dtoClass.getName()).newInstance();
                BeanUtils.copyProperties(entity, e);
                if (consumer != null) {
                    consumer.accept(e);
                }
                dtos.add(e);
            } catch (Exception e) {
                log.error("没有无参构造器", e);
            }
        }
        return dtos;
    }
}
