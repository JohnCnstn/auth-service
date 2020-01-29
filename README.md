# Service Template
Service template for new microservices.

## Architecture

PostgreSql was chosen as the database. It's is an open source object-relational database system with over 30 years of 
active development that has earned it a strong reputation for reliability, feature robustness, and performance.
                                                                                  
Openapi was chosen for designing, building, documenting and consuming REST APIs.

Kotlin was chosen for configuring Gradle. 

CheckStyle, Jacoco and Pmd plugins were chosen for supporting quality and reliable code.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Project requires Java 11, Make, Docker.

### Installing

Instructions for Java: [AdoptOpenJDK installation](https://adoptopenjdk.net/installation.html?variant=openjdk11)

Instructions for Docker: [Docker installation](https://docs.docker.com/v17.12/install/)

Instructions for Make: [Install make on Windows](http://gnuwin32.sourceforge.net/packages/make.htm)

### Running

Project requires running PostgreSql database.

`docker-compose up`

Then the project should be started, with the following command:

`./gradlew bootRun` for MacOS or Linux. `gradlew bootRun` for Windows.

### Running the tests

`./gradlew test` for MacOS or Linux. `gradlew test` for Windows.

## Contributing

When adding new feature to project make sure code satisfies rules made with CheckStyle, Pmd plugins. 
Useful documentation:

For CheckStyle: [CheckStyle Rules](https://checkstyle.sourceforge.io/config_coding.html)

For Pmd: [Pmd Java Rules](https://pmd.github.io/pmd-6.20.0/pmd_rules_java_codestyle.html#shortvariable)

Also minimal test coverage must be 75%. For more information visit 
[Jacoco Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
