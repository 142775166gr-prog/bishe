package com.example.zhjypt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration //标准当前类是一个配置类
@EnableSwagger2//启动swagger的自动配置
public class Swagger2Config {
    /*  swagger会帮助我们生成接口文档
     * 1：配置生成的文档信息:比如文档名称、作者、版本..
     * 2: 配置生成规则
     * */

    /*Docket封装接口文档信息*/
    @Bean
    public Docket getDocket() {
        //创建封面信息对象
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
        apiInfoBuilder.title("《智慧教育平台》后端接口说明") //文档标题
                .description("文档说明：《智慧教育平台》后端接口文档") //文档说明
                .version("v 2.1.1") //版本
                .contact(new Contact("daswswsdgkj", "www.neuedu.com", "wudagai@qq.com"));//作者
        ApiInfo apiInfo = apiInfoBuilder.build();
        //DocumentationType.SWAGGER_2:指定文档风格
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo) //指定生成的文档中的封面信息：文档标题、版本、作者
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.zhjypt.controller")) //为指定包的处理器方法生成接口文档
                //.paths(PathSelectors.regex("/user/"))//指定com.wudagai.neumall.controller中/user/的路径
                .paths(PathSelectors.any()) //指定com.wudagai.neumall.controller中所有的路径
                .build();
        return docket;
    }
}
