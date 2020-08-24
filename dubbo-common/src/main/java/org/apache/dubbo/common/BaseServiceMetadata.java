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
package org.apache.dubbo.common;

import org.apache.dubbo.common.utils.StringUtils;

/**
 * 2019-10-10
 */

/**
 * 服务元数据
 * 包含版本号，分组，接口名
 */
public class BaseServiceMetadata {
    public static final char COLON_SEPERATOR = ':';
    /**
     * 服务的映射路径
     */
    protected String serviceKey;
    protected String serviceInterfaceName;
    protected String version;
    protected volatile String group;

    /**
     * 这里确定了服务唯一名称的格式
     * ->group/path:version
     * @param path      路径，大部分情况是接口名称
     * @param group     服务分组
     * @param version   服务版本
     * @return
     */
    public static String buildServiceKey(String path, String group, String version) {
        StringBuilder buf = new StringBuilder();
        if (group != null && group.length() > 0) {
            buf.append(group).append("/");
        }
        buf.append(path);
        if (version != null && version.length() > 0) {
            buf.append(":").append(version);
        }
        return buf.toString();
    }

    public static String versionFromServiceKey(String serviceKey) {
        int index = serviceKey.indexOf(":");
        if (index == -1) {
            return null;
        }
        return serviceKey.substring(index + 1);
    }

    public static String groupFromServiceKey(String serviceKey) {
        int index = serviceKey.indexOf("/");
        if (index == -1) {
            return null;
        }
        return serviceKey.substring(0, index);
    }

    public static String interfaceFromServiceKey(String serviceKey) {
        int groupIndex = serviceKey.indexOf("/");
        int versionIndex = serviceKey.indexOf(":");
        groupIndex = (groupIndex == -1) ? 0 : groupIndex + 1;
        versionIndex = (versionIndex == -1) ? serviceKey.length() : versionIndex;
        return serviceKey.substring(groupIndex, versionIndex);
    }

    /**
     * Format : interface:version
     *
     * @return
     */
    public String getDisplayServiceKey() {
        StringBuilder serviceNameBuilder = new StringBuilder();
        serviceNameBuilder.append(serviceInterfaceName);
        serviceNameBuilder.append(COLON_SEPERATOR).append(version);
        return serviceNameBuilder.toString();
    }

    /**
     * revert of org.apache.dubbo.common.ServiceDescriptor#getDisplayServiceKey()
     *
     * @param displayKey
     * @return
     */
    public static BaseServiceMetadata revertDisplayServiceKey(String displayKey) {
        String[] eles = StringUtils.split(displayKey, COLON_SEPERATOR);
        if (eles == null || eles.length < 1 || eles.length > 2) {
            return new BaseServiceMetadata();
        }
        BaseServiceMetadata serviceDescriptor = new BaseServiceMetadata();
        serviceDescriptor.setServiceInterfaceName(eles[0]);
        if (eles.length == 2) {
            serviceDescriptor.setVersion(eles[1]);
        }
        return serviceDescriptor;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void generateServiceKey() {
        this.serviceKey = buildServiceKey(serviceInterfaceName, group, version);
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public void setServiceInterfaceName(String serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
