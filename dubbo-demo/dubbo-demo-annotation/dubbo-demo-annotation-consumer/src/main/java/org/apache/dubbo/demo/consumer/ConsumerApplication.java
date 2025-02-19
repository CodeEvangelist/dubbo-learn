/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.demo.consumer.comp.DemoServiceComponent;

import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.AnnotationAttributes;

import java.io.IOException;
import java.util.Map;

public class ConsumerApplication {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    /**
     * dubbo的consumer服务引用过程
     * ->spring容器启动
     * ->扫描到{@link EnableDubbo}注解，spring在适当时机开始加载{@link org.apache.dubbo.config.annotation.DubboReference}注解处理器
     * 对包含DubboReference 注解的属性进行注入,然后到{@linkplain com.alibaba.spring.beans.factory.annotation.AbstractAnnotationBeanPostProcessor#getInjectedObject}
     * ->然后调用到{@linkplain org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor#doGetInjectedBean}
     * ->最后开始获取对应的Reference{@linkplain ReferenceConfig#get()}
     * ->开始检查配置，初始化引导器{@linkplain ReferenceConfig#init()}
     * ->然后创建Proxy{@linkplain ReferenceConfig#createProxy}
     * ->spring将获取到的代理对象注入完成
     */
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        //下面直接从容器中获取发现获取不到接口的类，说明产生的代理类并没有放在spring容器中
        //DemoService bean = (DemoService)context.getBean("demoService");
        //String hello = bean.sayHello("world");
        //这样却能用到dubbo的代理类，说明dubbo生成的代理类，实在注解处理的过程注入的
        DemoService service = context.getBean("demoServiceComponent", DemoServiceComponent.class);
        String hello = service.sayHello("world");
        System.out.println("result :" + hello);
        System.in.read();
    }

    @Configuration
    @EnableDubbo(scanBasePackages = "org.apache.dubbo.demo.consumer.comp")
    @PropertySource("classpath:/spring/dubbo-consumer.properties")
    @ComponentScan(value = {"org.apache.dubbo.demo.consumer.comp"})
    static class ConsumerConfiguration {

    }
}
