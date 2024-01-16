package org.example.bind;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;

public class GenericReferenceProxy implements MethodInterceptor {
    /**
     * RPC 泛化调用服务
     */
    private final GenericService genericService;

    /**
     * RPC 泛化调用方法
     */
    private final String methodName;


    public GenericReferenceProxy(GenericService genericService, String methodName){
        this.genericService = genericService;
        this.methodName = methodName;
    }

    /**
     * 做一层代理控制，后续不止是可以使用 Dubbo 泛化调用，也可以是其他服务的泛化调用
     * 泛化调用文档：https://dubbo.apache.org/zh/docsv2.7/user/examples/generic-reference/
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] parameters = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = parameterTypes[i].getName();
        }
        return genericService.$invoke(methodName,parameters,args);
    }
}
