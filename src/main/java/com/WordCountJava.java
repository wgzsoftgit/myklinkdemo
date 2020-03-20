package com;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


//离线计算
//Dataset是flink的常用程序，数据集通过source进行初始化，例如读取文件或者序列化集合，
//然后通过transformation（filtering、mapping、joining、grouping）将数据集转成，
//然后通过sink进行存储，既可以写入hdfs这种分布式文件系统，也可以打印控制台，flink可以有很多种运行方式，如local、flink集群、yarn等.
public class WordCountJava {
    public static void main(String[] args) throws Exception {
        //构建环境
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //通过字符串构建数据集
        DataSet<String> text = env.fromElements(
                "Who's there?",
                "I think I hear them. Stand, ho! Who's there?");
        //分割字符串、按照key进行分组、统计相同的key个数
        DataSet<Tuple2<String, Integer>> wordCounts = text
                .flatMap(new LineSplitter()) //&&&
                .groupBy(0)
                .sum(1);
        //打印
        wordCounts.print();
    }
    //分割字符串的方法
    public static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        public void flatMap(String line, Collector<Tuple2<String, Integer>> out) {
            for (String word : line.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }

		
    }
}