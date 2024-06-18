package cn.xzb.mybatis;

import cn.hutool.extra.spring.SpringUtil;
import cn.xzb.mybatis.gen.handle.GenControllerFltSwing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SpringUtil.class)
public class MybatisGenApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(MybatisGenApplication.class);
        builder.headless(false).run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        GenControllerFltSwing.main(null);
    }
}
