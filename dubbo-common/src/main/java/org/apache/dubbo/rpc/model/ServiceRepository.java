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

import org.apache.dubbo.common.context.FrameworkExt;
import org.apache.dubbo.common.context.LifecycleAdapter;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.ReferenceConfigBase;
import org.apache.dubbo.config.ServiceConfigBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.dubbo.common.BaseServiceMetadata.interfaceFromServiceKey;
import static org.apache.dubbo.common.BaseServiceMetadata.versionFromServiceKey;

public class ServiceRepository extends LifecycleAdapter implements FrameworkExt {

    public static final String NAME = "repository";

    // services
    /**
     * 服务缓存，key是服务的映射路径，value是服务的整体描述
     */
    private ConcurrentMap<String, ServiceDescriptor> services = new ConcurrentHashMap<>();

    // consumers
    private ConcurrentMap<String, ConsumerModel> consumers = new ConcurrentHashMap<>();

    // providers
    private ConcurrentMap<String, ProviderModel> providers = new ConcurrentHashMap<>();

    // useful to find a provider model quickly with serviceInterfaceName:version
    private ConcurrentMap<String, ProviderModel> providersWithoutGroup = new ConcurrentHashMap<>();

    public ServiceRepository() {
        Set<BuiltinServiceDetector> builtinServices
                = ExtensionLoader.getExtensionLoader(BuiltinServiceDetector.class).getSupportedExtensionInstances();
        if (CollectionUtils.isNotEmpty(builtinServices)) {
            for (BuiltinServiceDetector service : builtinServices) {
                registerService(service.getService());
            }
        }
    }

    public ServiceDescriptor registerService(Class<?> interfaceClazz) {
        return services.computeIfAbsent(interfaceClazz.getName(),
                _k -> new ServiceDescriptor(interfaceClazz));
    }

    /**
     * See {@link #registerService(Class)}
     * <p>
     * we assume:
     * 1. services with different interfaces are not allowed to have the same path.
     * 2. services share the same interface but has different group/version can share the same path.
     * 3. path's default value is the name of the interface.
     * 1.不同的接口不允许有相同的映射路径
     * 2.同一个接口只能通过不同的组和版本才能使用相同的映射路径分享多次
     * 3.映射路径默认值就是接口名称
     *
     * 从这里来看，所谓注册服务其实是将服务使用ServiceDescriptor整理，然后放在services的缓存中
     *
     * @param path            服务的映射路径（规则是ContextPath+接口名称）
     * @param interfaceClass  接口
     * @return
     */
    public ServiceDescriptor registerService(String path, Class<?> interfaceClass) {
        ServiceDescriptor serviceDescriptor = registerService(interfaceClass);
        // if path is different with interface name, add extra path mapping
        //由于path是在前面组装的，主装的规则也是contextPath+接口名称，如果path不等于接口名称，说明
        //contextPath不为空，这样同一个接口就需要根据不同ContextPath暴露多个服务
        if (!interfaceClass.getName().equals(path)) {
            services.putIfAbsent(path, serviceDescriptor);
        }
        return serviceDescriptor;
    }

    public void unregisterService(Class<?> interfaceClazz) {
        unregisterService(interfaceClazz.getName());
    }

    public void unregisterService(String path) {
        services.remove(path);
    }

    public void registerConsumer(String serviceKey,
                                 ServiceDescriptor serviceDescriptor,
                                 ReferenceConfigBase<?> rc,
                                 Object proxy,
                                 ServiceMetadata serviceMetadata) {
        ConsumerModel consumerModel = new ConsumerModel(serviceMetadata.getServiceKey(), proxy, serviceDescriptor, rc,
                serviceMetadata);
        consumers.putIfAbsent(serviceKey, consumerModel);
    }

    public void reRegisterConsumer(String newServiceKey, String serviceKey) {
        ConsumerModel consumerModel = consumers.get(serviceKey);
        consumerModel.setServiceKey(newServiceKey);
        consumers.putIfAbsent(newServiceKey, consumerModel);
        consumers.remove(serviceKey);

    }

    /**
     * 这里刚好可以说明一下服务配置信息的流动过程
     * serviceConfig->serviceMetadata,serviceModel->ProviderModel
     * 这里会构建一个ProviderModel
     * @param serviceKey       服务唯一key
     * 格式见{@linkplain org.apache.dubbo.common.BaseServiceMetadata#buildServiceKey}
     *
     * @param serviceInstance  真正的服务实体
     * @param serviceModel     服务描述
     * @param serviceConfig    服务配置
     * @param serviceMetadata  服务元数据
     */
    public void registerProvider(String serviceKey,
                                 Object serviceInstance,
                                 ServiceDescriptor serviceModel,
                                 ServiceConfigBase<?> serviceConfig,
                                 ServiceMetadata serviceMetadata) {
        ProviderModel providerModel = new ProviderModel(serviceKey, serviceInstance, serviceModel, serviceConfig,
                serviceMetadata);
        providers.putIfAbsent(serviceKey, providerModel);
        providersWithoutGroup.putIfAbsent(keyWithoutGroup(serviceKey), providerModel);
    }

    /**
     * @param serviceKey
     * @return
     */
    private static String keyWithoutGroup(String serviceKey) {
        //接口名称+":"+版本号
        return interfaceFromServiceKey(serviceKey) + ":" + versionFromServiceKey(serviceKey);
    }

    public void reRegisterProvider(String newServiceKey, String serviceKey) {
        ProviderModel providerModel = providers.get(serviceKey);
        providerModel.setServiceKey(newServiceKey);
        providers.putIfAbsent(newServiceKey, providerModel);
        providers.remove(serviceKey);
    }

    public List<ServiceDescriptor> getAllServices() {
        return Collections.unmodifiableList(new ArrayList<>(services.values()));
    }

    public ServiceDescriptor lookupService(String interfaceName) {
        return services.get(interfaceName);
    }

    public MethodDescriptor lookupMethod(String interfaceName, String methodName) {
        ServiceDescriptor serviceDescriptor = lookupService(interfaceName);
        if (serviceDescriptor == null) {
            return null;
        }

        List<MethodDescriptor> methods = serviceDescriptor.getMethods(methodName);
        if (CollectionUtils.isEmpty(methods)) {
            return null;
        }
        return methods.iterator().next();
    }

    public List<ProviderModel> getExportedServices() {
        return Collections.unmodifiableList(new ArrayList<>(providers.values()));
    }

    public ProviderModel lookupExportedService(String serviceKey) {
        return providers.get(serviceKey);
    }

    public ProviderModel lookupExportedServiceWithoutGroup(String key) {
        return providersWithoutGroup.get(key);
    }

    public List<ConsumerModel> getReferredServices() {
        return Collections.unmodifiableList(new ArrayList<>(consumers.values()));
    }

    public ConsumerModel lookupReferredService(String serviceKey) {
        return consumers.get(serviceKey);
    }

    @Override
    public void destroy() throws IllegalStateException {
        // currently works for unit test
        services.clear();
        consumers.clear();
        providers.clear();
        providersWithoutGroup.clear();
    }
}
