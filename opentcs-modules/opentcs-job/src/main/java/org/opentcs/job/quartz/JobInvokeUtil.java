package org.opentcs.job.quartz;

import org.opentcs.job.domain.SysJob;
import org.springframework.util.StringUtils;
import org.opentcs.common.core.utils.SpringUtils;

import java.lang.reflect.Method;

/**
 * 任务执行工具
 * invokeTarget 格式：beanName.methodName  或  beanName.methodName('param1', 123)
 */
public class JobInvokeUtil {

    public static void invokeMethod(SysJob sysJob) throws Exception {
        String invokeTarget = sysJob.getInvokeTarget().trim();
        // 去掉末尾的空括号
        if (invokeTarget.endsWith("()")) {
            invokeTarget = invokeTarget.substring(0, invokeTarget.length() - 2).trim();
        }

        // 解析：beanName.method 或 beanName.method(params)
        int parenIdx = invokeTarget.lastIndexOf('(');
        String beanAndMethod;
        String params = "";
        if (parenIdx > 0) {
            beanAndMethod = invokeTarget.substring(0, parenIdx).trim();
            int endParen = invokeTarget.endsWith(")") ? invokeTarget.length() - 1 : invokeTarget.length();
            params = invokeTarget.substring(parenIdx + 1, endParen).trim();
        } else {
            beanAndMethod = invokeTarget;
        }

        int dotIdx = beanAndMethod.lastIndexOf('.');
        String beanName   = beanAndMethod.substring(0, dotIdx);
        String methodName = beanAndMethod.substring(dotIdx + 1);

        if (!SpringUtils.containsBean(beanName)) {
            throw new JobBeanUnavailableException(beanName);
        }

        Object bean = SpringUtils.getBean(beanName);

        if (!StringUtils.hasText(params)) {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(bean);
        } else {
            Object[] paramValues = resolveParams(params);
            Class<?>[] paramTypes = new Class<?>[paramValues.length];
            for (int i = 0; i < paramValues.length; i++) {
                paramTypes[i] = paramValues[i].getClass();
            }
            Method method = bean.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            method.invoke(bean, paramValues);
        }
    }

    private static Object[] resolveParams(String params) {
        if (!StringUtils.hasText(params)) {
            return new Object[0];
        }
        String[] parts = params.split(",");
        Object[] result = new Object[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            if ((p.startsWith("'") && p.endsWith("'")) ||
                    (p.startsWith("\"") && p.endsWith("\""))) {
                result[i] = p.substring(1, p.length() - 1);
            } else if (p.equalsIgnoreCase("true") || p.equalsIgnoreCase("false")) {
                result[i] = Boolean.parseBoolean(p);
            } else if (p.contains(".")) {
                result[i] = Double.parseDouble(p);
            } else {
                result[i] = Long.parseLong(p);
            }
        }
        return result;
    }
}
