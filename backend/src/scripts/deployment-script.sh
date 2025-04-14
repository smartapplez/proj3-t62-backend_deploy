#!/bin/bash
# This script is used to make the resource files deployment ready
cd ../main/java/com/lotreetea/backend/resource
sed -i 's|@CrossOrigin *( *origins *= *"http://localhost:3000" *)|@CrossOrigin(origins = "https://csce331-project3-deploy-frontend.onrender.com")|' *.java
cp ./pom-deployment.txt ../../pom.xml