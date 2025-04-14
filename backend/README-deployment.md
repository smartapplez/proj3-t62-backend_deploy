*** DEPLOYMENT CONFIG INFO ***

To make this deployment ready, there is script for it!
You can execute the bash script (on Local MobaXterm terminal for windows users) and will make this repo deployment ready for you :D
The script is located at /backend/scripts/deployment-script.sh

If you are at repo root,
Just type in `sh ./backend/scripts/deployment-ready.sh`

What the script does:

Table of Contents:
- Change of Root directory
- Maven Java config
- Cross Origin Config

**CHANGE OF ROOT DIRECTORY (for Dave ofc)**
TODO: Change the root directory for deployment repo from "/" to "/backend" so that the build doesn't fail and I can directly copy + paste
from this repo to the deployment repo

**MAVEN-JAVA CONFIG**
Railway deployment server uses version Java 17.0!
The Copy and Paste of the config is already accounted for in the deployment ready script
The server config can be found at /backend/scripts/pom-deployment.txt

**CROSS-ORIGIN CONFIG**
What the script does:
For every resource file under /backend/src/main/java/com/lotreetea/backend/resource, the script replaces "localhost..." in @CrossOrigin() to with the correct address:
"https://csce331-project3-deploy-frontend.onrender.com"
