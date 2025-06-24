package com.zwq.selfservice.util;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:h2:file:./data/testdb", "demo", "77889900")
                .globalConfig(builder -> {
                    builder.disableOpenDir() // 不允许自动打开输出目录
                            .author("zwq") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.zwq.selfservice") // 设置父包名
                            .controller("controller") // 设置 Controller 包名
                            .entity("entity") // 设置实体类包名
                            .mapper("dao") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mapper") // 设置 Mapper XML 文件包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置 Mapper XML 文件的输出路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("DETAILS_TABLE","BILLIARD_TABLE","VIP_INFO_TABLE","DEPOSIT_TABLE","MANAGER_INFO_TABLE") // 设置需要生成的表名
                            .controllerBuilder().enableRestStyle()
                            .entityBuilder().enableFileOverride().enableLombok().enableTableFieldAnnotation().logicDeleteColumnName("isDelete").addTableFills(new Property("createTime", FieldFill.INSERT)).addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .mapperBuilder().enableFileOverride().formatMapperFileName("%sDao").formatXmlFileName("%sMapper")
                            .serviceBuilder().enableFileOverride().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImp");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}