#!/bin/bash
lein jar
cp -f target/*.jar deploy
