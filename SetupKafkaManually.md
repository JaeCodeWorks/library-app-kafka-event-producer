## Setup Kafka brokers and topic manually

A step by step series for setting up Kafka broker and topics

```
> Download Install Apache Kafka from here (https://kafka.apache.org/downloads)
  Note: the following commands are for Apache Kafka 3+

> Spin Zookeeper
    - navigate to folder /bin
    - use command to spin zookeeper using the zookper.properties 
      ------------------------------------------------------------
      ./zookeeper-server-start.sh ../config/zookeeper.properties
      ------------------------------------------------------------
      
> Start the brokers
    - navigate to folder /config/server.properties
    - for this example we are using three brokers
      copy the server.properties file and name them 
      server.properties
      server1.properties
      server2.properties
    
    - accordingly change the following properties for those three files
    
      server1.properties
      ------------------------------------------------------------
      listeners=PLAINTEXT://localhost:9092
      auto.create.topics.enable=false
      ------------------------------------------------------------
      
      server2.properties
      ------------------------------------------------------------
      listeners=PLAINTEXT://localhost:9093
      auto.create.topics.enable=false
      ------------------------------------------------------------
      
      server3.properties
      ------------------------------------------------------------
      listeners=PLAINTEXT://localhost:9094
      auto.create.topics.enable=false
      ------------------------------------------------------------
     
    - navigate to folder /bin
    - use command to start each broker with the server properties created above
    
      Broker0
      ------------------------------------------------------------
      ./kafka-server-start.sh ../config/server.properties
      ------------------------------------------------------------
      
      Broker1
      ------------------------------------------------------------
      ./kafka-server-start.sh ../config/server1.properties
      ------------------------------------------------------------
      
      Broker2
      ------------------------------------------------------------
      ./kafka-server-start.sh ../config/server2.properties
      ------------------------------------------------------------

> Create the necessary topics
    
    - navigate to folder /bin
    - use following commands to create topic for all three brokers
    ---------------------------------------------------------------------------------------------------------------------------------------
    ./kafka-topics.sh --create --topic public.library.collection.v1 --replication-factor 1 --partitions 4 --bootstrap-server localhost:9092
    ./kafka-topics.sh --create --topic public.library.collection.v1 --replication-factor 1 --partitions 4 --bootstrap-server localhost:9093
    ./kafka-topics.sh --create --topic public.library.collection.v1 --replication-factor 1 --partitions 4 --bootstrap-server localhost:9094
    ---------------------------------------------------------------------------------------------------------------------------------------  
```

