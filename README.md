### RabbitMQ queues rabbitmq.helloWorld world
===============

Basic examples using RabbitMQ queues
 
### Dependencies
* Install and run a local RabbitMQ server (to list queues sudo rabbitmqctl list_queues)

### examples
* helloWorld (https://www.rabbitmq.com/tutorials/tutorial-one-java.html):
      Sender send 12 messages to server, this messages will be shared by Receivers, the server send 6 and 6 messages
      (run two instance of Receiver and then run Sender to send messages to server)
* workQueues (https://www.rabbitmq.com/tutorials/tutorial-two-java.html):
      Create a Work Queue that will be used to distribute time-consuming tasks among multiple workers, server will
      send all messages one by one when a Receive isn't busy
      (run two instance of Receiver and then run Sender to send messages to server)
* publishSubscribe (http://www.rabbitmq.com/tutorials/tutorial-three-java.html):
      Deliver a message to multiple consumers. This pattern is known as "publish/subscribe", essentially,
      published log messages are going to be broadcast to all the receivers.
      (run two instance of Receiver and then run Sender to send messages to server)

### run
$ gradle run

### build
$ gradle build