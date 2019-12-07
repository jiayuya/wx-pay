module.exports = {
    publicPath: "/",
    devServer: {
        disableHostCheck: true,
        port: 80,
        proxy: {
          "/": {
            target: "http://192.168.6.24:8080/",
            changeOrigin: true
          }
        }
    }
}