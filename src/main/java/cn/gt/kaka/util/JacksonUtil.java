package cn.gt.kaka.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Jackson JSON工具类
 * 
 * 提供JSON字符串与Java对象之间的转换功能，封装Jackson的常用操作
 * 支持对象序列化、反序列化、类型转换等功能，简化JSON处理代码
 * 
 * 主要功能：
 * 1. Java对象与JSON字符串的相互转换
 * 2. 对象类型转换和深拷贝
 * 3. 集合类型的JSON处理
 * 4. JsonNode的创建和操作
 * 
 * @author 李威 (Neo.Li)
 * @version 2.0
 * @since 1.0
 */
@Slf4j
public final class JacksonUtil {

    /**
     * 全局Jackson对象映射器
     * 配置为允许空对象序列化，避免序列化失败
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    static {
        // 允许空对象序列化为 {}，避免序列化空对象时抛出异常
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private JacksonUtil() {
        throw new AssertionError("JacksonUtil工具类不应被实例化");
    }

    /**
     * 对象类型转换
     * 
     * 将源对象转换为目标类型的对象，常用于不同类型间的数据转换
     * 内部使用Jackson的convertValue方法，支持复杂对象的转换
     * 
     * @param <T> 目标类型
     * @param fromValue 源对象，待转换的对象
     * @param toValueType 目标类型的Class对象
     * @return 转换后的目标类型对象
     * @throws IllegalArgumentException 当转换失败时抛出异常
     */
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        if (fromValue == null) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.convertValue(fromValue, toValueType);
        } catch (IllegalArgumentException e) {
            String errorMsg = String.format("对象类型转换失败: %s -> %s, 错误: %s", 
                    fromValue.getClass().getSimpleName(), toValueType.getSimpleName(), e.getMessage());
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }
    
    /**
     * 对象转换为集合类型
     * 
     * 将源对象转换为指定元素类型的List集合
     * 适用于将复杂对象转换为对象列表的场景
     * 
     * @param <T> 集合元素类型
     * @param fromValue 源对象
     * @param elementType 集合元素的Class对象
     * @return 转换后的List集合
     * @throws IllegalArgumentException 当转换失败时抛出异常
     */
    public static <T> List<T> convertToList(Object fromValue, Class<T> elementType) {
        if (fromValue == null) {
            return new ArrayList<>();
        }
        
        try {
            String jsonStr = OBJECT_MAPPER.writeValueAsString(fromValue);
            JavaType listType = getCollectionType(ArrayList.class, elementType);
            return OBJECT_MAPPER.readValue(jsonStr, listType);
        } catch (IOException e) {
            String errorMsg = String.format("对象转换为List失败: %s -> List<%s>, 错误: %s", 
                    fromValue.getClass().getSimpleName(), elementType.getSimpleName(), e.getMessage());
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }

    /**
     * JSON字符串转换为集合类型
     * 
     * 将JSON字符串解析为指定元素类型的List集合
     * 常用于API响应数据的解析
     * 
     * @param <T> 集合元素类型
     * @param jsonStr JSON字符串
     * @param elementType 集合元素的Class对象
     * @return 解析后的List集合
     * @throws IllegalArgumentException 当解析失败时抛出异常
     */
    public static <T> List<T> parseList(String jsonStr, Class<T> elementType) {
        if (!hasText(jsonStr)) {
            return new ArrayList<>();
        }
        
        try {
            TypeFactory typeFactory = OBJECT_MAPPER.getTypeFactory();
            JavaType listType = typeFactory.constructCollectionType(ArrayList.class, elementType);
            return OBJECT_MAPPER.readValue(jsonStr, listType);
        } catch (IOException e) {
            String errorMsg = String.format("JSON字符串解析为List失败: %s -> List<%s>, 错误: %s", 
                    jsonStr, elementType.getSimpleName(), e.getMessage());
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }
    
    /**
     * JSON字符串转换为对象
     * 
     * 将JSON字符串反序列化为指定类型的Java对象
     * 支持基本类型和复杂对象类型的转换
     * 
     * @param <T> 目标对象类型
     * @param jsonStr JSON字符串
     * @param clazz 目标类型的Class对象
     * @return 反序列化后的Java对象
     * @throws IllegalArgumentException 当反序列化失败时抛出异常
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        if (!hasText(jsonStr)) {
            return null;
        }
        
        // 如果目标类型是String，直接返回
        if (String.class.isAssignableFrom(clazz)) {
            return clazz.cast(jsonStr);
        }
        
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (IOException e) {
            String errorMsg = String.format("JSON字符串解析为对象失败: %s -> %s, 错误: %s", 
                    jsonStr, clazz.getSimpleName(), e.getMessage());
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }
 
    /**
     * 对象转换为JSON字符串
     * 
     * 将Java对象序列化为JSON格式的字符串
     * 支持基本类型、复杂对象和集合类型的序列化
     * 
     * @param value 待序列化的对象
     * @return JSON字符串，如果输入为null则返回null
     * @throws IllegalArgumentException 当序列化失败时抛出异常
     */
    public static String toJsonString(Object value) {
        if (value == null) {
            return null;
        }
        
        // 如果已经是字符串，直接返回
        if (value instanceof String) {
            return (String) value;
        }
        
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            String errorMsg = String.format("对象序列化为JSON失败: %s, 错误: %s", 
                    value.getClass().getSimpleName(), e.getMessage());
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }
 
    /**
     * JSON字符串转换为JsonNode
     * 
     * 将JSON字符串解析为Jackson的JsonNode对象，便于进行JSON结构的操作
     * JsonNode提供了灵活的JSON数据访问方式
     * 
     * @param jsonStr JSON字符串
     * @return JsonNode对象，如果输入为空则返回null
     * @throws IllegalArgumentException 当解析失败时抛出异常
     */
    public static JsonNode parseJsonNode(String jsonStr) {
        if (!hasText(jsonStr)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (IOException e) {
            String errorMsg = String.format("JSON字符串解析为JsonNode失败: %s, 错误: %s", 
                    jsonStr, e.getMessage());
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }
 
    /**
     * 对象深拷贝
     * 
     * 通过JSON序列化和反序列化实现对象的深度拷贝
     * 适用于需要完全独立副本的场景
     * 
     * @param <T> 对象类型
     * @param value 源对象
     * @return 深拷贝后的新对象
     * @throws IllegalArgumentException 当拷贝失败时抛出异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T value) {
        if (value == null) {
            return null;
        }
        
        String jsonStr = toJsonString(value);
        return parseObject(jsonStr, (Class<T>) value.getClass());
    }
    
    /**
     * 构建集合类型的JavaType
     * 
     * @param collectionClass 集合类型
     * @param elementClasses 元素类型
     * @return JavaType对象
     */
    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
    
    /**
     * 检查字符串是否有文本内容
     * 
     * @param str 待检查的字符串
     * @return true表示有内容，false表示为null或空字符串
     */
    private static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // ======================= 兼容性方法 =======================
    // 为了保持向后兼容性，保留原有的方法名

    /**
     * @deprecated 使用 {@link #convertToList(Object, Class)} 替代
     */
    @Deprecated
    public static <T> List<T> convertList(Object fromValue, Class<T> toValueType) {
        return convertToList(fromValue, toValueType);
    }

    /**
     * @deprecated 使用 {@link #parseList(String, Class)} 替代
     */
    @Deprecated
    public static <T> List<T> convertList(String jsonStr, Class<T> toValueType) {
        return parseList(jsonStr, toValueType);
    }

    /**
     * @deprecated 使用 {@link #parseObject(String, Class)} 替代
     */
    @Deprecated
    public static <T> T convertValue(String fromValue, Class<T> toValueType) {
        return parseObject(fromValue, toValueType);
    }

    /**
     * @deprecated 使用 {@link #parseObject(String, Class)} 替代
     */
    @Deprecated
    public static <T> T fromString(String string, Class<T> clazz) {
        return parseObject(string, clazz);
    }

    /**
     * @deprecated 使用 {@link #toJsonString(Object)} 替代
     */
    @Deprecated
    public static String toString(Object value) {
        return toJsonString(value);
    }

    /**
     * @deprecated 使用 {@link #parseJsonNode(String)} 替代
     */
    @Deprecated
    public static JsonNode toJsonNode(String value) {
        return parseJsonNode(value);
    }

    /**
     * @deprecated 使用 {@link #deepCopy(Object)} 替代
     */
    @Deprecated
    public static <T> T clone(T value) {
        return deepCopy(value);
    }
}
