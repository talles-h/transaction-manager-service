# Transaction Manager Service
Provides APIs to store and retrieve transactions and perform currency convertion from USD to other currencies.

The service uses the API from https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange to get the exchange rate from USD to other currencies.

H2 database (in memory) is used by default, so no database installation is needed to execute this service.

When running with default configuration, the Swagger documentation can be accessed at URL http://localhost:8080/swagger-ui/index.html

## Automated tests (JUnit)
JUnit tests are diviced in two collections: Unit tests and Integration tests.

They will be executed based on the active profiles (see below).

* Unit tests
  * Testing a specific service or method.
  * Will run if the profile ***unit-tests*** is active.

* Integration tests
  * Depends on database and external API for exchange rate.
  * Will run if profile ***integration-tests*** is active.

By default, both profiles are activated in file *src/test/resources/application-test.properties*.

### TODO for Automated tests
Add more tests to cover all error cases and other success cases for possible edge cases.
We should also add unit test for other modules/methods.

## Overral improvements
For Production deployment, below would be needed to complete:
- Add support to AUTHORIZATION and AUTHENTICATION
  - We could add OAUTH2 feature to the service.
- Enable HTTPS

In addition, we could define a better error reporting pattern and give more meaningful messages for all kind of errors (currently just a part of the errors are treated).


