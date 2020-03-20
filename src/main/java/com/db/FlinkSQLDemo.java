package com.db;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
//flink拿到数据库数据
public class FlinkSQLDemo {
  public static void main(String[] args) throws Exception{

        //sql查询结果列类型
        TypeInformation[] fieldTypes = new TypeInformation[] {

                BasicTypeInfo.INT_TYPE_INFO,        //第一列数据类型
                BasicTypeInfo.STRING_TYPE_INFO,     //第二类数据类型
                BasicTypeInfo.INT_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.INT_TYPE_INFO           //BIG_DEC_TYPE_INFO     

        };

        RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes);
        JDBCInputFormat jdbcInputFormat = JDBCInputFormat.buildJDBCInputFormat()
                //数据库连接信息
                .setDrivername("com.mysql.jdbc.Driver")
                .setDBUrl("jdbc:mysql://127.0.0.1:3306/dbName?useSSL=false&useUnicode=true&characterEncoding=UTF-8")
                .setUsername("root")
                .setPassword("root")
                .setQuery("SELECT id,name,age,emp_no,score FROM test_users")//查询sql
                .setRowTypeInfo(rowTypeInfo)
                .finish();

//        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        //搭建flink
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //获取数据源
        DataSet s1 = env.createInput(jdbcInputFormat); // datasource
        System.out.println("数据行："+s1.count());
        s1.print();//此处打印
    }
}