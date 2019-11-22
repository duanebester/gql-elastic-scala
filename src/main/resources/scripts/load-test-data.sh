#!/bin/sh

echo "\nDeleting existing test-users index..."
curl -H 'Content-Type: application/json' -XDELETE 'localhost:9200/test-users?pretty'
sleep 1
echo "\nCreating test-users index with mapping..."
curl -H 'Content-Type: application/json' -XPUT 'localhost:9200/test-users?pretty' --data-binary @geo-mapping.json
sleep 1
echo "\nAdding Users..."
curl -H 'Content-Type: application/json' -XPOST 'localhost:9200/test-users/_bulk?pretty' --data-binary @users.json

echo "\nDeleting existing test-coffee-shops index..."
curl -H 'Content-Type: application/json' -XDELETE 'localhost:9200/test-coffee-shops?pretty'
sleep 1
echo "\nCreating test-coffee-shops index with mapping..."
curl -H 'Content-Type: application/json' -XPUT 'localhost:9200/test-coffee-shops?pretty' --data-binary @geo-mapping.json
sleep 1
echo "\nAdding Coffee Shops..."
curl -H 'Content-Type: application/json' -XPOST 'localhost:9200/test-coffee-shops/_bulk?pretty' --data-binary @coffee-shops.json

echo "\n Done! 😊"
