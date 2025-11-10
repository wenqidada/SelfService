package com.zwq.selfservice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY) // 使用@SerializedName注解
            .serializeNulls() // 序列化null值
            .create();
    }

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson);
        return converter;
    }
}
