Vote Meet and Eat
=================

This is the source code for the latest version of the VoteMeetEat.com demo application.
This presentation (http://www.slideshare.net/chris.e.richardson/developing-applications-with-cloud-services-devnexus-2013) gives an overview of the application.
It uses a number of cloud services including http://www.twilio.com and http://factual.com to organize a get together at a restaurant.

The application is comprised of a number of different services (independently deployable applications)  that communicate via RabbitMQ.

These services implement the front-end UI:

* registration-sms - handles registration SMS messages from Twilio.com
* registration-webapp - implements user registration
* vme-webapp - front end application for votemeeteat

These services implement the backend:
* survey-user-management - maintains a geo-queryable database of users using MongoDB. It's a standalone application with a main().
* survey-management - manages the polling of users via Twilio.com. This service is deployed as a web application.
* vme-management - implements the concept of vote-meet-eat and uses survey-management to poll users. It's a standalone application with a main().
