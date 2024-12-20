Transaction Manager Service

Provides APIs to store and retrieve transactions and perform currency convertion from USD to other currencies.

The service uses the API from https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange to get the exchange rate from USD to other currencies.

H2 database (in memory) is used by default, so no database installation is needed to execute this service.

When running with default configuration, the Swagger documentation can be accessed at URL http://localhost:8080/swagger-ui/index.html
