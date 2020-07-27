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

import org.apache.dubbo.common.URL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Adaptive 可注解在类或方法上。
 * 当 Adaptive 注解在类上时，Dubbo 不会为该类生成代理类。
 * 注解在方法（接口方法）上时，Dubbo 则会为该方法生成代理逻辑。
 * Adaptive 注解在类上的情况很少，在 Dubbo 中，仅有两个类被 Adaptive 注解了，
 * 分别是 AdaptiveCompiler 和 AdaptiveExtensionFactory。
 * 此种情况，表示拓展的加载逻辑由人工编码完成。
 * 更多时候，Adaptive 是注解在接口方法上的，表示拓展的加载逻辑需由框架自动生成
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Adaptive {
    /**
     * value参数决定了哪一个目标扩展被注入，扩展类的名称放在URL上{@link URL}
     * 而参数的key则是由value决定
     *
     * 如果参数没有在URL中被找到，那么就会使用默认的扩展作为依赖注入（一般是有{@link SPI}注解的接口）
     *
     * 使用举例：
     * 如果这里的value={"key1", "key2"},在URL发现了参数key1，那么使用参数对应的值作为拓展的对象
     * 如果key没有发现，那么使用key2，如果key2在URL中也没有被发现，那么使用默认的拓展，如果还是没有，
     * 那么会抛出{@link IllegalStateException}异常
     *
     * 如果此value为空，那么会有一个生成value的规则：分割class名称，并且以'.'分隔开来，
     * 举例:如果是org.apache.dubbo.xxx.YyyInvokerWrapper的扩展，那么生成的value={"yyy.invoker.wrapper"}
     */
    String[] value() default {};

}