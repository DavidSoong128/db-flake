package com.horizon.flake.store;

import com.horizon.flake.common.Constants;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 10:27
 * @see
 * @since : 1.0.0
 */
public class DataSourcePool {

    private Logger logger = LoggerFactory.getLogger(DataSourcePool.class);

    private  BasicDataSource dataSource;

    private DataSourcePool(){
        initDataSource();
    }

    private static class DataSourcePoolHolder{
        private static DataSourcePool dbcpHolder = new DataSourcePool();
    }

    public static DataSourcePool poolHolder(){
        return DataSourcePoolHolder.dbcpHolder;
    }

    public Connection getConnection(){
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("getConn error", e);
        }
        return conn;
    }

    public void closeConn(Connection conn){
        try {
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("closeConn error", e);
        }
    }

    public void closeStatement(Statement statement){
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("closeStatement error", e);
            }
        }
    }

    private void initDataSource() {
        dataSource = new BasicDataSource();
        //2.为数据源实例指定必须的属性
        dataSource.setUsername(Constants.JDBC_USERNAME);
        dataSource.setPassword(Constants.JDBC_PASSWORD);
        dataSource.setUrl(Constants.JDBC_URL);
        dataSource.setDriverClassName(Constants.JDBC_DRIVER);
        //3。指定数据源的一些可选的属性
        //1)指定数据库连接池中初始化连接数的个数
        dataSource.setInitialSize(Constants.JDBC_POOL_INITSIZE);
        //2)指定最大的连接数:同一时刻同时向数据库申请的连接数
        //最大空闲数，放洪峰过后，连接池中的连接过多，
        dataSource.setMaxActive(Constants.JDBC_POOL_MAXACTIVE);
        //3)指定最小连接数:数据库空闲状态下所需要保留的最小连接数
        //防止当洪峰到来时，再次申请连接引起的性能开销；
        dataSource.setMinIdle(Constants.JDBC_POOL_MINIDLE);
        dataSource.setMaxIdle(Constants.JDBC_POOL_MAXIDLE);
        //4)最长等待时间:等待数据库连接的最长时间，单位为毫秒，超出将抛出异常
        dataSource.setMaxWait(1000*5);
        //在空闲连接回收器线程运行期间休眠的时间值,以毫秒为单位.
        dataSource.setTimeBetweenEvictionRunsMillis(10000);
        //在每次空闲连接回收器线程(如果有)运行时检查的连接数量
        dataSource.setNumTestsPerEvictionRun(10);
        //连接在池中保持空闲而不被空闲连接回收器线程
        dataSource.setMinEvictableIdleTimeMillis(10000);
        dataSource.setValidationQuery("SELECT NOW() FROM DUAL");
    }
}
