package com.connect;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

import com.mydata.MyNoParalleSource;
//输出结果中两个流过来的数据也有可能不是你一条我一条的，而是有可能一个流快的时候就先来了多条
public class ConnectionDemo {
    public static void main(String[] args) throws Exception {
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取数据源
        //注意：针对此source，并行度只能设置为1
        DataStreamSource<Long> text1 = env.addSource(new MyNoParalleSource()).setParallelism(1);

        DataStreamSource<Long> text2 = env.addSource(new MyNoParalleSource()).setParallelism(1);

        SingleOutputStreamOperator<String> text2_str = text2.map(new MapFunction<Long, String>() {
            @Override
            public String map(Long value) throws Exception {
                // 这里是第二个数据源，字符串我加了一个前缀str_
                return "str_" + value;
            }
        });
        ConnectedStreams<Long, String> connectStream = text1.connect(text2_str);
        SingleOutputStreamOperator<Object> result = connectStream.map(new CoMapFunction<Long, String, Object>() {
            @Override
            public Object map1(Long value) throws Exception {
                // 在这里可以进行业务处理
                return value;
            }
            @Override
            public Object map2(String value) throws Exception {
                // 在这里也可以进行业务处理
                return value;
            }
        });

        //打印结果
        result.print().setParallelism(1);
        String jobName = ConnectionDemo.class.getSimpleName();
        env.execute(jobName);
    }
}
