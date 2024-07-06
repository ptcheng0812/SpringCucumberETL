package etl.stepdefs;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import etl.data.APIData;
import etl.data.Kafka;
import etl.utilities.MethodHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static etl.utilities.MethodHelper.findNodeWithValue;

public class KafkaStepDef {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;
    @Autowired
    Kafka kafka;
    @Autowired
    APIData apiData;
    @Given("I connect to kafka with following props")
    public void iHaveConnectionToKafkaWithFollowingProps(DataTable table) {
        if (table != null) {
            Map<String, String> props_table = table.asMap();
            System.out.print(props_table);
            for (Map.Entry<String, String> entry : props_table.entrySet()) {
                kafka.setProps(entry.getKey(), entry.getValue());
//                kafka.setProps("bootstrapServers", bootstrapServers);
            }
        }
    }

    @When("I produce the string message {string} to topic {string}")
    public void iSendTheStringMessageToTopic(String arg0, String arg1) throws Exception {
        Properties props = new Properties();
        for ( Map.Entry<String, Object> kvp : kafka.props.entrySet()) {
            System.out.println("key: " + kvp.getKey() + " value: " + kvp.getValue());
            props.put(MethodHelper.getKafkaConstantValue(kvp.getKey()), MethodHelper.getKafkaClassName(kvp.getValue().toString()));
        }
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        Producer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = new ProducerRecord<>(arg1, arg0);
        producer.send(record);

        producer.close();
        System.out.println("String Message sent to Kafka topic successfully");
    }


    @Then("I consume the latest string message on topic {string} and assert with {string}")
    public void iReceiveTheLatestStringMessageOnTopicAndAssertWith(String arg0, String arg1) throws Exception {
        Properties props = new Properties();
        for ( Map.Entry<String, Object> kvp : kafka.props.entrySet()) {
            props.put(MethodHelper.getKafkaConstantValue(kvp.getKey()), MethodHelper.getKafkaClassName(kvp.getValue().toString()));
        }
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);


        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        TopicPartition partition = new TopicPartition(arg0, 0);
        consumer.assign(Collections.singletonList(partition));
        consumer.seekToEnd(Collections.singletonList(partition));
        long latestOffset = consumer.position(partition);
        if (latestOffset > 0) {
            consumer.seek(partition, latestOffset - 1);
        }
        // Poll for the latest message
        boolean messageReceived = false;
        String message = "";
        try {
            while (!messageReceived) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumed latest message: key = %s, value = %s, partition = %d, offset = %d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                    message = record.value();
                    messageReceived = true;
                    break; // Exit the loop after receiving the latest message
                }
            }
        } finally {
            consumer.close();
            System.out.println("Consumer closed successfully");
            Assert.assertEquals(message, arg1);
            System.out.println("Assertion completed successfully");
        }
    }

    @When("I produce the json message from file {string} to topic {string}")
    public void iProduceTheJsonMessageFromFileToTopic(String arg0, String arg1) throws Exception {
        Properties props = new Properties();
        for ( Map.Entry<String, Object> kvp : kafka.props.entrySet()) {
            props.put(MethodHelper.getKafkaConstantValue(kvp.getKey()), MethodHelper.getKafkaClassName(kvp.getValue().toString()));
        }
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        Producer<String, String> producer = new KafkaProducer<>(props);
        //Read Json File and Get Json String
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            File file = new File(arg0);
            Object jsonObject = objectMapper.readValue(file, Object.class);
            jsonString = objectMapper.writeValueAsString(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(arg1, jsonString);
        producer.send(record);
        producer.close();

        System.out.println("JSON message sent successfully");
    }

    @Then("I consume the latest json message on topic {string} and extract data from node {string}")
    public void iConsumeTheLatestJsonMessageOnTopicAndExtractDataFromNode(String arg0, String arg1) throws Exception {
        Properties props = new Properties();
        for ( Map.Entry<String, Object> kvp : kafka.props.entrySet()) {
            props.put(MethodHelper.getKafkaConstantValue(kvp.getKey()), MethodHelper.getKafkaClassName(kvp.getValue().toString()));
        }
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        TopicPartition partition = new TopicPartition(arg0, 0);
        consumer.assign(Collections.singletonList(partition));
        consumer.seekToEnd(Collections.singletonList(partition));
        long latestOffset = consumer.position(partition);
        if (latestOffset > 0) {
            consumer.seek(partition, latestOffset - 1);
        }
        // Poll for the latest message
        boolean messageReceived = false;
        String message = "";
        try {
            while (!messageReceived) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumed latest message: key = %s, value = %s, partition = %d, offset = %d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                    message = record.value();
                    messageReceived = true;
                    break; // Exit the loop after receiving the latest message
                }
            }
        } finally {
            consumer.close();
            System.out.println("Consumer closed successfully");
        }

        // Parse JSON string to JsonNode
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(message);
        if(rootNode.isArray()) { arrayNode = (ArrayNode) rootNode;}
        else {
            if (!Objects.equals(arg1, "")) {
                if(Objects.requireNonNull(findNodeWithValue(rootNode, arg1)).isArray()) {arrayNode = (ArrayNode) findNodeWithValue(rootNode, arg1);}
                if(Objects.requireNonNull(findNodeWithValue(rootNode, arg1)).isObject()) { arrayNode.add(findNodeWithValue(rootNode, arg1));}
            }
            else {arrayNode.add(rootNode);}
        }

        if(!arrayNode.isEmpty()) {
            apiData.setDataNode(arrayNode);
            System.out.println("Json Node: {" + arg1 + "} extracted successfully");
        } else {
            Assert.fail("This node from response data is empty. Please ensure a valid response return");
        }
    }
}
