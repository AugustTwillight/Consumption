// 精明消费助手 - Content Script
// 负责在电商页面解析提取商品信息

(function() {
  'use strict';

  // 监听来自popup的消息
  chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
    if (request.action === 'getProductInfo') {
      const product = extractProductInfo();
      sendResponse({ success: true, product: product });
    } else if (request.action === 'addMonitor') {
      const product = extractProductInfo();
      if (product) {
        // 发送价格监控请求到后端
        sendMonitorRequest(product);
        sendResponse({ success: true, product: product });
      } else {
        sendResponse({ success: false, error: '无法获取商品信息' });
      }
    }
    return true;
  });

  // 提取商品信息
  function extractProductInfo() {
    const url = window.location.href;
    let platform = 'unknown';
    let name = '';
    let price = '0.00';

    // 识别平台
    if (url.includes('taobao.com') || url.includes('tmall.com')) {
      platform = 'taobao';
      // 淘宝/天猫商品名称和价格选择器
      name = document.querySelector('.tb-detail-hd h1')?.textContent?.trim() || 
             document.querySelector('.tb-detail-h5')?.textContent?.trim() || '';
      price = document.querySelector('.tb-price .price-current')?.textContent?.replace(/[^\d.]/g, '') || 
              document.querySelector('.price-current')?.textContent?.replace(/[^\d.]/g, '') || '0.00';
    } else if (url.includes('jd.com')) {
      platform = 'jd';
      // 京东商品名称和价格选择器
      name = document.querySelector('#name')?.textContent?.trim() || 
             document.querySelector('.sku-name')?.textContent?.trim() || '';
      price = document.querySelector('.p-price .price')?.textContent?.replace(/[^\d.]/g, '') || 
              document.querySelector('.price')?.textContent?.replace(/[^\d.]/g, '') || '0.00';
    } else if (url.includes('pinduoduo.com')) {
      platform = 'pdd';
      // 拼多多商品名称和价格选择器
      name = document.querySelector('.goods-name')?.textContent?.trim() || 
             document.querySelector('.goods-title')?.textContent?.trim() || '';
      price = document.querySelector('.price-num')?.textContent?.replace(/[^\d.]/g, '') || 
              document.querySelector('.price')?.textContent?.replace(/[^\d.]/g, '') || '0.00';
    }

    if (platform !== 'unknown' && name) {
      return {
        name: name,
        link: url,
        platform: platform,
        price: price
      };
    }

    return null;
  }

  // 发送监控请求到后端
  function sendMonitorRequest(product) {
    // 从chrome.storage获取token
    chrome.storage.local.get(['token'], function(result) {
      const token = result.token;
      if (!token) {
        console.log('未找到token，请先登录');
        return;
      }

      // 这里简化处理，实际应该调用后端API保存监控任务
      const apiUrl = 'http://localhost:8080/api/v1/monitor/task';
      
      fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify({
          goodsName: product.name,
          goodsLink: product.link,
          platform: product.platform,
          currentPrice: product.price,
          targetPrice: product.price // 默认目标价为当前价格
        })
      }).then(response => {
        if (response.ok) {
          console.log('监控任务创建成功');
        } else {
          console.log('监控任务创建失败');
        }
      }).catch(error => {
        console.log('监控请求异常:', error);
      });
    });
  }

  // 页面加载完成后执行
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
      console.log('精明消费助手 - 页面DOM加载完成');
    });
  } else {
    console.log('精明消费助手 - 页面已加载完成');
  }
})();
