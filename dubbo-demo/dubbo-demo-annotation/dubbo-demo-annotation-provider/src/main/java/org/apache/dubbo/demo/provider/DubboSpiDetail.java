package org.apache.dubbo.demo.provider;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.rpc.Invoker;

/**
 * 这里贴出两个dubbo-spi生成的类的实例用来举例，方便后续遇到dubbo-spi时及时知道生成的扩展类大体的样子
 */
public class DubboSpiDetail {
    /**
     * 这里是{@linkplain org.apache.dubbo.rpc.Protocol#export(Invoker)}dubbo-spi生成的方法
     * @param arg0
     * @return
     * @throws org.apache.dubbo.rpc.RpcException
     */
    public org.apache.dubbo.rpc.Exporter export(org.apache.dubbo.rpc.Invoker arg0) throws org.apache.dubbo.rpc.RpcException {
        if (arg0 == null) throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument == null");
        if (arg0.getUrl() == null)
            throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument getUrl() == null");
        org.apache.dubbo.common.URL url = arg0.getUrl();
        String extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol());
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.rpc.Protocol) name from url (" + url.toString() + ") use keys([protocol])");
        org.apache.dubbo.rpc.Protocol extension =  ExtensionLoader.getExtensionLoader(org.apache.dubbo.rpc.Protocol.class).getExtension(extName);
        return extension.export(arg0);
    }

    /**
     * 这里是{@linkplain org.apache.dubbo.remoting.Transporter#bind(URL, ChannelHandler)}dubbo-spi生成的方法
     * @param arg0
     * @param arg1
     * @return
     * @throws org.apache.dubbo.remoting.RemotingException
     */
    public org.apache.dubbo.remoting.RemotingServer bind(org.apache.dubbo.common.URL arg0, org.apache.dubbo.remoting.ChannelHandler arg1) throws org.apache.dubbo.remoting.RemotingException {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        org.apache.dubbo.common.URL url = arg0;
        String extName = url.getParameter("server", url.getParameter("transporter", "netty"));
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.remoting.Transporter) name from url (" + url.toString() + ") use keys([server, transporter])");
        org.apache.dubbo.remoting.Transporter extension =  ExtensionLoader.getExtensionLoader(org.apache.dubbo.remoting.Transporter.class).getExtension(extName);
        return extension.bind(arg0, arg1);
    }
}
