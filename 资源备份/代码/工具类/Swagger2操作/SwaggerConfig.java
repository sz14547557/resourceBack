package com.sz.partner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 *todo sz※ 此处自定义 Swagger 接口文档的配置 整合knife4j
 */
@Configuration
@EnableSwagger2WebMvc
// 在dev、test模式时，knife4j才生效
@Profile({"dev", "test"})
public class SwaggerConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //todo sz※  这里一定要标注你controller控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.sz.partner.controller"))
                // 路径选择器，使用PathSelectors.any()表示不限制路径,线上环境不要把接口暴露出去
                .paths(PathSelectors.any())
                .build();
    }


    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("某政伙伴匹配")
                .description("某政伙伴匹配接口文档")
                .termsOfServiceUrl("https://github.com")
                .contact(new Contact("sz","https://github.com","xxx@qq.com"))
                .version("1.0")
                .build();
    }
}
