package com.zhy.demo.util;

import com.alibaba.fastjson.JSONObject;
import com.zhy.demo.dto.connector.CreateDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhy
 */
public class ConnectorParamsUtil {

    /**
     * mysqlSource
     *
     * @param createDTO 变量
     * @return List<JSONObject>
     */
    public static List<JSONObject> getMysqlSourceData(CreateDTO createDTO) {
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "io.debezium.connector.mysql.MySqlConnector");
            config.put("snapshot.delay.ms", "30000");
            //config.put("snapshot.select.statement.overrides", "inspector.inspector_work_order_tags,inspector.work_order");
            //config.put("snapshot.select.statement.overrides.inspector.inspector_work_order_tags", "select * from inspector.inspector_work_order_tags where tag_id=2 and update_time>'2020-11-05'");
            //config.put("snapshot.select.statement.overrides.inspector.work_order", "select * from inspector.work_order where update_time>'2020-11-05'");
            config.put("include.schema.changes", "false");
            config.put("database.zeroDateTimeBehavior", "convertToNull");
            config.put("database.jdbc.driver", "com.mysql.jdbc.Driver");
            config.put("database.history.skip.unparseable.ddl", "true");
            config.put("time.precision.mode", "connect");
            config.put("database.serverTimezone", "Asia/Shanghai");
            config.put("database.history.store.only.monitored.tables.ddl", "true");
            config.put("database.history.kafka.bootstrap.servers", "broker:29092");
            config.put("database.hostname", "172.17.0.1");
            config.put("database.port", "3306");
            config.put("database.user", "root");
            config.put("database.password", "12345");
            config.put("database.whitelist", createDTO.getDatabaseWhitelist());
            config.put("table.whitelist", createDTO.getTableWhitelist());
            config.put("database.history.kafka.topic", createDTO.getName() + ".db.history" + i);
            config.put("snapshot.mode", createDTO.getSnapshotMode());
            config.put("database.server.id", createDTO.getDatabaseServerId() + i);
            config.put("database.server.name", createDTO.getName() + "_" + i);
            config.put("name", createDTO.getName() + "_" + i);
            data.put("config", config);
            data.put("name", createDTO.getName() + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * httpSource
     *
     * @param createDTO 变量
     * @return List<JSONObject>
     */
    public static List<JSONObject> getHttpSourceData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.http.HttpSourceConnector");
            config.put("request.method", "POST");
            config.put("auth.type", "none");
            config.put("incremental.fields", "timestamp");
            config.put("http.api.url", "http://localhost:8880/http/send/data1");
            config.put("max.retries", "50");
            config.put("retry.backoff.ms", "2000");
            config.put("connect.timeout.ms", "10000");
            config.put("read.timeout.ms", "60000");
            config.put("batch.size", "100");
            config.put("kafka.topic", createDTO.getKafkaTopic() + "_" + i);
            config.put("poll.interval.ms", createDTO.getPollIntervalMs());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * messagebusSource
     *
     * @param createDTO 入参
     * @return List<JSONObject>
     */
    public static List<JSONObject> getMessagebusSourceData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.messagebus.MessagebusSourceConnector");
            config.put("dialect.type", "Rabbit");
            config.put("register.url", "http://message-bus-server.testing00.svc/messagebus/sigin/v2/");
            config.put("client.id", "1307971845391126528");
            config.put("client.secret", "MTMwNzk3MTg0Mjc0OD");
            config.put("tags", createDTO.getTags());
            config.put("kafka.topic", createDTO.getKafkaTopic() + "_" + i);
            config.put("prefetch.count", createDTO.getPrefetchCount());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * jdbcSink
     *
     * @param createDTO 入参
     * @return List<JSONObject>
     */
    public static List<JSONObject> getJdbcSinkData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "io.confluent.connect.jdbc.JdbcSinkConnector");
            config.put("connection.url", "jdbc:mysql://172.17.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull");
            config.put("connection.user", "root");
            config.put("connection.password", "12345");
            config.put("transforms", "unwrapfromenvelope1,valuetokeyconfig1,extractfieldconfig1,datetimeconverterconfig1");
            config.put("transforms.unwrapfromenvelope1.type", "io.debezium.transforms.UnwrapFromEnvelope");
            config.put("transforms.valuetokeyconfig1.type", "org.apache.kafka.connect.transforms.ValueToKey");
            config.put("transforms.valuetokeyconfig1.fields", "id");
            config.put("transforms.extractfieldconfig1.type", "org.apache.kafka.connect.transforms.ExtractField$Key");
            config.put("transforms.extractfieldconfig1.field", "id");
            config.put("transforms.datetimeconverterconfig1.type", "com.renrenche.kafka.connect.transform.DateTimeConverter$Value");
            config.put("transforms.datetimeconverterconfig1.target.type", "string");
            config.put("transforms.datetimeconverterconfig1.format", "yyyy-MM-dd HH:mm:ss");
            config.put("transforms.datetimeconverterconfig1.fields", "order_create_time,create_time,update_time");
            config.put("insert.mode", "upsert");
            config.put("dialect.name", "MySqlDatabaseDialect");
            config.put("pk.mode", "record_key");
            config.put("pk.fields", "id");
            config.put("table.name.format", createDTO.getTableNameFormat());
            config.put("topics", createDTO.getTopics());
            config.put("batch.size", createDTO.getBatchSize());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * httpSink
     *
     * @param createDTO 变量
     * @return List<JSONObject>
     */
    public static List<JSONObject> getHttpSinkData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.http.HttpSinkConnector");
            config.put("auth.type", "none");
            config.put("batch.enable", "true");
            config.put("http.api.url", "http://localhost:9081/test");
            config.put("request.method", "GET");
            config.put("max.retries", "50");
            config.put("retry.backoff.ms", "2000");
            config.put("read.timeout.ms", "60000");
            config.put("connect.timeout.ms", "10000");
            config.put("transforms", "logrecord");
            config.put("transforms.logrecord.type", "com.renrenche.kafka.connect.transform.LogRecord");
            config.put("transforms.logrecord.log.level", "warn");
            config.put("transforms.logrecord.log.name", "httpSink_tags");
            //config.put("topics", createDTO.getTopics());
            config.put("topics", "mysqlsource_930_3_" + i + ".source_test.message_500000");
            //config.put("topics", "jdbcAdaptive_928_6_" + i);
            config.put("batch.size", createDTO.getBatchSize());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * messagebusSink
     *
     * @param createDTO 变量
     * @return List<JSONObject>
     */
    public static List<JSONObject> getMessagebusSinkData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.messagebus.MessagebusSinkConnector");
            config.put("dialect.type", "Rabbit");
            config.put("register.url", "http://message-bus-server.testing00.svc/messagebus/sigin/v2/");
            config.put("client.id", "1307971388660781056");
            config.put("client.secret", "MTMwNzk3MTM5MTY5Mz");
            config.put("message.tag", "tag202009234");
            config.put("transforms", "logrecord");
            config.put("transforms.logrecord.type", "com.renrenche.kafka.connect.transform.LogRecord");
            config.put("transforms.logrecord.log.level", "warn");
            config.put("transforms.logrecord.log.name", "messagebuSink_tags");
            config.put("topics", createDTO.getTopics());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * jdbcAdaptive
     *
     * @param createDTO 变量
     * @return List<JSONObject>
     */
    public static List<JSONObject> getJdbcAdaptiveData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.adaptors.JdbcAdaptiveConnector");
            config.put("work.bootstrap.servers", "broker:29092");
            config.put("work.schema.registry.url", "http://10.1.1.4:8081");
            config.put("database.jdbc.driver", "com.mysql.jdbc.Driver");
            config.put("database.zeroDateTimeBehavior", "convertToNull");
            config.put("database.serverTimezone", "Asia/Shanghai");
            config.put("connection.url", "jdbc:mysql://172.17.0.1/test");
            config.put("connection.user", "root");
            config.put("connection.password", "12345");
            config.put("transforms", "unwrapfromenvelope1,replacefield");
            config.put("transforms.unwrapfromenvelope1.type", "io.debezium.transforms.UnwrapFromEnvelope");
            config.put("transforms.replacefield.type", "org.apache.kafka.connect.transforms.ReplaceField$Value");
            config.put("transforms.replacefield.whitelist", "id,shop_id");
            config.put("max.retries", "50");
            config.put("retry.backoff.ms", "2000");
            config.put("primary.entity.associated.key", "id");
            config.put("related.entity.associated.key", "id");
            config.put("primary.entity.id.key", "id");
            config.put("aggregate.type", "one_to_one");
            config.put("table", "cs_shop");
            config.put("kafka.topic", createDTO.getKafkaTopic() + "_" + i);
            config.put("topics", createDTO.getTopics());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * elasticSearchAdaptive
     *
     * @param createDTO 变量
     * @return List<JSONObject>
     */
    public static List<JSONObject> getElasticSearchAdaptiveData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.adaptors.ElasticSearchAdaptiveConnector");
            config.put("work.bootstrap.servers", "172.21.115.125:9092,172.21.115.126:9092,172.21.115.127:9092");
            config.put("work.schema.registry.url", "http://172.21.115.123:8081,http://172.21.115.124:8081");
            config.put("connection.url", "http://localhost:9084");
            config.put("transforms", "unwrapfromenvelope1,logrecord");
            config.put("transforms.unwrapfromenvelope1.type", "io.debezium.transforms.UnwrapFromEnvelope");
            config.put("transforms.logrecord.type", "com.renrenche.kafka.connect.transform.LogRecord");
            config.put("transforms.logrecord.log.level", "warn");
            config.put("transforms.logrecord.log.name", "elasticSearchAdaptive_tags");
            config.put("max.retries", "50");
            config.put("retry.backoff.ms", "2000");
            config.put("primary.entity.id.key", "id");
            config.put("primary.entity.associated.key", "id");
            config.put("related.entity.associated.key", "id");
            config.put("aggregate.type", "one_to_one");
            config.put("index", "zhy1");
            config.put("type", "zhy2");
            config.put("kafka.topic", createDTO.getKafkaTopic() + "_" + i);
            config.put("topics", createDTO.getTopics());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * httpAdaptive
     *
     * @param createDTO 变量
     * @return List<JSONObject>
     */
    public static List<JSONObject> getHttpAdaptiveData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.adaptors.HttpAdaptiveConnector");
            config.put("work.bootstrap.servers", "172.21.115.125:9092,172.21.115.126:9092,172.21.115.127:9092");
            config.put("work.schema.registry.url", "http://172.21.115.123:8081,http://172.21.115.124:8081");
            config.put("http.api.url", "http://localhost:9083/test");
            config.put("transforms", "unwrapfromenvelope1,logrecord");
            config.put("transforms.unwrapfromenvelope1.type", "io.debezium.transforms.UnwrapFromEnvelope");
            config.put("transforms.logrecord.type", "com.renrenche.kafka.connect.transform.LogRecord");
            config.put("transforms.logrecord.log.level", "warn");
            config.put("transforms.logrecord.log.name", "httpAdaptive_tags");
            config.put("max.retries", "50");
            config.put("retry.backoff.ms", "2000");
            config.put("primary.entity.associated.key", "id");
            config.put("related.entity.associated.key", "id");
            config.put("primary.entity.id.key", "id");
            config.put("aggregate.type", "one_to_one");
            config.put("kafka.topic", createDTO.getKafkaTopic() + "_" + i);
            config.put("topics", "mysqlsource_928_4_" + i + ".source_test.message_500000");
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }

    public static List<JSONObject> getRrcElasticSearchSinkData(CreateDTO createDTO) {
        String name = createDTO.getName();
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = createDTO.getStart(); i <= createDTO.getEnd(); i++) {
            JSONObject data = new JSONObject();
            JSONObject config = new JSONObject();
            config.put("connector.class", "com.renrenche.kafka.connect.elasticsearch.ElasticsearchSinkConnector");
            config.put("connection.url", "http://172.17.0.1:9200");
            config.put("connection.timeout.ms", "10000");
            config.put("read.timeout.ms", "30000");
            config.put("max.retries", "5");
            config.put("retry.backoff.ms", "10000");
            config.put("transforms", "logrecord,unwrapfromenvelope,datetimeconverterconfig2,valuetokey,extractfield");
            config.put("transforms.logrecord.type","com.renrenche.kafka.connect.transform.LogRecord");
            config.put("transforms.logrecord.log.level","warn");
            config.put("transforms.logrecord.log.name","cs_shop_tags");
            config.put("transforms.unwrapfromenvelope.type", "io.debezium.transforms.UnwrapFromEnvelope");
            config.put("transforms.datetimeconverterconfig2.target.type","string");
            config.put("transforms.datetimeconverterconfig2.format","yyyy-MM-dd HH:mm:ss");
            config.put("transforms.datetimeconverterconfig2.fields","create_time,update_time");
            config.put("transforms.datetimeconverterconfig2.type","com.renrenche.kafka.connect.transform.DateTimeConverter$Value");
            config.put("transforms.valuetokey.type", "org.apache.kafka.connect.transforms.ValueToKey");
            config.put("transforms.valuetokey.fields", "id");
            config.put("transforms.extractfield.type", "org.apache.kafka.connect.transforms.ExtractField$Key");
            config.put("transforms.extractfield.field", "id");
            //config.put("transforms.fieldnamemapping.type","org.apache.kafka.connect.transforms.ReplaceField$Value");
            //config.put("transforms.fieldnamemapping.blacklist","test2");
            config.put("topic.index.map", createDTO.getTopics() + ":zhy_index");
            config.put("type.name", "zhy_type");
            config.put("topics", createDTO.getTopics());
            config.put("batch.size", createDTO.getBatchSize());
            config.put("name", name + "_" + i);
            data.put("config", config);
            data.put("name", name + "_" + i);
            dataList.add(data);
        }
        return dataList;
    }
}