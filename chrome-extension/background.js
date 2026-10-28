// 精明消费助手 - Background Service Worker (Manifest V3)
// 负责处理通知、后台任务等

// 监听安装事件
chrome.runtime.onInstalled.addListener(function() {
  console.log('精明消费助手已安装');
  
  // 请求通知权限
  if (Notification.permission !== 'granted') {
    chrome.notifications.create({
      type: 'basic',
      iconUrl: 'icon128.png',
      title: '精明消费助手已安装',
      message: '点击此处允许接收价格降价通知'
    }, function(notificationId) {
      console.log('Installation notification created:', notificationId);
    });
  }
});

// 监听来自popup或content script的消息
chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
  if (request.action === 'showNotification') {
    showNotification(request.title, request.message);
    sendResponse({ success: true });
  } else if (request.action === 'priceAlert') {
    // 处理价格告警通知
    showNotification('价格监控告警', `商品【${request.goodsName}】价格已降至¥${request.currentPrice}，低于目标价¥${request.targetPrice}！`);
    sendResponse({ success: true });
  }
  return true;
});

// 显示浏览器通知 (Manifest V3 兼容)
function showNotification(title, message) {
  chrome.notifications.create({
    type: 'basic',
    iconUrl: 'icon128.png',
    title: title,
    message: message
  }, function(notificationId) {
    if (chrome.runtime.lastError) {
      console.error('Notification creation failed:', chrome.runtime.lastError.message);
    } else {
      console.log('Notification created:', notificationId);
    }
  });
}

// Service Worker 保持活跃（Manifest V3 中 setInterval 可能在后台被休眠，重要任务应使用 chrome.alarms）
chrome.alarms.create('keepAlive', { periodInMinutes: 15 });
chrome.alarms.onAlarm.addListener(function(alarm) {
  if (alarm.name === 'keepAlive') {
    console.log('Background service worker keep-alive ping...');
  }
});
