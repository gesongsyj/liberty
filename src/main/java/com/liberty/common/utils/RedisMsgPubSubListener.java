package com.liberty.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

import javax.websocket.Session;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RedisMsgPubSubListener extends JedisPubSub implements Runnable {

  private BlockingQueue queue;
  private Session session;
  private volatile boolean isRunning = true;
  Logger logger = LoggerFactory.getLogger(RedisMsgPubSubListener.class);

  public RedisMsgPubSubListener(Session session) {
    this.queue = new ArrayBlockingQueue(10);
    this.session = session;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  @Override
  public void run() {
    logger.info("Session:" + session.getId() + "，启动推送线程:" + Thread.currentThread().getName());

    CountUtil sessionFailCount = new CountUtil(10); // 10*0.5s，即 5s的掉线时间
    try {
      while (isRunning) {
        // 客户端 在线状态 检测
        trySend("hello", sessionFailCount); // 发送 心跳检测
        if (sessionFailCount.isOffline()) {
          break;
        }

        // 事件消息推送
        Object data = queue.poll(500, TimeUnit.MILLISECONDS); // 0.5s 延迟
        if (data != null) {
          trySend(data.toString(), sessionFailCount);
          logger.info("Session:" + session.getId() + "，推送内容：" + data.toString());
        }
      }
    } catch (Exception e) {
      logger.info("Session:" + session.getId() + " 出错:" + e.getMessage());
    } finally {
      this.unsubscribe();
      RedisKit.sessionStatusMap.remove(Thread.currentThread().getName());
      Thread.currentThread().interrupt();
      logger.info("Session:" + session.getId() + " 已退出推送线程。");
    }
  }

  private void trySend(String context, CountUtil count) {
    try {
      session.getBasicRemote().sendText(context);
      count.setCount(0);
    } catch (IOException e) {
      logger.info("Session:" + session.getId() + " 出错:" + e.getMessage());
      count.plusCount();
    }
  }

	public void stop() {
    isRunning = false;
  }

  @Override
  public void unsubscribe() {
    super.unsubscribe();
  }

  @Override
  public void unsubscribe(String... channels) {
    super.unsubscribe(channels);
  }

  @Override
  public void subscribe(String... channels) {
    super.subscribe(channels);
  }

  @Override
  public void psubscribe(String... patterns) {
    super.psubscribe(patterns);
  }

  @Override
  public void punsubscribe() {
    super.punsubscribe();
  }

  @Override
  public void punsubscribe(String... patterns) {
    super.punsubscribe(patterns);
  }

  @Override
  public void onMessage(String channel, String message) {
    logger.info("channel:" + channel + " receives message :" + message);
    try {
      queue.put(message);
      logger.info("channel:" + channel + " Queue Size:" + queue.size() + "接收线程:" + Thread.currentThread().getName());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onPMessage(String pattern, String channel, String message) {

  }

  @Override
  public void onSubscribe(String channel, int subscribedChannels) {
    logger.info("channel:" + channel + " is been subscribed: " + subscribedChannels);
  }

  @Override
  public void onPUnsubscribe(String pattern, int subscribedChannels) {

  }

  @Override
  public void onPSubscribe(String pattern, int subscribedChannels) {

  }

  @Override
  public void onUnsubscribe(String channel, int subscribedChannels) {
    logger.info("channel:" + channel + " is been unsubscribed: " + subscribedChannels);
  }

}
