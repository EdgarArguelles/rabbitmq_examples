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
* routing (http://www.rabbitmq.com/tutorials/tutorial-four-java.html):
      Make it possible to subscribe only to a subset of the messages. For example, we will be able to direct only
      critical error messages to the log file (to save disk space), while still being able to print all
      of the log messages on the console.
      (run an instance of each Receivers and then run Sender to send messages to server)
* topic (http://www.rabbitmq.com/tutorials/tutorial-five-java.html):
      Make it possible to subscribe only to a subset of the messages, but using different criteria like severity and
      source
      (run an instance of each Receivers and then run Sender to send messages to server)

### run
$ gradle run

### build
$ gradle build