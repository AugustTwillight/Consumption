import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

let stompClient = null

export function connectWebSocket() {
  const userStore = useUserStore()
  if (!userStore.token || !userStore.userInfo?.id) {
    console.log('用户未登录或用户信息不完整，无法连接WebSocket')
    return null
  }

  // 创建 SockJS 连接
  const socket = new SockJS(`${import.meta.env.VITE_APP_BASE_API || '/api/v1'}/ws`)
  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onConnect: (frame) => {
      console.log('Connected to WebSocket: ' + frame)
      // 订阅用户专属消息队列
      stompClient.subscribe(`/user/${userStore.userInfo.id}/queue/messages`, (message) => {
        const msgData = JSON.parse(message.body)
        handleWebSocketMessage(msgData)
      })
    },
    onStompError: (frame) => {
      console.error('Broker reported error: ' + frame.headers['message'])
      console.error('Additional details: ' + frame.body)
    }
  })

  stompClient.activate()
  return stompClient
}

export function disconnectWebSocket() {
  if (stompClient !== null) {
    stompClient.deactivate()
    stompClient = null
  }
}

function handleWebSocketMessage(msgData) {
  const { type, message, timestamp } = msgData
  console.log('Received WebSocket message:', type, message)
  
  // 根据消息类型进行不同处理
  if (type === 'price_alert') {
    ElMessage.warning({ message: `【价格告警】${message}`, duration: 5000 })
  } else if (type === 'comment_notice') {
    ElMessage.info({ message: `【评论通知】${message}`, duration: 5000 })
  } else if (type === 'sync_change') {
    ElMessage.success({ message: `【同步变更】${message}`, duration: 3000 })
  } else {
    ElMessage.info({ message: `[${type}] ${message}`, duration: 3000 })
  }
}

export function getStompClient() {
  return stompClient
}
