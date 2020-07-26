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
package org.apache.dubbo.common.extension;

import org.apache.dubbo.common.lang.Prioritized;

/**
 * 这里封装了对于dubbo加载的分类和优先级，并且继承了Prioritized接口，是为了后面的排序
 * 目前分了3类
 * 1、DubboInternalLoadingStrategy--->dubbo内部class加载配置
 * 2、DubboLoadingStrategy        --->dubbo内部一些转换器等配置
 * 3、ServicesLoadingStrategy     --->dubbo的service加载配置
 */
public interface LoadingStrategy extends Prioritized {
    //加载的路径
    String directory();

    default boolean preferExtensionClassLoader() {
        return false;
    }

    default String[] excludedPackages() {
        return null;
    }


    /**
     * 如果出现重复的实例，此字段用来标识是否覆盖其他低优先级的实例
     */
    default boolean overridden() {
        return false;
    }
}
