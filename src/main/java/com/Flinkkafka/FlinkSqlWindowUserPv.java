package com.Flinkkafka;

import java.sql.Timestamp;
import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.java.StreamTableEnvironment;


 
 // 使用FlinkSql 计算 10秒滚动窗口内 用户点击次数，之后自定义 sink To mysql
public class FlinkSqlWindowUserPv{
 
    public static void main(String[] args) throws Exception {
 
    	StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    	
    	env.setParallelism(8);
    	
    	TableConfig tc = new TableConfig();
    	
    	StreamTableEnvironment tableEnv = new StreamTableEnvironment(env, tc);
 
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.220.129:9092");
        properties.put("zookeeper.connect", "192.168.220.129:2181");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test6");
 
        FlinkKafkaConsumer010<String> myConsumer = new FlinkKafkaConsumer010<String>("test1", new SimpleStringSchema(),
                properties);
 
        DataStream<String> stream = env.addSource(myConsumer);
        
        DataStream<Tuple5<String, String, String, String, Long>> map = stream.map(new MapFunction<String, Tuple5<String, String, String, String,Long>>() {
 
        	private static final long serialVersionUID = 1471936326697828381L;
 //12 223 3342 332 222 1212
			@Override
			public Tuple5<String, String, String, String,Long> map(String value) throws Exception {
				String[] split = value.split(" ");
				return new Tuple5<String, String, String, String,Long>(split[0],split[1],split[2],split[3],Long.valueOf(split[4])*1000);
			}
		});
        
        map.print(); //打印流数据
        System.out.println("kafka数据");
        
        //注册为user表
        tableEnv.registerDataStream("Users", map, "userId,itemId,categoryId,behavior,timestampin,proctime.proctime"); 
        
        //执行sql查询     滚动窗口 10秒    计算10秒窗口内用户点击次数
        Table sqlQuery = tableEnv.sqlQuery("SELECT TUMBLE_END(proctime, INTERVAL '10' SECOND) as processtime,"
        		+ "userId,count(*) as pvcount "
        		+ "FROM Users "
        		+ "GROUP BY TUMBLE(proctime, INTERVAL '10' SECOND), userId");
        
        
        //Table 转化为 DataStream 
        DataStream<Tuple3<Timestamp, String, Long>> appendStream = tableEnv.toAppendStream(sqlQuery,Types.TUPLE(Types.SQL_TIMESTAMP,Types.STRING,Types.LONG));
        
        appendStream.print();
        System.out.println("flink数据");
        
        //sink to mysql
        appendStream.map(new MapFunction<Tuple3<Timestamp,String,Long>, UserPvEntity>() {
 
			private static final long serialVersionUID = -4770965496944515917L;
 
			@Override
			public UserPvEntity map(Tuple3<Timestamp, String, Long> value) throws Exception {
				
				return new UserPvEntity(Long.valueOf(value.f0.toString()),value.f1,value.f2);
			}
		}).addSink(new SinkUserPvToMySQL2());   //&&&&&&
        appendStream.print().setParallelism(1);
        env.execute("userPv from Kafka");
 
    }
    
}
//https://blog.csdn.net/qq_20672231/article/details/84936716