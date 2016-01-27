package com.horizon.flake.store;

import com.horizon.flake.core.EventSqlParser;
import com.horizon.flake.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 10:24
 * @see
 * @since : 1.0.0
 */
public class SQLExecutor {

    private Logger logger = LoggerFactory.getLogger(SQLExecutor.class);

    private DataSourcePool dataSourcePool = DataSourcePool.poolHolder();

    private EventSqlParser eventSqlParser = EventSqlParser.sqlParser();

    private static class SQLHolder {
        private static SQLExecutor sqlExecutor = new SQLExecutor();
    }

    public static SQLExecutor sqlHolder() {
        return SQLHolder.sqlExecutor;
    }

    public void executeSql(Object obj) {
        Connection connection = null;
        PreparedStatement statement = null;
        String sql = null;
        List<Object> parameterList = null;
        try {
            connection = dataSourcePool.getConnection();
            Map<String, List<Object>> tableMap = eventSqlParser.sqlParser(obj);
            if (null != tableMap && tableMap.size() > 0) {
                sql = tableMap.keySet().iterator().next();
                parameterList = tableMap.get(sql);
            } else {
                logger.error("parse object orm error,obj {}", JsonUtil.ObjectToJson(obj));
                return;
            }
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < parameterList.size(); i++) {
                statement.setObject(i + 1, parameterList.get(i));
            }
            statement.execute();
            logger.info("execute sql:" + sql + ";parameterList:" + JsonUtil.ObjectToJson(parameterList));
        } catch (Exception ex) {
            logger.error("execute sql exception:'" + sql + "';parameterList:'" + JsonUtil.ObjectToJson(parameterList)
                    , ex);
        } finally {
            dataSourcePool.closeStatement(statement);
            dataSourcePool.closeConn(connection);
        }
    }
}
