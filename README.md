# Starter gradle plugins for kotlin

## The aim

The aim of this project is to create a number of starter-level configurable plugins for kotlin developers not yet familiar with Gradle.

It is not a secret that Gradle is one of the most powerful yet complicated build tools. And learning its details is blocker for many kotlin beginners. It is easy to simplify the initial adoption by creating a number of simple plugins to cover basic cases. Yet creating those plugins requires knowledge of Gradle which new people are missing.

## The process

The process of creating plugins is community-drivven. Meaning that community will try to meet your needs.

In orderd to get a working (and published to [maven central](https://search.maven.org/)) plugin, you need to first create an [issue](https://github.com/mipt-npm/gradle-kotlin-start/issues) with the description of your problem. And then answer questions if they arise.

All plugins will follow [semantic versioning](https://semver.org/) convention and will have a suffix for kotlin version. Like this: `0.1.0-kotlin-1.6.10`.

## Features

All project features should by default feature publishing to a github rebository and to sonatype/maven-central if you get corresponding credentials.
