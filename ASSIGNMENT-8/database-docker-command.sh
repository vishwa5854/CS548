docker run -d \
--name cs548db \
--network cs548-network \
-p 5432:5432 \
-v path-to-host-data-dir:/var/lib/postgresql/data \
-e POSTGRES_PASSWORD=XXXXXX \
-e PGDATA=/var/lib/postgresql/data/pgdata \
-e DATABASE_PASSWORD=YYYYYY \
cs548/database

docker run -d --name clinic-domain --network cs548-network -p 5050:8080 -e DATABASE_PASSWORD=YYYYYY cs548/clinic-domain