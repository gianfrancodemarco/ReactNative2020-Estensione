echo 'Running SQL script ...'
mysql --verbose --host=localhost --user=root --password=root < DBSetup.sql
echo 'Starting server...'
start cmd /K java -jar mapServer.jar
echo 'Server Started!'