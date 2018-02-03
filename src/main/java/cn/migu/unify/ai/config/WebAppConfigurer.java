package cn.migu.unify.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {

    //file:/Users/gus97/pic/u1/
    @Value("${remoteSrc}")
    String remoteSrc;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/pic/**","/txt/**").addResourceLocations(remoteSrc+"/pic/",remoteSrc+"/txt/");
        super.addResourceHandlers(registry);
    }
}
