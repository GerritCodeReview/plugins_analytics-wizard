# Analytics Wizard plugin

Wizard to locally setup an Analytics Dashboard like the [Gerrit's one](https://gerrit-analytics.gerritforge.com).
This will allow you to explore the potentials of **DevOps Analytics** on your own repositories.

## What it DOES
* Setup a ready to use Analytics Dashboard with some defaults charts
* Populate the Dashboard with data coming from the repos you would like to analyse

## What it DOES NOT
* Schedule recurring import of the data, but just the first one
* Create a production ready environment. It is meant to build a *playground* to explore the potential of **DevOps
analytics**
* Create multiple dashboards

# Setup
This plugin requires:
* Gerrit v2.16
* [Analytics plugin](https://gerrit.googlesource.com/plugins/analytics/)

# How to use it
Once the plugin is installed and you are logged in Gerrit browse to this url: [https://<you_gerrit_url>/plugins/analytics-wizard/static/analytics-dashboard.html]()

You will land on this screen:

![alt text](./resources/wizard.png "Wizard screen")

Different parameters can be configured:
* **Dashboard name** (mandatory): name of the dashboard you are about to create
* **Projects prefix** (optional): prefix of the projects you want to import, i.e.: to import all the projects under the Gerrit namespace, you can specify `gerrit/`. *Note: It is not a regular expression.*
* **Aggregation type** (optional): the data can be aggregated by `email only`, by `email per hour`, by `email per day`, by `email per month` or by `email per year`.   
* **Date time-frame** (optional): time window you want to collect data about
* **Username/Password** (optional): credentials for Gerrit API, if basic auth is needed

Once you set the parameters pressing the "Create Dashboard" button will trigger the Dashboard creation and the data import.

*Beware this operation will take a while since it requires to download several Docker images and run an ETL job to collect and aggregate the data.*

At the end of this operation you will be presented with a dashbaord similar to this one:

![alt text](./resources/dashboard.png "Wizard screen")

You can now navigate among the different charts and uncover the potentials of DevOps analytics!

# Development

* [Project Git repo](https://gerrit.googlesource.com/plugins/analytics-wizard/)
* The project relies on:
  * an [ETL data extractor](https://gerrit.googlesource.com/apps/analytics-etl)
  * an [Analytics plugin](https://gerrit.googlesource.com/plugins/analytics/) to expose Gerrit metrics
* [CI/CD](https://gerrit-ci.gerritforge.com/job/plugin-analytics-wizard-sbt-master-master/)



