
# Viewer performance tests

## Description

Mavenized scala project to stress Viewer using Gatling (http://gatling.io/).

## How it works

One maven profile has been created to run Viewer performance tests. (_-PVIEWER_)

### Manual Populatation

Manual configuration should be performed before running simulation:

- Create Crossdata datasource
- Create Crossdata dataview
- Create a new page with a TABLE widget using Crossdata dataview
- Get pageWidget association and edit [associationId.csv](https://github.com/Stratio/paas-stability/blob/branch/src/test/resources/data/viewer/associationId.csv) so _PWID_ is the same of the new TABLE widget.

This file will be used as the feeder for the rest of the scripts to run the performance tests.

IMPORTANT: By default Viewer configuration only get 250 entries in the getData action. It is recommended to upgrade this value in settings/configuration menu.

### Environment variables

These are the environment variables that can be used to run simulation:

- **users**     = define the number of users to perform simulation steps.
- **injectD**   = defines the user injection ramp.
- **runD**      = defines running duration.
- **URL**       = defines where Viewer instance is running.
- **PORT**      = defines the port of Viewer instance.
- **PROTOCOL**  = http or https. **http by default**.

### Run performance test

To run Viewer performance test against Crossdata DataSource defined within the associationId.csv feeder you should run the following command:

```sh
$ mvn test -PVIEWER -Dusers=1 -DinjectD=1 -DrunD=1 -DURL=127.0.0.1 -DPORT=9000 -DPROTOCOL=http
```
