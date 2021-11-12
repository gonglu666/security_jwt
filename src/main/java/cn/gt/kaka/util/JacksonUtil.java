package cn.gt.kaka.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Json utility
 * @author Neo.Li
 */
public class JacksonUtil {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	static{
		//allow empty object to be serialized into {}
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

    public static <T> T convertValue(Object fromValue, Class<T> toValueType){
        try {
            return OBJECT_MAPPER.convertValue(fromValue, toValueType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The given object: "
                    + fromValue.getClass() + " cannot be transformed to " + toValueType + ". Detail message : " + e.getMessage());
        }
    }
    
    public static <T> List<T> convertList(Object fromValue, Class<T> toValueType){
        try {
        	String jsonStr = OBJECT_MAPPER.writeValueAsString(fromValue);
        	JavaType javaType = getCollectionType(ArrayList.class, toValueType); 
        	return OBJECT_MAPPER.readValue(jsonStr, javaType);
        } catch (IOException e) {
			throw new IllegalArgumentException("The given object: "
                    + fromValue.getClass() + " cannot be transformed to " + toValueType + ". Detail message : " + e.getMessage());
		} catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The given object: "
                    + fromValue.getClass() + " cannot be transformed to " + toValueType + ". Detail message : " + e.getMessage());
        }
    }

    //This method is used convert the Jackson String to corresponding list of class
    public static<T> List<T>convertList(String jsonStr,Class<T> toValueType) {
    	try {
    		TypeFactory typeFactory =OBJECT_MAPPER.getTypeFactory();
    		JavaType javaType=typeFactory.constructCollectionType(ArrayList.class,toValueType);  		
    		return OBJECT_MAPPER.readValue(jsonStr,javaType);
    	} catch (IOException e) {
			throw new IllegalArgumentException("The given String: "
                    + jsonStr + " cannot be transformed to " + toValueType + ". Detail message : " + e.getMessage());
		} catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The given String: "
                    + jsonStr+ " cannot be transformed to " + toValueType + ". Detail message : " + e.getMessage());
        }
    }
    
    public static <T> T convertValue(String fromValue, Class<T> toValueType){
    	return fromString(fromValue, toValueType);
    }
    
    public static <T> T fromString(String string, Class<T> clazz) {
    	if(string == null || string.equals("")) {
    		return null;
    	}
    	if(clazz.isAssignableFrom(String.class)) {
    		return clazz.cast(string);
    	}
        try {
            return OBJECT_MAPPER.readValue(string, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: "
                + string + " cannot be transformed to " + clazz + ". Detail message : " + e.getMessage());
        }
    }
 
    public static String toString(Object value) {
    	if(value == null) {
    		return null;
    	}
    	if(value instanceof String) {
    		return (String) value;
    	}
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The given Json object value: "
                + value + " cannot be transformed to a String. Detail message : " + e.getMessage());
        }
    }
 
    public static JsonNode toJsonNode(String value) {
    	if(value == null || value.equals("")) {
    		return null;
    	}
        try {
            return OBJECT_MAPPER.readTree(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
 
    @SuppressWarnings("unchecked")
	public static <T> T clone(T value) {
        return fromString(toString(value), (Class<T>) value.getClass());
    }
    
    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {   
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);   
    }    
}
