Feature: Kafka Testing

  Scenario: Kafka Topic Sending and Consume String Message then Assert
    Given I connect to kafka with following props
      | ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG     | StringSerializer.class.getName()   |
      | ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG   | StringSerializer.class.getName()   |
      | ConsumerConfig.GROUP_ID_CONFIG                 | "console-consumer-71128"           |
      | ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG   | StringDeserializer.class.getName() |
      | ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG | StringDeserializer.class.getName() |
      | ConsumerConfig.AUTO_OFFSET_RESET_CONFIG        | "latest"                           |
    When I produce the string message "A message from Spring ETL to Verify" to topic "test"
    Then I consume the latest string message on topic "test" and assert with "A message from Spring ETL to Verify"

  Scenario: Kafka Topic Sending and Consume Json Message then Extract Data
#    Given I connect to kafka with following props
#      | ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG     | StringSerializer.class.getName()   |
#      | ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG   | StringSerializer.class.getName()   |
#      | ConsumerConfig.GROUP_ID_CONFIG                 | "console-consumer-71128"           |
#      | ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG   | StringDeserializer.class.getName() |
#      | ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG | StringDeserializer.class.getName() |
#      | ConsumerConfig.AUTO_OFFSET_RESET_CONFIG        | "latest"                           |
    When I produce the json message from file "src/main/resources/kafka_produce.json" to topic "test"
    Then I consume the latest json message on topic "test" and extract data from node "products"