# Spring Microservices with RabbitMQ Communication

## Overview

This project demonstrates a microservices-based architecture using Spring Boot, RabbitMQ, and Docker Compose. It includes three microservices **Product**, **Order**, and **Email** designed for an e-commerce application. These services communicate with each other via RabbitMQ to handle events such as product orders, customer notifications, and other actions across the application. The RabbitMQ integration allows real-time event streaming between microservices, showcasing the asynchronous communication model.

## Folder Structure

The main project folder includes the following:
- **Product-service**: Manages product-related operations.
- **Order-service**: Processes customer orders.
- **Email-service**: Sends notifications based on order and customer actions.
- **Compose.yml**: Runs all services in one network with a single command.
- **Readme.md**: Project documentation.

## Prerequisites

- **Docker** and **Docker Compose** installed on your machine.
- **Java JDK 17** or above for local development.
- **IntelliJ IDEA** Community Edition is recomended.

## Getting Started

Follow these steps to clone and run the project locally using Docker Compose.

### 1. Clone the Repository
```bash
git clone https://github.com/makerjose/spring-microservices-with-rabbitmq.git
cd spring-microservices-with-rabbitmq
```

### 2: Build the Microservices

Each microservice is a standalone Spring Boot project. You can build them using Maven:
```bash
cd product-service mvn clean install

cd ../order-service mvn clean install

cd ../email-service mvn clean install
```
Alternatively, you can use the Maven plugin in IntelliJ IDEA to build the project with a single click. 

### 3: Configure the Mail Server

The **Email Service** requires SMTP settings to send emails. Create a .env file in the resources directory. Update the following environment variables in your .env file:
```bash
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS=true
```

**How to Generate a Gmail App Password**
1. Go to Google Account Security Settings: https://myaccount.google.com/security.
2. Enable 2-Step Verification (if not already enabled).
3. Scroll down to App Passwords and select Mail as the app and Other (Custom Name) for the device.
4. Generate the password and copy it.
5. Use this password in SPRING_MAIL_PASSWORD.

### 4: Run the Services with Docker Compose

From the main project directory, use Docker Compose to start all the services, including RabbitMQ.
```bash
docker compose up --build
```

This will:
-	Launch RabbitMQ for handling message brokering.
-	Start each microservice and postgres on its designated port as specified in the compose.yml file.

### 5: Access Databases in Postgres

•	Access the Postgres container:
```bash
docker exec -it postgres psql -U postgres
```
•	To verify the databases, list all available databases and then quit:
```bash
\l
\q
```
 
### Testing RabbitMQ Communication

Each microservice has RabbitMQ producers and consumers set up for event communication. To test this:
1.	Send a message from one microservice to a RabbitMQ exchange/queue.
2.	Observe how other microservices that subscribe to this exchanges react to the event (check logs for message consumption).

### Swagger API Documentation
Here are the endpoints for APIs in the product and order service for purposes of testing.
- Product Service: http://localhost:8083/swagger-ui/index.html
- Order Service: http://localhost:8084/swagger-ui/index.html

## Stopping the Services

To stop all running containers, run:
```bash
docker compose down
```

## Additional Notes

-	Each microservice can be configured independently via its application.properties file, allowing custom RabbitMQ exchanges, queues, etc.
-	RabbitMQ and Postgres have their designated volumes for persisting data. 

## License

This project is licensed under the MIT License.