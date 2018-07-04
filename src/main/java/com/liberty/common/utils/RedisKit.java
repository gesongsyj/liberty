package com.liberty.common.utils;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.liberty.common.config.Global;

import redis.clients.jedis.Jedis;
import java.util.HashMap;

/**
 * Redis 工具类
 */
public class RedisKit {

  private static final Cache CACHE = Redis.use(Global.CACHE_NAME);

  // Note: a Redis connection cannot be shared between publishers and subscribers
  private static final Jedis PUBLISHER = CACHE.getJedis();
  private static final Jedis SUBSCRIBER = CACHE.getJedis();

  // 当前订阅会话Map
  public static HashMap<String, RedisMsgPubSubListener> sessionStatusMap = new HashMap<>();




}
