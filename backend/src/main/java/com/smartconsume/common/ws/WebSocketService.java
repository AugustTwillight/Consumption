package com.smartconsume.common.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket推送服务类
 */
@Slf4j
@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 向指定用户推送消息
     *
     * @param userId 用户ID
     * @param messageType 消息类型
     * @param message 消息内容
     */
    public void sendToUser(Long userId, String messageType, String message) {
        String destination = "/user/" + userId + "/queue/messages";
        messagingTemplate.convertAndSend(destination, new WebSocketMessage(messageType, message));
        log.info("WebSocket推送消息到用户[{}]: type={}, message={}", userId, messageType, message);
    }

    /**
     * 向所有在线用户广播消息
     *
     * @param messageType 消息类型
     * @param message 消息内容
     */
    public void broadcast(String messageType, String message) {
        String destination = "/topic/messages";
        messagingTemplate.convertAndSend(destination, new WebSocketMessage(messageType, message));
        log.info("WebSocket广播消息: type={}, message={}", messageType, message);
    }

    /**
     * WebSocket消息实体
     */
    public static class WebSocketMessage {
        private String type;
        private String message;
        private long timestamp;

        public WebSocketMessage(String type, String message) {
            this.type = type;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
