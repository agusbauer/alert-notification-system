# Alert Notification System

Java pager alert system.
The application can process alerts from different monitored services and send notifications to the corresponding users.

## Use cases

 **Register a new service alert:**
 Given an existing serviceId and a message. It registers the alarm, notifies the corresponding users and set an ack timeout.
 
 **Acknowledge an open alert:**
 Given an existing alertId. It sets the current alarm status to acknowledge.
 
 **Set service as healthy:**
 Given an existing serviceId. It set the unhealthy system to a healthy status.
 
 **Process an alert ack timeout:**
 Given an existing alertId. If the alert is not acknowledge and the system is unhealthy it will send the notification to the next users according to the escalation level.

## Application Structure

Code folder: `alert-notification-system/pager-application/src/main/`.  
Tests folder: `alert-notification-system/pager-application/src/test/`. 

```
├── application.port.in
|       Entry interfaces to the application. The use cases are here.
|       There are also the corresponding commands to each use case with their validations       
|
├── application.port.out
|       Exit interfaces to allow the application to interact with external services. 
|       The repositories and distributed lock interfaces are here
|
├── application.port.helpers
|       Helper interfaces that are implemented inside the project. 
|       i.e: NotificationsHelper 
|
├── application.service
|       Implementation of use cases and helper interfaces.
|       Using domain models and out ports in order to perform the business logic
|
├── common
|       Common clases that can be used in the implementation. i.e:exceptions
|
├── domain
|       The entities with their particular logic are here
└──
```

### Entities

 * MonitoredService 
 ```
{
	"id": "ssss",
	"status": "healthy or unhealthy"
}
```

 * Alert
```
{
	"id": "aaaa",
	"monitoredServiceId": "ssss",
	"message": "something happened",
	"lastNotifiedLevelId": "llll",
	"status": "open or acknowledge"
}
```

* Escalation
```
{
	"id": "xxxx",
	"serviceId": "sssss",
	"levels": [{
		"id": "llll",
		"targets": [{
			"id": "tttt",
			"type": "mail or sms",
			"email": "example@aaa.com",
			"phone_number": "12345678"
		}]
	}]
}
```

## Considerations

### Concurrent issues solutions
In order to avoid concurrency issues, optimistic locking and strong consistency is assumed from the persistence adapter.
(In the code, if the save method returns false this mean an optimistic locking failure).
There is also a LockPort that is used as a distributed lock. (I imagine that this could be implemented with Redis and having a default TTL)

I noticed the following concurrent cases:
* **More than one alert from a service is received at the same time:** 
To solve this I decided to rely on the optimistic locking from the monitoredServiceRepository 
* **More than one Alert Timeout event for the same alert happen at the same time:**
To solve this I decided to rely on the optimistic locking from the alertRepository. 
* **Alert Ack followed by an Alert TimeOut must not send any notifications:**
I solved this locking before read and update the alert and locking before read and check if the alert is acknowledge.
* **Health service event followed by an Alert TimeOut must not send any notifications:**
I solved this locking before read and update the status of the monitored service and locking before read and check the status.

### Other considerations

I assumed that the monitored services are already stored into the pager db.

Despite that there is one persistence adapter. I decided to use two repositories(one for each entity in the pager db). 
Maybe those interfaces could be implemented with the same adapter but i think this could give more flexibility

The save methods on the repositories are used for save a new object and also for update an existing one.

In case of not found a resource in a repository when is required, a NotFoundException will be thrown. 
The application is not doing any error handling because that can be part of the adapters that are interacting with the in-ports

In case that the last escalation level is reached and there is an ack timeout, instead of throwing an error, I chose to send the notification to the last level again.

Metrics and logging tools are not taking in consideration in this exercise just for simplicity.
For example adding New Relic and Kibana tags when an exception occurs to keep track of the errors.

Because the adapters are not implemented, the application does not contains a main file so it cannot be run but it's possible to run the tests.

## How to run the tests

### Requirements

* Maven 3.
* Java 8 jdk. [installation guide](https://mkyong.com/java/how-to-install-java-on-mac-osx/)

### Steps
1) Go to the folder `/alert-notification-system/pager-application`
2) Run `mvn clean test`
3) In order to see the resulting test coverage. Open: `/alert-notification-system/pager-application/target/site/jacoco/index.html`

To just compile the project run `mvn compile`

## Libraries used
* [Junit5](https://junit.org/junit5/) Unit testing engine
* [Mockito](https://site.mockito.org/) Mocking library