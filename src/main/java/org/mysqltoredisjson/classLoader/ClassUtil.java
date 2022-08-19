package org.mysqltoredisjson.classLoader;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import org.mysqltoredisjson.classLoader.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://blog.csdn.net/YouShouRenSheng/article/details/125292113
 */
@Component
@Configurable
@EnableScheduling
public class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 调用类方法
     *
     * @param cls        类
     * @param methodName 方法名
     * @param paramsCls  方法参数类型
     * @param params     方法参数
     * @return
     */
    public static Object invoke(Class<?> cls, String methodName, Class<?>[] paramsCls, Object[] params) {
        Object result = null;
        try {
            Method method = cls.getDeclaredMethod(methodName, paramsCls);
            Object obj = cls.newInstance();
            result = method.invoke(obj, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BusinessException("java脚本执行失败，" + e.getMessage());
        }
        return result;
    }

    /**
     * 执行Java函数，参数和返回值都是String类型
     *
     * @param javaStr    脚本
     * @param className  类名
     * @param methodName 方法名
     * @param parameter  参数
     * @return
     */
    public Object convertByJava(String javaStr, String className, String methodName, Object parameter) {
        StringBuilder sb = new StringBuilder();
//        sb.append("import java.text.*;\n");
//        sb.append("import java.util.*;\n");
//        sb.append("import com.alibaba.fastjson.*;\n");
        sb.append(javaStr);
        //1. 创建自定义类加载器的实例，可以使用上面的自定义类加载器
        MemoryClassLoader classLoader = new MemoryClassLoader();
        Object result = null;
        try {
            logger.info("registerJava --> " + className);
            logger.info("registerJava --> " + sb.toString());
            //添加依赖的jar包, 如代码中引用的fastjson对应的jar
            classLoader.registerJava(className,sb.toString());
            // 编译
            logger.info("findClass--> " + className);
            //2. 加载指定的类
            Class clazz = classLoader.findClass(className);
            //3. 执行方法
            result = JSONUtil.isJsonArray(JSONUtil.toJsonStr(parameter)) ? ClassUtil.invoke(clazz, methodName, new Class[]{List.class}, new Object[]{parameter}) :
                    ClassUtil.invoke(clazz, methodName, new Class[]{Map.class}, new Object[]{parameter});
        } catch (Exception e) {
            throw new BusinessException("java脚本执行失败，compiler失败"+e.getMessage());
        }
        //输出结果
        logger.info("parameter:" + JSONObject.toJSONString(parameter));
        logger.info("result:" + JSONObject.toJSONString(result));
        if (null == result) {
            throw new BusinessException("java脚本执行失败，执行脚本后结果为空");
        }
        return result;
    }


    /**
     * 执行JS函数，参数和返回值都是String类型
     *
     * @param jsStr     脚本
     * @param func      方法名
     * @param parameter 参数
     * @return
     */
    public Object runJs(String jsStr, String func, Object... parameter) {
        String regular = jsStr;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");  //创建引擎实例
        Object result = "";
        try {
            engine.eval(regular); //编译
            if (engine instanceof Invocable) {
                result = ((Invocable) engine).invokeFunction(func, parameter); // 执行方法
                logger.info("parameter:" + JSONObject.toJSONString(parameter));
                logger.info("result:" + JSONObject.toJSONString(result));
                if (null == result) {
                    throw new BusinessException("js脚本执行失败，执行脚本后结果为空");
                }
                return result;
            }
        } catch (Exception e) {
            throw new BusinessException("js脚本执行失败,表达式runtime错误:" + e.getMessage());
        }

        return "";
    }

    public Object runDict(String jsStr, Object parameter) {
        //需要替换的map字符串    {\"model\":{\"A\":\"A模型\",\"B\":\"B模型\",\"C\":\"C模型\",\"D\":\"D模型\"}}
        Map<String, Map<String, String>> transformMap = JSONObject.parseObject(jsStr, HashMap.class);
        if (ObjectUtil.isEmpty(transformMap)) {
            throw new BusinessException("字典脚本错误，请检查脚本");
        }
        //判断是object对象还是list对象
        if (JSONUtil.isJsonArray(JSONUtil.toJsonStr(parameter))) {
            //被替换的
            List<Map<String, Object>> jsonObjects = objToList(parameter);
            //取到model这个key
            for (String key :
                    transformMap.keySet()) {
                //{\"A\":\"A模型\",\"B\":\"B模型\",\"C\":\"C模型\",\"D\":\"D模型\"}
                Map<String, String> stringStringMap = transformMap.get(key);
                jsonObjects.forEach(jsonObject -> {
                    String value = String.valueOf(jsonObject.get(key));
                    if (stringStringMap.containsKey(value)) {
                        jsonObject.put(key, stringStringMap.get(value));
                    }
                });
            }
            return jsonObjects;
        } else {
            //被替换的
            Map<String, Object> jsonObject = JSONObject.parseObject(parameter.toString());
            for (String key :
                    transformMap.keySet()) {
                //{\"A\":\"A模型\",\"B\":\"B模型\",\"C\":\"C模型\",\"D\":\"D模型\"}
                Map<String, String> stringStringMap = transformMap.get(key);

                String value = String.valueOf(jsonObject.get(key));
                if (stringStringMap.containsKey(value)) {
                    jsonObject.put(key, stringStringMap.get(value));
                }
            }
            return jsonObject;
        }

    }


    //object转list对象
    public List objToList(Object obj) {
        if (obj instanceof List<?>) {
            List list = new ArrayList<>((List<?>) obj);
            return list;
        }
        return null;
    }
}