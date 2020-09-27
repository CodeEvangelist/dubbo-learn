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
package org.apache.dubbo.demo.provider;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.apache.dubbo.config.spring.beans.factory.annotation.ServiceClassPostProcessor;

import org.apache.dubbo.config.spring.context.DubboBootstrapApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

public class ProviderApplication {
    /**启动过程
     * ->Spring容器启动
     * ->注册ServiceClassPostProcessor
     * ->调用{@linkplain ServiceClassPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}
     * ->加载{@link DubboBootstrapApplicationListener}
     * ->Spring容器启动完成
     * ->调用相应的Listener
     * ->调用{@linkplain DubboBootstrapApplicationListener#onApplicationContextEvent(ApplicationContextEvent)}
     * ->{@linkplain DubboBootstrap#start()}开始启动
     * ->...省略很多dubbo服务暴露，注册详细说明等等
     * ->{@link DubboBootstrap}启动完成
     */
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProviderConfiguration.class);
        context.start();
        System.in.read();
    }
    //首先spring会扫描到此Configuration
    //当然会加载此类之上的EnableDoubbo注解
    //@EnableDubbo中引入了很多的配置类，包括dubbo核心类，配置类等等
    @Configuration
    @EnableDubbo(scanBasePackages = "org.apache.dubbo.demo.provider")
    @PropertySource("classpath:/spring/dubbo-provider.properties")
    static class ProviderConfiguration {
        @Bean
        public RegistryConfig registryConfig() {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress("zookeeper://192.168.0.104:2181");
            //有时候zookeeper网络不是特别好，会导致zookeeper not connected，所以这里添加了zookeeper的timeout
            registryConfig.setTimeout(10000);
            return registryConfig;
        }
    }
}
