docker run -d --name clinic-domain --network cs548-network \
-p 5050:8080 -e DATABASE_PASSWORD=YYYYYY cs548/clinic-domain