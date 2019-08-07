package com.wolken.schedule.quartz;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;


import static com.wolken.schedule.quartz.TargetMethodRetrospect.TRIGGER_METHOD;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/1 11:36
 */
public class JobCallerCGlibProxy {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> clazz, Method m, Class[] constructorType,
            Object[] constructorArgs) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setInterfaces(new Class[] { TargetMethodRetrospect.class });
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if (m != null) {
                if (method.getName().equals(m.getName())) {
                    return proxy.invokeSuper(obj, args);
                }
            }
            if (TRIGGER_METHOD.equals(method.getName())) {
                return m.invoke(obj, args);
            }
            return null;
        });
        if (constructorArgs == null || constructorType == null) {
            return (T) enhancer.create();
        }
        return (T) enhancer.create(constructorType, constructorArgs);
    }

    public static <T> T createProxy(Class<T> clazz, Method m) {
        return createProxy(clazz, m, null, null);
    }

    public static <T> T createProxy(Class<T> clazz) {
        return createProxy(clazz, null);
    }

}
