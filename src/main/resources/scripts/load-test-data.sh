#!/bin/sh
echo "\nDeleting existing test-users index..."
curl -H 'Content-Type: application/json' -XDELETE 'localhost:9200/test-users?pretty'
sleep 1
echo "\nCreating test-users index with mapping..."
curl -H 'Content-Type: application/json' -XPUT 'localhost:9200/test-users?pretty' --data-binary @users-mapping.json
sleep 1
echo "\nAdding users..."
curl -H 'Content-Type: application/json' -XPOST 'localhost:9200/test-users/_bulk?pretty' --data-binary @users.json
echo "\n Done! 😊"
