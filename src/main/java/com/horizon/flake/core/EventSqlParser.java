package com.horizon.flake.core;

import com.horizon.flake.api.*;
import com.horizon.flake.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 10:20
 * @see
 * @since : 1.0.0
 */
public class EventSqlParser {

    private Logger logger = LoggerFactory.getLogger(EventSqlParser.class);

    private static class SqlParserHolder{
        private static EventSqlParser sqlParser = new EventSqlParser();
    }

    public static EventSqlParser sqlParser(){
        return SqlParserHolder.sqlParser;
    }
    /**
     * 获取线程ID
     * @param obj
     * @return
     */
    public Integer routeThreadId(Object obj){
        try {
            Class<?> c = obj.getClass();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value == null){
                    continue;
                }
                Annotation[] annots  = field.getDeclaredAnnotations();
                for (Annotation annotation : annots) {
                    if(annotation instanceof Route){
                        int hashCode = value.hashCode();
                        return Math.abs(hashCode % Constants.FLAKE_THREAD_POOL_SIZE);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("routeThreadId exception:",e);
        }
        return -1;
    }

    public Map<String,List<Object>> sqlParser(Object obj) {
        LinkedHashMap<Column, Object> columnMap = new LinkedHashMap<Column, Object>();
        LinkedHashMap<Condition, Object> conditionMap = new LinkedHashMap<Condition, Object>();
        String actionSymbol = null;
        String tableName = null;
        try {
            Class<?> c = obj.getClass();
            Table table =  (Table)c.getAnnotation(Table.class);
            tableName = table.value();
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                Object value = f.get(obj);
                if(value == null){
                    continue ;
                }
                if (value instanceof Action) {
                    Action action = (Action) value;
                    actionSymbol = action.getSymbol();
                    continue;
                }
                Annotation[] annots = f.getDeclaredAnnotations();
                for (Annotation annot : annots) {
                    if(annot instanceof Column) {
                        Column column = (Column) annot;
                        columnMap.put(column, value);
                    }
                    if (annot instanceof Condition) {
                        Condition condition = (Condition) annot;
                        conditionMap.put(condition, value);
                        continue;
                    }
                    if(annot instanceof Table){
                        tableName = (String)value;
                    }
                }
            }
            return getSql(actionSymbol, tableName, columnMap, conditionMap);
        } catch (Exception e) {
            logger.error("parse " + obj.getClass().getName() + "error", e);
        }
        return null;
    }

    private Map<String,List<Object>> getSql(String actionSymbol, String tableName, LinkedHashMap<Column, Object> columnMap,
                                            LinkedHashMap<Condition, Object> conditionMap) {
        Map<String,List<Object>> sqlParameMap = new HashMap<String,List<Object>>();

        StringBuffer whereStr = new StringBuffer();
        List<Object> whereValuesList = new ArrayList<Object>();
        if(conditionMap.size() > 0){
            for(Iterator<Condition> iterator = conditionMap.keySet().iterator();iterator.hasNext();){
                Condition condition = iterator.next();
                Object value = conditionMap.get(condition);
                whereStr.append(iterator.hasNext()?condition.column()+"=? and ":condition.column()+"=?");
                whereValuesList.add(value);
            }
        }

        switch (actionSymbol) {
            case "insert":
                StringBuffer columnStr = new StringBuffer();
                StringBuffer valuesStr = new StringBuffer();
                List<Object> valuesList = new ArrayList<Object>();
                for(Iterator<Column> iterator = columnMap.keySet().iterator();iterator.hasNext();){
                    Column column = iterator.next();
                    Object value = columnMap.get(column);
                    valuesList.add(value);
                    columnStr.append(iterator.hasNext()?column.name()+",":column.name());
                    valuesStr.append(iterator.hasNext()?"?,":"?");
                }
                String insertSql = "insert into " + tableName + "("+ columnStr + ") values(" + valuesStr + ")";
                sqlParameMap.put(insertSql, valuesList);
                break;
            case "update":
                StringBuffer updateColumnStr = new StringBuffer();
                List<Object> updateValuesList = new ArrayList<Object>();
                for(Iterator<Column> iterator = columnMap.keySet().iterator();iterator.hasNext();){
                    Column column = iterator.next();
                    Object value = columnMap.get(column);
                    if(column.autoOper() && value instanceof Integer){
                        Integer intValue = (Integer)value;
                        if(intValue < 0){
                            value = Math.abs(intValue);
                            updateColumnStr.append(iterator.hasNext()?column.name()+"="+column.name()+"-?,":column.name()+"="+column.name()+"-?");
                        }else{
                            updateColumnStr.append(iterator.hasNext()?column.name()+"="+column.name()+"+?,":column.name()+"="+column.name()+"+?");
                        }
                    }else{
                        updateColumnStr.append(iterator.hasNext()?column.name()+"=?,":column.name()+"=?");
                    }
                    updateValuesList.add(value);
                }
                updateValuesList.addAll(whereValuesList);
                String updateSql = "update " + tableName + " set " + updateColumnStr + " where " + whereStr;
                sqlParameMap.put(updateSql, updateValuesList);
                break;
            case "delete":
                String deleteSql = "delete from " + tableName + " where " + whereStr;
                sqlParameMap.put(deleteSql, whereValuesList);
                break;
            default :
                break;
        }
        return sqlParameMap;
    }
}
