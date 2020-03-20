package com;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


//离线计算
//它创建一个数字数据集，对每个数字进行平方并过滤掉所有奇数
public class WordCountJava2 {
    public static void main(String[] args) throws Exception {
        //构建环境
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //通过字符串构建数据集
        DataSet<Integer> numbers = env.fromElements(1, 2, 3, 4, 5, 6, 7);
        
     // Square every number
     DataSet<Integer> result = numbers.map(new MapFunction<Integer, Integer>() {
         @Override
         public Integer map(Integer integer) throws Exception {
             return integer * integer;
         }
     })
     // Leave only even numbers
     .filter(new FilterFunction<Integer>() {
         @Override
         public boolean filter(Integer integer) throws Exception {
             return integer % 2 == 0;
         }
     });
       
     result.print();
    }
  
}