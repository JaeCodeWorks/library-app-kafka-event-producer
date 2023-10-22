# library-app-kafka-events-producer

[![Generic badge](https://img.shields.io/badge/LANGUAGE-Java_17-blue.svg)]()
[![Generic badge](https://img.shields.io/badge/DOKERIZED-YES-green.svg)]()

# Description
Library kafka events producer is spring-boot application which was built with 'contract-first api development'.
The application offers api that client can use to add or update a book in the Library.
Primary purpose of this app is to act as a microservice and have an asynchronous communication by sending event to Kafka topic,
where other microservices can consume the events from the topic.

## Getting started
These following instructions will get you a copy of the project up and running on your local machine
for development and testing purposes.

### Prerequisites
* OpenJDK Java 17 or greater
* Apache Kafka
* Setup the brokers and topics using the scripts

## Build
```
mvn clean install
```

## Start the service locally
```
mvn spring-boot:run
```

## Run spring Boot Application
### Run docker containers
From root dir run the following to start dockerised Kafka and Zookeeper:

```
docker-compose up -d
```

Multiple dockerised Kafka brokers can also be setup using following commands
```
docker-compose -f docker-compose-multi-broker.yml up
```

### Start spring boot application
```
java -jar target/library-app-kafka-events-producer-1.0-SNAPSHOT.jar
```

### Produce inbound LibraryEvent (manual curls)
Perform following curl in the terminal.
More curl examples can be found in 'curl-commands.txt' file.
```
POST WITH-NULL-LIBRARY-EVENT-ID
-------------------------------
curl -i \
-d '{"libraryEventId":null,"book":{"bookId":456,"bookName":"Head First Design Patterns","bookAuthor":"Eric Freeman"}}' \
-H "Content-Type: application/json" \
-X POST http://localhost:8080/library-api/v1/libraryevent
```

## Setup brokers and topic manually
If for some reason, manual setup is preferred rather than the dockerised Kafka.
Follow the instructions on 'SetupKafkaManually.md'

## Running Tests
Run integration and unit tests with 
```
mvn clean install
```

## Test naming standard
Naming standard for tests followed according to article below
https://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html

## Improvements
- Use Schema Registry for event validation(avro schema)
- Kafka-UI to have visibility of the events in the topic

## Authors
Shreejung Limbu
