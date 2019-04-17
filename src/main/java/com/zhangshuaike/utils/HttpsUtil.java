package com.zhangshuaike.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
/**
 * 请求的工具类
 * @author shuaike
 *
 */
public class HttpsUtil {
	/**
	 * post请求方法
	 */
	private static final String METHOD_POST = "POST";

	/**
	 * utf-8编码格式
	 */
	private static final String DEFAULT_CHARSET = "utf-8";

	/**
	 * doPost
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            编码
	 * @param ctype
	 *            类型
	 * @param connectTimeout
	 *            连接超时时间
	 * @param readTimeout
	 *            读取超时时间
	 * @return 结果
	 * @throws Exception
	 *             异常
	 */
	public static String doPost(String url, String params, String charset, String ctype, int connectTimeout,
			int readTimeout) throws Exception {
		charset = (charset == null || "".equals(charset)) ? DEFAULT_CHARSET : charset;
		byte[] content = {};
		if (params != null) {
			content = params.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	/**
	 * doPost
	 * 
	 * @param url
	 *            请求地址
	 * @param ctype
	 *            类型
	 * @param content
	 *            内容
	 * @param connectTimeout
	 *            连接超时时间
	 * @param readTimeout
	 *            读取超时时间
	 * @return 结果
	 * @throws Exception
	 *             异常
	 */
	public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
			throws Exception {
		HttpsURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
				SSLContext.setDefault(ctx);

				conn = getConnection(new URL(url), METHOD_POST, ctype);
				conn.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (Exception e) {
				// log.error("GET_CONNECTOIN_ERROR, URL = " + url, e);
				throw e;
			}
			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				// log.error("REQUEST_RESPONSE_ERROR, URL = " + url, e);
				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}

	/**
	 * 获取连接
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方法
	 * @param ctype
	 *            类型
	 * @return HttpsURLConnection
	 * @throws IOException
	 *             异常
	 */
	private static HttpsURLConnection getConnection(URL url, String method, String ctype) throws IOException {
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "stargate");
		conn.setRequestProperty("Content-Type", ctype);
		return conn;
	}

	/**
	 * getResponseAsString
	 * 
	 * @param conn
	 *            conn连接
	 * @return String
	 * @throws IOException
	 *             IOException
	 */
	protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		} else {
			String msg = getStreamAsString(es, charset);
			if (StringUtils.isEmpty(msg)) {
				throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
			} else {
				throw new IOException(msg);
			}
		}
	}

	/**
	 * getStreamAsString
	 * 
	 * @param stream
	 *            stream
	 * @param charset
	 *            charset
	 * @return String
	 * @throws IOException
	 *             IOException
	 */
	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * getResponseCharset
	 * 
	 * @param ctype
	 *            ctype
	 * @return String
	 */
	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}
		return charset;
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * doGet
	 * 
	 * @param url
	 *            请求地址
	 * @param keyValueParams
	 *            参数
	 * @param cypt
	 *            cypt
	 * @return String
	 * @throws Exception
	 *             Exception
	 */
	public static String doGet(String url, Map<String, String> keyValueParams, String cypt) throws Exception {
		String result = "";
		BufferedReader in = null;
		try {

			String urlStr = url + "?" + getParamStr(keyValueParams);
			// System.out.println("GET请求的URL为："+urlStr);
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new DefaultTrustManager() }, new java.security.SecureRandom());
			URL realUrl = new URL(urlStr);
			// 打开和URL之间的连接
			HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
			// 设置https相关属性
			connection.setSSLSocketFactory(sc.getSocketFactory());
			connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
			connection.setDoOutput(true);

			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("Content-type", cypt);
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			// System.out.println("获取的结果为："+result);
		} catch (Exception e) {
			// System.out.println("发送GET请求出现异常！" + e);
			// e.printStackTrace();
			throw e;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				// e2.printStackTrace();
				throw e2;
			}
		}
		return result;
	}

	/**
	 * 转化字符串参数
	 * 
	 * @param params
	 *            参数
	 * @return String
	 */
	public static String getParamStr(Map<String, String> params) {
		String paramStr = StringUtils.EMPTY;
		if (null == params || 0 == params.size()) {
			return paramStr;
		}
		// 获取参数列表组成参数字符串
		for (String key : params.keySet()) {
			paramStr += key + "=" + params.get(key) + "&";
		}
		// 去除最后一个"&"
		return paramStr.substring(0, paramStr.length() - 1);
	}

	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 * 
	 * @param url
	 *            url地址
	 * @return url请求参数部分
	 * @author lzf
	 */
	public static Map<String, String> getUrlParam(String url) {
		// 初始化返回
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isBlank(url)) {
			return params;
		}
		//
		String strUrlParam = truncateUrl(url);
		if (StringUtils.isBlank(strUrlParam)) {
			return params;
		}
		String[] arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = strSplit.split("[=]");
			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				params.put(arrSplitEqual[0], arrSplitEqual[1]);
			} else {
				if (!"".equals(arrSplitEqual[0])) {
					// 只有参数没有值，也加入
					params.put(arrSplitEqual[0], "");
				}
			}
		}
		return params;
	}

	/**
	 * 去掉url中的路径，留下请求参数部分
	 * 
	 * @param url
	 *            url地址
	 * @return url
	 * @author lzf
	 */
	private static String truncateUrl(String url) {
		String strAllParam = null;
		String[] arrSplit = null;
		url = url.trim();
		arrSplit = url.split("[?]");
		if (url.length() > 1) {
			if (arrSplit.length > 1) {
				for (int i = 1; i < arrSplit.length; i++) {
					strAllParam = arrSplit[i];
				}
			}
		}
		return strAllParam;
	}


 
 
    /**
     * HTTPS 的get 请求
     * @param url
     * @return
     */
    public static String get(String url) {
        StringBuffer bufferRes = null;
        try {
            TrustManager[] tm = { new MyX509TrustManager() };  
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");  
            sslContext.init(null, tm, new java.security.SecureRandom());  
            // 从上述SSLContext对象中得到SSLSocketFactory对象  
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            
            URL urlGet = new URL(url);
            HttpsURLConnection http = (HttpsURLConnection) urlGet.openConnection();
            // 连接超时
            http.setConnectTimeout(25000);
            // 读取超时 --服务器响应比较慢，增大时间
            http.setReadTimeout(25000);
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setSSLSocketFactory(ssf);
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            
            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null){
                bufferRes.append(valueString);
            }
            in.close();
            if (http != null) {
                // 关闭连接
                http.disconnect();
            }
            return bufferRes.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * get请求https
     * @param url
     * @param params
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        return get(initParams(url, params));
    }
    
    /**
     * HTTPS 的POST 请求
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, String params) {
        StringBuffer bufferRes = null;
        try {
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象  
            SSLSocketFactory ssf = sslContext.getSocketFactory();
 
 
            URL urlGet = new URL(url);
            HttpsURLConnection http = (HttpsURLConnection) urlGet.openConnection();
            // 连接超时
            http.setConnectTimeout(25000);
            // 读取超时 --服务器响应比较慢，增大时间
            http.setReadTimeout(25000);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setSSLSocketFactory(ssf);
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
 
 
            OutputStream out = http.getOutputStream();
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
 
 
            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null){
                bufferRes.append(valueString);
            }
            in.close();
            if (http != null) {
                // 关闭连接
                http.disconnect();
            }
            return bufferRes.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
 
    /**
     * 构造请求参数
     * @param url
     * @param params
     * @return
     */
    public static String initParams(String url, Map<String, String> params){
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        } else {
            sb.append("&");
        }
        boolean first = true;
        for (Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=");
            if (StringUtils.isNotEmpty(value)) {
                try {
                    sb.append(URLEncoder.encode(value, DEFAULT_CHARSET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}



