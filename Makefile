redeploy:
	mvn package && rm /root/neo4j/plugins/aridaeHomology-1.0.0.jar && mv ./target/aridaeHomology-1.0.0.jar /root/neo4j/plugins/ && rm -r ./target