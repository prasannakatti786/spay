package com.spay.wallet.customerservice.config.kafka;

//import com.spondias.fintech.common.dto.CustomTopic;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaAdmin;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaProducerConfig {
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServer;
//
//    private Map<String, Object> producerConfig(){
//        var props =  new HashMap<String, Object>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return  props;
//    }
//
//    @Bean
//    public KafkaAdmin.NewTopics createTopic(){
//         var regenerateOto = TopicBuilder
//                .name(CustomTopic.REGENERATE_OTP).partitions(3)
//                .replicas(3).configs(Map.of("min.insync.replicas","2"))
//                .build();
//          return new KafkaAdmin.NewTopics(regenerateOto);
//    }
//
//    @Bean
//    public ProducerFactory<String, Object> producerFactory(){
//       return new DefaultKafkaProducerFactory<>(producerConfig());
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String,Object> producerFactory){
//        return new KafkaTemplate<>(producerFactory);
//    }

//}
