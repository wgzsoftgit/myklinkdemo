package com.Flinkkafka.flinkreadkafkadata;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
* @Description    KafkaProducerTest 发送Kafka消息
* @Author         0262000099 Hengtai Nie
* @CreateDate     2018/9/21 11:29
*/
public class KafkaProducerTest {

  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.put("bootstrap.servers", "192.168.220.129:9092");
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    Producer<String, String> producer = new KafkaProducer<>(props);
    int totalMessageCount = 10;
    for (int i = 0; i < totalMessageCount; i++) {                                     //&&
      String value = String.format("%d,%s,%d", System.currentTimeMillis(), "machine-1", currentMemSize());
      producer.send(new ProducerRecord<>("test1", value), new Callback() {
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
          if (exception != null) {
            System.out.println("Failed to send message with exception " + exception);
          }
        }
      });
      Thread.sleep(100L);
    }
    producer.close();
  }

  private static long currentMemSize() {
    return MemoryUsageExtrator.currentFreeMemorySizeInBytes();//很简单的工具类，提取当前可用内存字节数
  }
}
//https://blog.csdn.net/weixin_41939278/article/details/82803773