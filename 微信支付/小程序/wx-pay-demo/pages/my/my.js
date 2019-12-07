// pages/my.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    url: 'http://192.168.6.24:8080',
    user: null,
    state: false,
    orders: [],
    orderType: ["待支付", "已支付", "已退款"],
    operatingType: ["去支付", "去退款", "已退款"],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    //没有登录，直接授权
    this.getUserInfo();
    // console.info(this.data.user);
    //如果登录了，则去后台获取openid
    if (wx.getStorageInfoSync("mini-app:openid")) {
      wx.login({
        success: (res) => {
          wx.request({
            url: this.data.url + '/user?code=' + res.code,
            method: 'GET',
            success: (resp) => {
              wx.setStorageSync("mini-app:openid", resp.data.data)
            }
          })
        }
      })
    }
    this.orderList()
  },
  //获取用户信息
  getUserInfo(){
    wx.getUserInfo({
      success: (res) => {
        this.setData({
          user: res.userInfo
        })
      }
    })
  },
  //生成订单
  addOrder(){
    let openId = wx.getStorageSync("mini-app:openid");
    wx.request({
      url: this.data.url +'/order',
      method: "POST",
      data: openId,
      success: (res) =>{
        console.info(res);
        this.orderList();
      }
    })
  },
  //订单列表
  orderList(){
    let openId = wx.getStorageSync("mini-app:openid");
    wx.request({
      url: this.data.url + '/order?openId='+openId,
      method: "GET",
      success: (res) => {
        console.info(res);
        if(res.data.code == 0){
          this.setData({
            orders: res.data.data
          })
        }
      }
    })
  },
  //支付或退款
  payAndRefund(e){
    console.info(e);
    let order = e.currentTarget.dataset.data;
    if(order.status == 2) return;

    if(order.status == 0){
      this.pay(order);
    }else{
      this.refund(order);
    }
  },
  //支付
  pay(order){
    this.data.state = false;
    wx.request({
      url: this.data.url + '/wx/pay?orderId=' + order.orderId + "&openId=" + order.openId,
      method: 'GET',
      success: (res) => {
        console.info(res.data.data);
        let data = res.data.data
        wx.requestPayment({
          timeStamp: data.timeStamp,
          nonceStr: data.nonceStr,
          package: data.packageValue,
          signType: data.signType,
          paySign: data.paySign,
          success: (res) => {
            const temp = setInterval(() => {
              if (this.data.state) {
                this.orderList();
                console.info("支付成功", res);
                wx.showToast({
                  title: '支付成功',
                  icon: 'success'
                })
                clearInterval(temp)
              }
              this.queryStatus(1, order.orderId);
            }, 1000);
          },
          fail: (res) => {
            console.info("取消支付", res)
            wx.showToast({
              title: '取消支付',
              icon: 'warn'
            })
          },
          complete: (res) => {
            this.orderList()
          }
        })
      }
    })
  },
  //退款
  refund(order){
    this.data.state = false;
    wx.request({
      url: this.data.url + '/wx/refund?orderId=' + order.orderId,
      method: 'GET',
      success: (res) => {
        console.info("退款成功");
        wx.showLoading({
          title: '退款中',
        })
        const temp = setInterval(() => {
          if (this.data.state) {
            console.info("成功");
            wx.hideLoading();
            this.orderList();
            wx.showToast({
              title: '退款成功',
              icon: 'success'
            })
            clearInterval(temp)
          }
          this.queryStatus(2, order.orderId);
        }, 1000);
      },
      fail: (res) => {
        console.info("退款失败")
        wx.showToast({
          title: '退款失败',
          icon: 'cancel'
        })
      },
    })
  },
  //查询状态（轮询调用）
  queryStatus(status, orderId) {
    wx.request({
      url: this.data.url + '/order/status?status=' + status + "&orderId=" + orderId,
      method: 'GET',
      success: (res) =>{
        if(res.data.data == true){
          this.data.state = true;
        }
      }
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})