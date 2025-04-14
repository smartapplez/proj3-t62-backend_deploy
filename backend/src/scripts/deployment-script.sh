#!/bin/bash

# This script is used to make the resource files deployment ready
sed -i 's|@CrossOrigin *( *origins *= *"http://localhost:3000" *)|@CrossOrigin(origins = "https://csce331-project3-deploy-frontend.onrender.com")|' ../main/java/com/lotreetea/backend/resource/*.java
cp ./pom-deployment.txt ../../pom.xml