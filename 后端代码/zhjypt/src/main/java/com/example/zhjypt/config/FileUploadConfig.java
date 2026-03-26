package com.example.zhjypt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 文件上传配置
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        createUploadDirectories();
        
        // 配置静态资源访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
    
    /**
     * 创建上传目录
     */
    private void createUploadDirectories() {
        String[] directories = {
            "uploads/videos/",
            "uploads/documents/", 
            "uploads/images/",
            "uploads/audio/"
        };
        
        for (String dir : directories) {
            File directory = new File(dir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("创建上传目录: " + dir);
                } else {
                    System.err.println("创建上传目录失败: " + dir);
                }
            }
        }
    }
}