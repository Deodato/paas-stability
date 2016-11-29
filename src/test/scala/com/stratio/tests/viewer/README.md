
# Viewer performance tests

## Description

Mavenized scala project to stress Viewer using Gatling (http://gatling.io/).

## How it works

One maven profile has been created to run Viewer performance tests. (_-PVIEWER_)

### Manual Populatation

Manual configuration should be performed before running simulation:

1.- Create Crossdata datasource
2.- Create Crossdata dataview
3.- Create a new page with a TABLE widget using Crossdata dataview
4.- Get pageWidget association and edit _src/test/resources/data/viewer/associationId.csv_ ([associationId.csv](src/test/resources/data/viewer/associationId.csv)) so PWID is the same of the new TABLE widget.

This file will be used as the feeder for the rest of the scripts to run the performance tests.

### Environment variables

To set custom variables use as follows:

- users     = define the number of users to perform simulation steps.
- injectD   = defines the user injection ramp.
- runD      = defines running duration.
- URL       = defines where Viewer instance is running.

### Run performance test

To run Viewer performance test against Crossdata DataSource defined within the associationId.csv feeder you should run the following command:

```sh
$ mvn test -PVIEWER -Dusers=1 -DinjectD=1 -DrunD=1 -DURL=127.0.0.1
```
