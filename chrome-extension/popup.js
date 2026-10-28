document.addEventListener('DOMContentLoaded', function() {
  const productInfoEl = document.getElementById('productInfo');
  const monitorBtn = document.getElementById('monitorBtn');
  const calcBtn = document.getElementById('calcBtn');
  const statusMsg = document.getElementById('statusMsg');
  const openAppBtn = document.getElementById('openAppBtn');

  // 打开Web端应用
  openAppBtn.addEventListener('click', function(e) {
    e.preventDefault();
    chrome.tabs.create({ url: 'http://localhost' });
  });

  // 加入价格监控
  monitorBtn.addEventListener('click', function() {
    sendMessageToContent('addMonitor', {}, function(response) {
      if (response && response.success) {
        showStatus('已成功加入价格监控', false);
      } else {
        showStatus('加入监控失败，请重试', true);
      }
    });
  });

  // AI凑单计算
  calcBtn.addEventListener('click', function() {
    sendMessageToContent('getProductInfo', {}, function(response) {
      if (response && response.product) {
        showStatus('正在跳转到凑单计算页面...', false);
        // 这里可以跳转到Web端的凑单计算页面，携带商品参数
        const productData = encodeURIComponent(JSON.stringify(response.product));
        chrome.tabs.create({ url: `http://localhost/#/calc?product=${productData}` });
      } else {
        showStatus('获取商品信息失败', true);
      }
    });
  });

  // 从content script获取商品信息
  sendMessageToContent('getProductInfo', {}, function(response) {
    if (response && response.product) {
      const product = response.product;
      productInfoEl.innerHTML = `
        <p class="name">${product.name || '未知商品'}</p>
        <p class="price">¥${product.price || '0.00'}</p>
        <p class="platform">平台: ${product.platform || '未知'}</p>
      `;
    } else {
      productInfoEl.innerHTML = '<p>未在支持的页面中，请前往淘宝/京东/拼多多商品页</p>';
    }
  });

  // 发送消息到content script
  function sendMessageToContent(action, data, callback) {
    chrome.tabs.query({ active: true, currentWindow: true }, function(tabs) {
      if (tabs[0] && tabs[0].id) {
        chrome.tabs.sendMessage(tabs[0].id, { action, data }, function(response) {
          if (callback) callback(response);
        });
      }
    });
  }

  // 显示状态消息
  function showStatus(message, isError) {
    statusMsg.textContent = message;
    statusMsg.className = 'status' + (isError ? ' error' : '');
    setTimeout(() => {
      statusMsg.textContent = '';
      statusMsg.className = 'status';
    }, 3000);
  }
});
