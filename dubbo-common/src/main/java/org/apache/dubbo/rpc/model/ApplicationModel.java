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
package org.apache.dubbo.rpc.model;

import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.context.FrameworkExt;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.context.ConfigManager;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link ExtensionLoader}, {@code DubboBootstrap} and this class are at present designed to be
 * singleton or static (by itself totally static or uses some static fields). So the instances
 * returned from them are of process scope. If you want to support multiple dubbo servers in one
 * single process, you may need to refactor those three classes.
 *
 * {@link ExtensionLoader}, {@code DubboBootstrap}和此类都被设计成单例或静态的，所以在一个dubbo服务中
 * 仅有一份，如果需要dubbo服务支持多个（互相隔离开的dubbo服务）,那么需要重写这三个类，因为这三个类包含了
 * dubbo服务RPC过程中元数据信息（元数据指的是dubbo配置中心，服务提供者，服务消费者，服务运行环境，java编译器等等~~）
 *
 *
 * Represent a application which is using Dubbo and store basic metadata info for using
 * during the processing of RPC invoking.
 * <p>
 * ApplicationModel includes many ProviderModel which is about published services
 * and many Consumer Model which is about subscribed services.
 * <p>
 *
 */

/**
 * 在此对dubbo的数据模型使用图形做个说明，如下所示
 *
 *                           ApplicationModel
 *                         /       |          \
 *                        /        |           \
 *                       /         |            \
 *           ConfigManager      Environment       ServiceRepository
 *          |    |       \               \            |      |     \
 *   协议、注册  monitor   router，ssl   服务运行的环境  desc  Provider  Consumer
 *
 */
public class ApplicationModel {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ApplicationModel.class);
    public static final String NAME = "application";

    private static AtomicBoolean INIT_FLAG = new AtomicBoolean(false);

    public static void init() {
        if (INIT_FLAG.compareAndSet(false, true)) {
            ExtensionLoader<ApplicationInitListener> extensionLoader = ExtensionLoader.getExtensionLoader(ApplicationInitListener.class);
            Set<String> listenerNames = extensionLoader.getSupportedExtensions();
            for (String listenerName : listenerNames) {
                extensionLoader.getExtension(listenerName).init();
            }
        }
    }

    /**
     * dubbo消费者
     * @return
     */
    public static Collection<ConsumerModel> allConsumerModels() {
        return getServiceRepository().getReferredServices();
    }

    /**
     * dubbo服务提供者
     * @return
     */
    public static Collection<ProviderModel> allProviderModels() {
        return getServiceRepository().getExportedServices();
    }

    public static ProviderModel getProviderModel(String serviceKey) {
        return getServiceRepository().lookupExportedService(serviceKey);
    }

    public static ConsumerModel getConsumerModel(String serviceKey) {
        return getServiceRepository().lookupReferredService(serviceKey);
    }

    private static final ExtensionLoader<FrameworkExt> LOADER = ExtensionLoader.getExtensionLoader(FrameworkExt.class);

    public static void initFrameworkExts() {
        Set<FrameworkExt> exts = ExtensionLoader.getExtensionLoader(FrameworkExt.class).getSupportedExtensionInstances();
        for (FrameworkExt ext : exts) {
            ext.initialize();
        }
    }
    /**
     * 服务运行的环境
     */
    public static Environment getEnvironment() {
        return (Environment) LOADER.getExtension(Environment.NAME);
    }
    /**
     * 配置管理，包括一些路由规则等等
     */
    public static ConfigManager getConfigManager() {
        return (ConfigManager) LOADER.getExtension(ConfigManager.NAME);
    }

    /**
     * dubbo服务仓库，包含了provider和consumer
     * @return
     */
    public static ServiceRepository getServiceRepository() {
        return (ServiceRepository) LOADER.getExtension(ServiceRepository.NAME);
    }

    public static ApplicationConfig getApplicationConfig() {
        return getConfigManager().getApplicationOrElseThrow();
    }

    public static String getName() {
        return getApplicationConfig().getName();
    }

    @Deprecated
    private static String application;

    @Deprecated
    public static String getApplication() {
        return application == null ? getName() : application;
    }

    // Currently used by UT.
    @Deprecated
    public static void setApplication(String application) {
        ApplicationModel.application = application;
    }

    // only for unit test
    public static void reset() {
        getServiceRepository().destroy();
        getConfigManager().destroy();
        getEnvironment().destroy();
    }

}
