
# Stratio Crossdata performance tests

## Description

Mavenized scala project to stress Stratio Crossdata using Gatling (http://gatling.io/).

## How it works

One maven profile has been created to run Stratio Crossdata performance tests. (_-PCROSSDATA_)

### Manual Populatation

Manual configuration should be performed before running simulation:

- Get users, passwords and queries to execute in the simulation [crossdatafeeder.csv](https://github.com/Stratio/paas-stability/blob/branch/src/test/resources/data/crossdata/crossdatafeeder.csv).

This file will be used as the feeder for the rest of the scripts to run the performance tests.

### Environment variables

These are the environment variables that can be used to run simulation:

- **users**           = define the number of users to perform simulation steps.
- **injectD**         = defines the user injection ramp.
- **runD**            = defines running duration.
- **CROSSDATA_HOST**  = defines where Crossdata instance is running.
- **CROSSDATA_PORT**  = defines the http port of Crossdata instance.

### Run performance test

To run Crossdata performance test you should run the following command:

```sh
$ mvn test -PCROSSDATA -DCROSSDATA_HOST="172.17.0.2" -DCROSSDATA_PORT="13422"
```
