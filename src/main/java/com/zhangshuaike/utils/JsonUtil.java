package com.zhangshuaike.utils;

import java.text.SimpleDateFormat;     
import java.util.ArrayList;     
import java.util.Collection;     
import java.util.Date;     
import java.util.HashMap;     
import java.util.Iterator;     
import java.util.List;     
import java.util.Map;     
    
import net.sf.ezmorph.MorpherRegistry;     
import net.sf.ezmorph.object.DateMorpher;     
import net.sf.json.JSONArray;     
import net.sf.json.JSONObject;     
import net.sf.json.JsonConfig;     
import net.sf.json.processors.JsonValueProcessor;     
import net.sf.json.util.JSONUtils;     
import net.sf.json.xml.XMLSerializer;     
/**
 * json转换的工具类   
 * @author shuaike
 *
 */
public class JsonUtil {     
    

    /**   
    * 从json串转换成实体对象   
    * @param jsonObjStr e.g. {'name':'get','dateAttr':'2009-11-12'}   
    * @param clazz Person.class   
    * @return   
    */    
    public static Object getDtoFromJsonObjStr(String jsonObjStr, Class clazz) {     
        return JSONObject.toBean(JSONObject.fromObject(jsonObjStr), clazz);     
    }     
    
    /**   
    * 从json串转换成实体对象，并且实体集合属性存有另外实体Bean   
    * @param jsonObjStr e.g. {'data':[{'name':'get'},{'name':'set'}]}   
    * @param clazz e.g. MyBean.class   
    * @param classMap e.g. classMap.put("data", Person.class)   
    * @return Object   
    */    
    public static Object getDtoFromJsonObjStr(String jsonObjStr, Class clazz, Map classMap) {     
        return JSONObject.toBean(JSONObject.fromObject(jsonObjStr), clazz, classMap);     
    }     
    
    /**   
    * 把一个json数组串转换成普通数组   
    * @param jsonArrStr  e.g. ['get',1,true,null]   
    * @return Object[]   
    */    
    public static Object[] getArrFromJsonArrStr(String jsonArrStr) {     
        return JSONArray.fromObject(jsonArrStr).toArray();     
    }     
    
    /**   
    * 把一个json数组串转换成实体数组   
    * @param jsonArrStr e.g. [{'name':'get'},{'name':'set'}]   
    * @param clazz e.g. Person.class   
    * @return Object[]   
    */    
    public static Object[] getDtoArrFromJsonArrStr(String jsonArrStr, Class clazz) {     
        JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);     
        Object[] objArr = new Object[jsonArr.size()];     
        for (int i = 0; i < jsonArr.size(); i++) {     
            objArr[i] = JSONObject.toBean(jsonArr.getJSONObject(i), clazz);     
        }     
        return objArr;     
    }     
    
    /**   
    * 把一个json数组串转换成实体数组，且数组元素的属性含有另外实例Bean   
    * @param jsonArrStr e.g. [{'data':[{'name':'get'}]},{'data':[{'name':'set'}]}]   
    * @param clazz e.g. MyBean.class   
    * @param classMap e.g. classMap.put("data", Person.class)   
    * @return Object[]   
    */    
    public static Object[] getDtoArrFromJsonArrStr(String jsonArrStr, Class clazz,     
            Map classMap) {     
        JSONArray array = JSONArray.fromObject(jsonArrStr);     
        Object[] obj = new Object[array.size()];     
        for (int i = 0; i < array.size(); i++) {     
            JSONObject jsonObject = array.getJSONObject(i);     
            obj[i] = JSONObject.toBean(jsonObject, clazz, classMap);     
        }     
        return obj;     
    }     
    
    /**   
    * 把一个json数组串转换成存放普通类型元素的集合   
    * @param jsonArrStr  e.g. ['get',1,true,null]   
    * @return List   
    */    
    public static List getListFromJsonArrStr(String jsonArrStr) {     
        JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);     
        List list = new ArrayList();     
        for (int i = 0; i < jsonArr.size(); i++) {     
            list.add(jsonArr.get(i));     
        }     
        return list;     
    }     
    
    /**   
    * 把一个json数组串转换成集合，且集合里存放的为实例Bean   
    * @param jsonArrStr e.g. [{'name':'get'},{'name':'set'}]   
    * @param clazz   
    * @return List   
    */    
    public static List getListFromJsonArrStr(String jsonArrStr, Class clazz) {     
        JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);     
        List list = new ArrayList();     
        for (int i = 0; i < jsonArr.size(); i++) {     
            list.add(JSONObject.toBean(jsonArr.getJSONObject(i), clazz));     
        }     
        return list;     
    }     
    
    /**   
    * 把一个json数组串转换成集合，且集合里的对象的属性含有另外实例Bean   
    * @param jsonArrStr e.g. [{'data':[{'name':'get'}]},{'data':[{'name':'set'}]}]   
    * @param clazz e.g. MyBean.class   
    * @param classMap e.g. classMap.put("data", Person.class)   
    * @return List   
    */    
    public static List getListFromJsonArrStr(String jsonArrStr, Class clazz, Map classMap) {     
        JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);     
        List list = new ArrayList();     
        for (int i = 0; i < jsonArr.size(); i++) {     
            list.add(JSONObject.toBean(jsonArr.getJSONObject(i), clazz, classMap));     
        }     
        return list;     
    }     
    
    /**   
    * 把json对象串转换成map对象   
    * @param jsonObjStr e.g. {'name':'get','int':1,'double',1.1,'null':null}   
    * @return Map   
    */    
    public static Map getMapFromJsonObjStr(String jsonObjStr) {     
        JSONObject jsonObject = JSONObject.fromObject(jsonObjStr);     
    
        Map map = new HashMap();     
        for (Iterator iter = jsonObject.keys(); iter.hasNext();) {     
            String key = (String) iter.next();     
            map.put(key, jsonObject.get(key));     
        }     
        return map;     
    }     
    
    /**   
    * 把json对象串转换成map对象，且map对象里存放的为其他实体Bean   
    * @param jsonObjStr e.g. {'data1':{'name':'get'},'data2':{'name':'set'}}   
    * @param clazz e.g. Person.class   
    * @return Map   
    */    
    public static Map getMapFromJsonObjStr(String jsonObjStr, Class clazz) {     
        JSONObject jsonObject = JSONObject.fromObject(jsonObjStr);     
    
        Map map = new HashMap();     
        for (Iterator iter = jsonObject.keys(); iter.hasNext();) {     
            String key = (String) iter.next();     
            map.put(key, JSONObject.toBean(jsonObject.getJSONObject(key), clazz));     
        }     
        return map;     
    }     
    
    /**   
     * 把json对象串转换成map对象，且map对象里存放的其他实体Bean还含有另外实体Bean   
     * @param jsonObjStr e.g. {'mybean':{'data':[{'name':'get'}]}}   
     * @param clazz e.g. MyBean.class   
     * @param classMap  e.g. classMap.put("data", Person.class)   
     * @return Map   
     */    
    public static Map getMapFromJsonObjStr(String jsonObjStr, Class clazz, Map classMap) {     
        JSONObject jsonObject = JSONObject.fromObject(jsonObjStr);     
    
        Map map = new HashMap();     
        for (Iterator iter = jsonObject.keys(); iter.hasNext();) {     
            String key = (String) iter.next();     
            map.put(key, JSONObject     
                    .toBean(jsonObject.getJSONObject(key), clazz, classMap));     
        }     
        return map;     
    }     
    
   
 
}