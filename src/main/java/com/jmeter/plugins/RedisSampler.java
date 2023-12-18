package com.jmeter.plugins;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.*;

/**
 * @author spancer
 */
public class RedisSampler extends AbstractSampler {

    private final static Logger logger = LoggerFactory.getLogger(RedisSampler.class);

    @Override
    public SampleResult sample(Entry entry) {
        String server = getPropertyAsString("server");
        logger.info("Redis Sampler Server: {}", server);
        String password = getPropertyAsString("password");
        logger.info("Redis Sampler Password: {}", password);
        String operation = getPropertyAsString("operation");
        logger.info("Redis Sampler Operation: {}", operation);
        String key = getPropertyAsString("key");
        logger.info("Redis Sampler Key: {}", key);
        String value = getPropertyAsString("value");
        logger.info("Redis Sampler Value: {}", value);
        String type = getPropertyAsString("type");
        logger.info("Redis Sampler Type: {}", type);
        String expired = getPropertyAsString("expired");
        logger.info("Redis Sampler Expired: {}", expired);
        SampleResult sampleResult = new SampleResult();
        sampleResult.setSampleLabel("Redis Sampler");
        JSONObject result = new JSONObject();
        try {
            sampleResult.sampleStart();
            try (JedisCluster jedisCluster = createJedisCluster(server, password)) {
                if ("SET".equals(operation)) {
                    sampleResult.setSamplerData(String.format("Key: %s\nValue: %s", key, value));
                    setTypeValue(jedisCluster, type, key, value, expired, result);
                } else {
                    sampleResult.setSamplerData(String.format("Key: %s", key));
                    getTypeValue(jedisCluster, type, key, result);
                }
            }
            sampleResult.setResponseOK();
        } catch (Exception e) {
            logger.error("Redis Sampler Running Error!", e);
            result.put("result", e.getMessage());
            sampleResult.setResponseCode("-1");
            sampleResult.setResponseMessage("Redis Sampler Running Error!");
            sampleResult.setSuccessful(false);
        } finally {
            result.put("code", sampleResult.getResponseCode());
            sampleResult.setResponseData(result.toJSONString(), "UTF-8");
            sampleResult.sampleEnd();
        }
        return sampleResult;
    }

    private JedisCluster createJedisCluster(String server, String password) {
        Set<HostAndPort> nodes = new HashSet<>();
        String[] servers = server.split(",");
        for (String item : servers) {
            String[] arr = item.trim().split(":");
            String host = arr[0];
            int port = 6379;
            if (arr.length > 1) {
                port = Integer.parseInt(arr[1]);
            }
            logger.info("Redis Sampler set Host and Port: [{}:{}]", host, port);
            nodes.add(new HostAndPort(host, port));
        }
        if (null == password || password.isEmpty()) {
            return new JedisCluster(nodes);
        }
        return new JedisCluster(nodes, 2000, 2000, 5, password, new GenericObjectPoolConfig());
    }

    private void setTypeValue(JedisCluster jedisCluster, String type, String key, String value, String expired, JSONObject result) {
        Object response = null;
        int seconds = Integer.parseInt(expired);
        switch (type) {
            case "String":
                response = jedisCluster.set(key, value);
                break;
            case "Hash":
                JSONObject jsonObject = JSONObject.parseObject(value);
                Map<String, String> hash = new HashMap<>();
                jsonObject.keySet().forEach(k -> hash.put(k, jsonObject.getString(k)));
                response = jedisCluster.hset(key, hash);
                break;
            case "List":
                for (String item : value.split(",")) {
                    response = jedisCluster.rpush(key, item.trim());
                }
                break;
            case "Set":
                for (String item : value.split(",")) {
                    response = jedisCluster.sadd(key, item.trim());
                }
                break;
        }
        result.put("result", response);
        if (!expired.isEmpty()) {
            Long expire = jedisCluster.expire(key, seconds);
            result.put("expire", expire);
        }
    }

    private void getTypeValue(JedisCluster jedisCluster, String type, String key, JSONObject result) {
        Object response = null;
        switch (type) {
            case "String":
                response = jedisCluster.get(key);
                break;
            case "Hash":
                response = jedisCluster.hgetAll(key);
                break;
            case "List":
                Long len = jedisCluster.llen(key);
                response = jedisCluster.lrange(key, 0, len);
                break;
            case "Set":
                response = jedisCluster.smembers(key);
                break;
        }
        result.put("result", response);
    }

    @Override
    public void removed() {
        super.removed();
    }
}
