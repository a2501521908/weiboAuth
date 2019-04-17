# weiboAuth

微博第三方登陆 java maven项目

项目使用servlet版本,可以自行升级成自己想要的。

需要用户自行修改的: 

1.config.properties 配置文件 修改成自己的回调地址,应用key等。

2.QueryWeiBo.java 28行向下的常量,均已标注好,同样修改为自己的即可。

建议:

建议把 QueryWeiBo.java 下的  getAccessToken()、getUserInfo() 封装为类




