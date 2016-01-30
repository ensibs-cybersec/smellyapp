# SmellyApp

## Purpose ##

This sample application has been created to illustrate several programming mistakes that present security risks. It is intended to serve as a software base for searching security issues, evaluating them and correcting the corresponding code.

## Composition ##

SmellyApp is composed with three processes :

- **Initialization** : a Python / Flask application that sets up the MySQL database
- **persons-repository** : a Java / Spark service that implements a repository to store and retrieve persons-based data
- **webapp** : a Node.JS / Angular / Bootstrap Single Page Application that acts as an interface for creating and listing persons

SmellyApp also uses a MySQL database for persisence.

## Use ##

Easiest way to run the application is to use the docker-compose.yml file that is provided :

    git clone https://github.com/ensibs-cybersec/smellyapp.git
    cd smellyapp
    docker-compose build
    docker-compose up

One then needs to first initialize the database
> http://localhost:5000

The application is then available under 
> http://localhost:8080

Login is any name, and the password is `coucou`. The rest of the application is self-explanatory.

## Development ##

A `docker-compose-dev.yml` file is provided, where one can change the last line so that it reflects its one file structure. This way, the source is mapped on a Docker volume and changes to the GUI quicker to realize. One should `npm install` locally, though, in order for the Docker container to work fine.

The commands to run the application, in this configuration, are :

    docker-compose -f docker-compose-dev.yml build
    docker-compose -f docker-compose-dev.yml up