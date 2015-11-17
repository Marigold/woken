#!/bin/bash

id=$1

if [ -z "$id" ]; then
  id=$(date +'%s')
fi

./analytics-db/start-db.sh -p 5432

java -Dconfig.file=test-application.conf -jar ../target/scala-2.11/workflow-assembly-0.1.jar &

http -v --timeout 180 PUT localhost:8087/job \
         jobId="$id" \
         dockerImage="registry.federation.mip.hbp/mip_node/r-summary-stats:latest" \
         inputDb=ldsm \
         outputDb=analytics \
         nodes:='[]' \
         parameters:='{"PARAM_query":"select tissue1_volume from brain_feature order by tissue1_volume", "PARAM_colnames":"tissue1_volume"}'