package org.example.bind;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.example.session.Configuration;

import java.util.HashMap;
import java.util.Map;

public class GenericReferenceRegistry {
    private final Configuration configuration;
    public GenericReferenceRegistry(Configuration configuration){
        this.configuration = configuration;
    }

    //泛化调用静态代理工厂
    private final Map<String, GenericReferenceProxyFactory> knowGenericReferences = new HashMap<>();

    public IGenericReference getGenericReference(String methodName){
        GenericReferenceProxyFactory genericReferenceProxyFactory = knowGenericReferences.get(methodName);
        if(genericReferenceProxyFactory == null){
            throw new RuntimeException("Type " + methodName + " is not known to the GenericReferenceRegistry.");
        }
        return genericReferenceProxyFactory.newInstance(methodName);
    }

    /**
     * 注册泛化调用服务接口方法
     * @param application   服务：api-gateway-test
     * @param interfaceName 接口：cn.bugstack.gateway.rpc.IActivityBooth
     * @param methodName    方法：sayHi 全局唯一
     */
    public void addGenericReference(String application, String interfaceName, String methodName) {
        // 获取基础服务（创建成本较高，内存存放获取）
        ApplicationConfig applicationConfig = configuration.getApplicationConfig(application);
        RegistryConfig registryConfig = configuration.getRegistryConfig(application);
        ReferenceConfig<GenericService> reference = configuration.getReferenceConfig(interfaceName);
        // 构建Dubbo服务
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(applicationConfig).registry(registryConfig).reference(reference).start();
        // 获取泛化调用服务
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);
        // 创建并保存泛化工厂
        knowGenericReferences.put(methodName, new GenericReferenceProxyFactory(genericService));
    }

}
