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
package org.apache.dubbo.registry;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;

/**
 * RegistryFactory. (SPI, Singleton, ThreadSafe)
 * SPI自适应扩展，单例，线程安全
 * @see org.apache.dubbo.registry.support.AbstractRegistryFactory
 */

/**
 * 这里说明一下当注册中心为zookeeper时的自适应过程
 * 1、当获取到注册中心，也就是URL中的协议为zookeeper时，这里自适应得到的扩展为
 *   {@link org.apache.dubbo.registry.zookeeper.ZookeeperRegistryFactory}
 * 2、但是ZookeeperRegistryFactory中没有重写getRegistry方法
 * 3、这样便会调用到ZookeeperRegistryFactory的父类，也就是
 *   {@linkplain AbstractRegistryFactory#getRegistry(URL)}
 * 4、这样最终会调用到{@linkplain AbstractRegistryFactory#createRegistry(URL)}
 * 5、而此时其实this表示的是ZookeeperRegistryFactory,这样便会重新调用到
 *   {@linkplain org.apache.dubbo.registry.zookeeper.ZookeeperRegistryFactory#createRegistry(URL)}
 */
@SPI("dubbo")
public interface RegistryFactory {

    /**
     * Connect to the registry
     * <p>
     * Connecting the registry needs to support the contract: <br>
     * 1. When the check=false is set, the connection is not checked, otherwise the exception is thrown when disconnection <br>
     * 2. Support username:password authority authentication on URL.<br>
     * 3. Support the backup=10.20.153.10 candidate registry cluster address.<br>
     * 4. Support file=registry.cache local disk file cache.<br>
     * 5. Support the timeout=1000 request timeout setting.<br>
     * 6. Support session=60000 session timeout or expiration settings.<br>
     *
     * @param url Registry address, is not allowed to be empty
     * @return Registry reference, never return empty value
     */
    @Adaptive({"protocol"})
    Registry getRegistry(URL url);

}