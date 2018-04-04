package com.awesprojects.lmsclient.api.internal;

import com.awesprojects.lmsclient.api.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

public class ApiCallHandler<T> implements InvocationHandler {

    public static final String NAME = "APICallHandler";
    private static final Logger log = Logger.getLogger(NAME);

    private T proxied;
    private Class<? super T> proxiedInterface;

    ApiCallHandler(T proxied,Class<? super T> interfaceType){
        this.proxied = proxied;
        this.proxiedInterface = interfaceType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(proxied, args);
        }catch (InvocationTargetException throwable){
            log.warning("error occurred in " + proxiedInterface.getName() + "." + method.getName()
                    + " -> " + throwable.getTargetException());
            return new Response(Response.STATUS_API_ERROR,throwable.getTargetException().toString());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createAndWrap(T toProxy,Class<? super T> interfaceType){
        ApiCallHandler<T> handler = new ApiCallHandler<>(toProxy,interfaceType);
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},handler);
    }

}
