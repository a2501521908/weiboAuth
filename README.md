# weiboAuth
微博第三方登陆 java maven项目

项目使用servlet版本,可以自行升级成自己想要的。

需要用户自行修改的: 

Constants.java 中的部分常量

建议:
 
建议把 QueryWeiBo.java 下的  getAccessToken()、getUserInfo() 封装为工具类
常量加载到容器的配置中,我这只是配置



