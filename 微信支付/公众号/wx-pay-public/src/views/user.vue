<template>
  <div>
    <div class="userinfo">
      <img class="userinfo-avatar" :src="user.headImgUrl" alt />
      <div class="userinfo-nickname">{{user.nickname}}</div>
    </div>
    <button @click="addOrder">生成订单</button>

    <div class="items">
      <div class="item">
        <div>订单号</div>
        <div>金额</div>
        <div>状态</div>
        <div>操作</div>
      </div>
      <div class="item" v-for="order in orders" :key="order.orderId">
        <span>{{order.orderId}}</span>
        <span>{{order.price}}</span>
        <span>{{orderType[order.status]}}</span>
        <button style="margin:0" @click="payAndRefund(order)">{{operatingType[order.status]}}</button>
      </div>
    </div>
  </div>
</template>

<script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js"></script>
<script>
import axios from "axios";
import vConsole from "vconsole";
new vConsole();
export default {
  data() {
    return {
      code: "",
      user: {},
      orders: [],
      state: false,
      orderType: ["待支付", "已支付", "已退款"],
      operatingType: ["去支付", "去退款", "已退款"]
    };
  },
  created() {
    this.code = this.$route.query.code;
    if (this.code) {
      this.getOpenId(this.code);
    } else {
      this.auth();
    }
  },
  methods: {
    auth() {
      axios.get("/user/auth").then(res => {
        let url = res.data.data;
        url = url.replace("%3A", ":").replace(/%2F/g, "/");
        location.href = url;
      });
    },
    getOpenId() {
      axios.get("/user/login?code=" + this.code).then(res => {
        if (res.data.code == 0) {
          this.getUserInfo();
        }
      });
    },
    getUserInfo() {
      axios.get("/user/info").then(res => {
        if (res.data.code == 0) {
          this.user = res.data.data;
          this.orderList();
        }
      });
    },
    addOrder() {
      axios.post("/order").then(res => {
        if (res.data.code == 0) {
          this.orderList();
        }
      });
    },
    orderList() {
      axios.get("/order").then(res => {
        if (res.data.code == 0) {
          this.orders = res.data.data;
        }
      });
    },
    payAndRefund(order){
      if(order.status == 2) return;
      if(order.status == 0){
        this.wxpay(order.orderId);
      } else {
        this.refund(order.orderId);
      }
    },
    wxpay(orderId){
      axios.get('/wx/pay?orderId='+orderId).then(res => {
        console.info(res.data.data);
        this.checkPay(res.data.data,orderId)
      })
    },
    checkPay(data,orderId){
      console.info("payData", data);
      if (typeof WeixinJSBridge == "undefined") {
          console.info("WeixinJSBridge is undefined");
          if (document.addEventListener) {
              document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
          } else if (document.attachEvent) {
              document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
              document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
          }
        } else {
            this.pay(data,orderId);
        }
    },
    pay(data,orderId) {
      console.info("payData", data);
      WeixinJSBridge.invoke(
        'getBrandWCPayRequest', {
            "appId": "wx0f0a4bbe2fc2fc3a",     //公众号名称，由商户传入
            "timeStamp": data.timeStamp,         //时间戳，自1970年以来的秒数
            "nonceStr": data.nonceStr, //随机串
            "package": data.packageValue,
            "signType": data.signType,         //微信签名方式：
            "paySign": data.paySign //微信签名
        },
        (res) => {
            console.info("支付", res);
            if (res.err_msg == "get_brand_wcpay_request:ok") {
              this.state = false;
              const temp = setInterval(() =>{
                if(this.state){
                  console.info("成功");
                  this.orderList();
                  clearInterval(temp)
                }
                this.queryStatus(1,orderId);
              },1000);
              console.info("zhifu:",this.state);
              // 使用以上方式判断前端返回,微信团队郑重提示：
              // res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
            }
        });
    },
    refund(orderId){
      this.state = false;
      axios.get('/wx/refund?orderId='+orderId).then(res => {
        if(res.data.code == 0){
          console.info("tuikuan:",this.state);
          const temp = setInterval(() =>{
            if(this.state){
              console.info("成功");
              this.orderList();
              clearInterval(temp)
            }
            this.queryStatus(2,orderId);
          },1000);
        }
      })
    },
    queryStatus(status,orderId){
      axios.get('/order/status?status='+status+"&orderId="+orderId).then(res => {
        if(res.data.data == true) {
          this.state = true;
        }
      })
    }
  }
};
</script>

<style>
.userinfo {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.userinfo-avatar {
  width: 64px;
  height: 64px;
  margin: 10px;
  border-radius: 50%;
}

.userinfo-nickname {
  color: #aaa;
}

.items {
  display: flex;
  flex-direction: column;
  width: 100%;
}
.item {
  display: flex;
  justify-content: space-around;
  padding: 2px 0;
}
</style>
