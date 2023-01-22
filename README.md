# Gateway App API Gateway
API Gateway App based on https://spring.io/projects/spring-cloud-gateway

Responsible for:
* routing to the services
* providing API documentation
* checking JWT token validity
* providing login and logout capabilities

## Local prerequisite
To start application locally we should have docker installed and running. 
We should run the docker-compose file with the command below
```
docker-compose up
```

## Local startup
To start application run.
```
./mvnw spring-boot:run
```
The service will be accessible on http://localhost:8080

## Testing
### Unit Tests
All unit tests are bound to "test" goal. Just run:
```
./mvnw test
```
# Integration Tests
All integration tests are bound to "integration-test" goal. Just run:
```
./mvnw integration-test
```

### Go to the browser
```
http://localhost:8080
```

### Cancel port-forwarding with
```
CTRL+C
```

## Routing
All routes are configured in [application.yml](src/main/resources/application.yml) via `spring.cloud.gateway.routes.*` properties

### Domains
Every domain should have its own route on `/$DOMAIN_NAME`. The naming pattern is `domain_$DOMAIN_NAME_route`
It should route to a service and provide users attributes that are extracted from token as headers (Forwarding filter) and clean unwanted headers (RemoveRequestHeader filter)

### Documentation
For every domain there should be a documentation available under `/docs/$DOMAIN_NAME`. The naming pattern is `doc_$DOMAIN_NAME_route` 
This route redirects to the api gateway swagger and load the api-docs of the domain in the url (RedirectTo filter).
In order to load the api-docs another route needs to be configured. The name pattern is `api_doc_$DOMAIN_NAME_route`
and routes to the api-docs of the domain's service (RewritePath filter).

## API documentation
API documentation is done via [swagger](https://swagger.io/) and [springdoc](https://springdoc.org/). The API Gateway will only present swagger, and 
the open api definitions will be loaded from the domain services (see Routing->Documentation)

## Token validation
Token validation is done via Spring Security and is setup in [`SecurityConfig`](src/main/java/com/gateway/app/apigateway/SecurityConfig.java) and configured via `spring.security.oauth2*` properties

## Login & Logout
Login and logout capabilities are implemented via [Single Page Application (SPA)](src/main/resources/static/login.html) using [firebase-ui](https://github.com/firebase/firebaseui-web).

After login of user following functions are available:
* refreshToken() - will refresh the token stored in `currentAccessToken` if expired (user refreshToken(true) to force refresh)
* logOut() - will sign out the current user and invalidate the `currentAccessToken 

