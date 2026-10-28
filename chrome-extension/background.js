// 精明消费助手 - Background Service Worker
// 负责处理通知、后台任务等

// 监听安装事件
chrome.runtime.onInstalled.addListener(function() {
  console.log('精明消费助手已安装');
  
  // 创建通知权限请求
  if (Notification.permission !== 'granted') {
    Notification.requestPermission();
  }
});

// 监听来自popup或content script的消息
chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
  if (request.action === 'showNotification') {
    showNotification(request.title, request.message);
    sendResponse({ success: true });
  }
  return true;
});

// 显示浏览器通知
function showNotification(title, message) {
  if (Notification.permission === 'granted') {
    chrome.notifications.create({
      type: 'basic',
      iconUrl: 'icon128.png',
      title: title,
      message: message
    }, function(notificationId) {
      console.log('Notification created:', notificationId);
    });
  } else {
    console.log('Notification permission not granted');
  }
}

// 定期检查价格监控告警（简化处理，实际应由后端WebSocket推送）
setInterval(function() {
  // 这里可以添加定期检查逻辑
  console.log('Background service worker running...');
}, 60000); // 每分钟检查一次
