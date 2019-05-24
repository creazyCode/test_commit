package com.imall.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

	private static CloseableHttpClient httpClient;
	private static CloseableHttpClient httpsClient;
	
	private final static Object syncLock1 = new Object();
	private final static Object syncLock2 = new Object();
	/**
	 * HttpClient连接SSL
	 */
	public static void ssl() {
		CloseableHttpClient httpClient = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			FileInputStream instream = new FileInputStream(new File(
					"d:\\tomcat.keystore"));
			try {
				// 加载keyStore d:\\tomcat.keystore
				trustStore.load(instream, "123456".toCharArray());
			} catch (CertificateException e) {
				e.printStackTrace();
			} finally {
				try {
					instream.close();
				} catch (Exception ignore) {
				}
			}
			// 相信自己的CA和所有自签名的证书
			SSLContext sslcontext = SSLContexts
					.custom()
					.loadTrustMaterial(trustStore,
							new TrustSelfSignedStrategy()).build();
			// 只允许使用TLSv1协议
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "TLSv1" },
					null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
					.build();
			// 创建http请求(get方式)
			HttpGet httpGet = new HttpGet(
					"https://localhost:8443/myDemo/Ajax/serivceJ.action");
			LOGGER.debug("executing request" + httpGet.getRequestLine());
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				if(response.getStatusLine().getStatusCode() != 200 
						&& response.getStatusLine().getStatusCode() != 206
						&& response.getStatusLine().getStatusCode() != 301 
						&& response.getStatusLine().getStatusCode() != 302){
					LOGGER.error("https {} error, status line {}", 
							new Object[]{httpGet.getURI(), response.getStatusLine().toString()});
				}
				LOGGER.debug("----------------------------------------");
				LOGGER.debug(response.getStatusLine().toString());
				if (entity != null) {
					LOGGER.debug("Response content length: "
							+ entity.getContentLength());
					LOGGER.debug(EntityUtils.toString(entity));
					EntityUtils.consume(entity);
				}
			} finally {
				httpGet.releaseConnection();
				response.close();
//				httpClient.close();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
//				try {
//					httpClient.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
		}
	}

	/**
	 * post方式提交表单（模拟用户登录请求）
	 */
	public static void postForm() {
		// 创建默认的httpClient实例.
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httpPost = new HttpPost(
				"http://localhost:8080/myDemo/Ajax/serivceJ.action");
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("username", "admin"));
		formparams.add(new BasicNameValuePair("password", "123456"));
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(uefEntity);
			LOGGER.debug("executing request " + httpPost.getURI());
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					LOGGER.debug("--------------------------------------");
					LOGGER.debug("Response content: "
							+ EntityUtils.toString(entity, "UTF-8"));
					LOGGER.debug("--------------------------------------");
				}
			} finally {
				httpPost.releaseConnection();
				response.close();
//				httpClient.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
//			try {
//				httpClient.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}

	private static RequestConfig createRequestConfig(int timeout, boolean redirectsEnabled){
//		String proxyHost = "192.168.1.188";
//		int proxyPort = 8888;
//		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
//		
//	    return RequestConfig.custom()
//	        .setSocketTimeout(timeout)
//	        .setConnectTimeout(timeout)
//	        .setConnectionRequestTimeout(timeout)
//	        .setRedirectsEnabled(redirectsEnabled)
//	        .setExpectContinueEnabled(true)
////	        .setProxy(proxy)
//	        .build();
		return createRequestConfig(timeout, redirectsEnabled, null, 0);
	}
	
	private static RequestConfig createRequestConfig(int timeout, boolean redirectsEnabled, String proxyHost, int proxyPort){
//		String proxyHost = "192.168.1.108";
//		int proxyPort = 8888;
//		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		HttpHost proxy = new HttpHost("127.0.0.1", 1080);
		RequestConfig rc = null;
		if(!StringUtils.isBlank(proxyHost) && proxyPort > 0){
			proxy = new HttpHost(proxyHost, proxyPort);
			rc = RequestConfig.custom()
			        .setSocketTimeout(timeout)
			        .setConnectTimeout(timeout)
			        .setConnectionRequestTimeout(timeout)
			        .setRedirectsEnabled(redirectsEnabled)
			        .setProxy(proxy)
			        .build();
		}else{
			rc = RequestConfig.custom()
			        .setSocketTimeout(timeout)
			        .setConnectTimeout(timeout)
			        .setConnectionRequestTimeout(timeout)
			        .setRedirectsEnabled(redirectsEnabled)
//			        .setProxy(proxy)
			        .build();
		}
		return rc;
	}
	
	private static RequestConfig getDefaultRequestConfig(){
		int timeout = 10000;
		boolean redirectsEnabled = false;
		return createRequestConfig(timeout, redirectsEnabled);
	}
	
	public static RequestConfig getProxyRequestConfig() {
		String proxyHost = "192.168.1.188";
		int proxyPort = 8888;
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();  
		return config;
	}
	
	public static CloseableHttpClient getHttpClient(){
		if(httpClient == null){
			synchronized (syncLock1) {
				if(httpClient == null){
					Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
			                .register("https", SSLConnectionSocketFactory.getSocketFactory())
			                .register("http", PlainConnectionSocketFactory.getSocketFactory())
			                .build();
					PoolingHttpClientConnectionManager pccm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
				    pccm.setMaxTotal(100); // 连接池最大并发连接数
				    pccm.setDefaultMaxPerRoute(30); // 单路由最大并发数
				    
					HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
						public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
							if (executionCount > 3) {
								return false;
							}
							if (exception instanceof NoHttpResponseException) {
								LOGGER.info("[NoHttpResponseException has retry request:" + context.toString() + "][executionCount:"
										+ executionCount + "]");
								return true;
							} else if (exception instanceof SocketException) {
								LOGGER.info("[SocketException has retry request:" + context.toString() + "][executionCount:"
										+ executionCount + "]");
								return true;
							}
							return false;
						}
					};
					httpClient = HttpClients.custom().setConnectionManager(pccm).
							setDefaultRequestConfig(getDefaultRequestConfig()).setRetryHandler(retryHandler).build();
				}
			}
		}
	    return httpClient;
	}
	
	/**
	 * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param httpEntity
	 * @param requestBody
	 */
	public static Object post(String url, Map<String, String> headers, 
			Map<String, String> params, HttpEntity httpEntity, 
			String requestBody) {
		Object object = null;
		// 创建默认的httpClient实例.
		CloseableHttpClient httpClient = null;
		if(url.trim().indexOf("https://") == 0){
			httpClient = getHttpsClient();
		}else{
			httpClient = getHttpClient();
		}
		// 创建httppost
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(getDefaultRequestConfig());
		if(headers != null){
			Set<String> keys = headers.keySet();
			for(String key : keys){
				String value = headers.get(key);
				if(key != null && value != null){
					httpPost.addHeader(key, value);
				}
			}
		}
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if(params != null){
			Set<String> keys = params.keySet();
			for(String key : keys){
				formparams.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		try {
			if(httpEntity != null){
				httpPost.setEntity(httpEntity);
			}else if(requestBody != null){
				httpPost.setEntity(new StringEntity(requestBody, "UTF-8"));
//				httpPost.setEntity(new ByteArrayEntity(requestBody.getBytes("UTF-8")));
			}else{
				UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
//				uefEntity = new UrlEncodedFormEntity(formparams, "GBK");
				httpPost.setEntity(uefEntity);
			}
			Object[] result = null;
			for(int i = 0; i < 2; i ++){
				if(result != null && result[0] != null && Boolean.TRUE.equals(result[0])){
					break;
				}
				result = executePost(httpClient, httpPost, params);
				if(result != null){
					object = result[1];
				}
			}
		} catch (Exception e) {
			if(params != null){
				LOGGER.error("post {} error, params {}", url, params.toString());
			}else{
				LOGGER.error("post {} error", url);
			}
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
//			try {
				httpPost.releaseConnection();
//				httpClient.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		return object;
	}
	
	private static Object[] executePost(CloseableHttpClient httpClient, HttpPost httpPost, 
			Map<String, String> params){
		Object[] result = new Object[2];
		boolean success = false;
		Object object = null;
		try {
			LOGGER.debug("executing post request " + httpPost.getURI());
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					if(response.getStatusLine().getStatusCode() != 200 
							&& response.getStatusLine().getStatusCode() != 206 
							&& response.getStatusLine().getStatusCode() != 301 
							&& response.getStatusLine().getStatusCode() != 302){
						LOGGER.error("post {} error, params {}, status line {}", 
								new Object[]{httpPost.getURI(), params == null ? "" : params.toString(), response.getStatusLine().toString()});
					}
					LOGGER.debug("--------------------------------------");
					LOGGER.debug(httpPost.getURI().toString());
					LOGGER.debug(response.getStatusLine().toString());
					object = EntityUtils.toString(entity, "UTF-8");
					LOGGER.debug("Response content: " + object);
//					LOGGER.debug("Response content: " + EntityUtils.toString(entity, "UTF-8"));
					LOGGER.debug("--------------------------------------");
				}
			} finally {
				httpPost.releaseConnection();
				response.close();
			}
			success = true;
		} catch (Exception e) {
			if(params != null){
				LOGGER.error("post {} error, params {}", httpPost.getURI().toString(), params.toString());
			}else{
				LOGGER.error("post {} error", httpPost.getURI().toString());
			}
			e.printStackTrace();
		}
		result[0] = success;
		result[1] = object;
		return result;
	}
	
	public static byte[] getBytesFromEntity(HttpEntity httpEntity){
		try {
			byte[] bArr = new byte[100000];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream content = httpEntity.getContent();
            for (int i = 0; i != -1; i = content.read(bArr)) {
                byteArrayOutputStream.write(bArr, 0, i);
            }
            return bArr;
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return null;
    }
	
	public static byte[] getBytesFromInputStream(InputStream inputStream){
		int i = 0;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[100000];
            while (i != -1) {
                byteArrayOutputStream.write(bArr, 0, i);
                i = inputStream.read(bArr);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 发送 get请求
	 * @param url
	 * @param headers
	 * @param params
	 */
	public static String get(String url, Map<String, String> headers, Map<String, String> params) {
		String content = null;
		CloseableHttpClient httpClient = null;
		if(url.trim().indexOf("https://") == 0){
			httpClient = getHttpsClient();
		}else{
			httpClient = getHttpClient();
		}
		HttpGet httpGet = null;
		try {
			if(params != null){
				Set<String> keys = params.keySet();
				if(url.contains("?")){
					url += "&";
				}else{
					url += "?";
				}
				for(String key : keys){
					String value = params.get(key);
					if(StringUtils.isBlank(value)){
						value = "";
					}
					url += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8") + "&";
				}
				url = url.substring(0, url.length() - 1);
			}
			httpGet = new HttpGet(url);
			httpGet.setConfig(getDefaultRequestConfig());
			if(headers != null){
				Set<String> keys = headers.keySet();
				for(String key : keys){
					httpGet.addHeader(key, headers.get(key));
				}
			}
			boolean success = false;
			for(int i = 0; i < 2; i ++){
				if(success){
					break;
				}
				try{
					content = executeGet(httpClient, httpGet);
					success = true;
				}catch (Exception e) {
					if(params != null){
						LOGGER.error("get {} error, params {}", url, params.toString());
					}else{
						LOGGER.error("get {} error", url);
					}
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			if(params != null){
				LOGGER.error("get {} error, params {}", url, params.toString());
			}else{
				LOGGER.error("get {} error", url);
			}
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
//			try {
				httpGet.releaseConnection();
//				httpClient.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		return content;
	}
	
	private static String executeGet(CloseableHttpClient httpClient, 
			HttpGet httpGet){
		String content = null;
		try {
			LOGGER.debug("executing get request " + httpGet.getURI());
			// 执行get请求.
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if(response.getStatusLine().getStatusCode() != 200 
						&& response.getStatusLine().getStatusCode() != 206
						&& response.getStatusLine().getStatusCode() != 301 
						&& response.getStatusLine().getStatusCode() != 302){
					LOGGER.error("get {} error, status line {}", 
							new Object[]{httpGet.getURI(), response.getStatusLine().toString()});
				}
				LOGGER.debug("--------------------------------------");
				LOGGER.debug(httpGet.getURI().toString());
				// 打印响应状态
				LOGGER.debug(response.getStatusLine().toString());
				if(response.getStatusLine().getStatusCode() == 301 
						|| response.getStatusLine().getStatusCode() == 302){
					Header[] locationHeaders = response.getHeaders("Location");
					if(locationHeaders.length > 0){
						content = locationHeaders[0].getValue();
					}
					if(!StringUtils.isBlank(content)){
						if(content.indexOf("http://") != 0 && content.indexOf("https://") != 0){
							String requestUrl = httpGet.getURI().toString();
							content = requestUrl.substring(0, requestUrl.lastIndexOf("/") + 1) + content;
						}
						LOGGER.debug("Redirect location: " + content);
					}
				}
				if (entity != null) {
					// 打印响应内容长度
					LOGGER.debug("Response content length: " + entity.getContentLength());
					String temp = EntityUtils.toString(entity);
					if(StringUtils.isBlank(content)){
						content = temp;
					}
					// 打印响应内容
					if(entity.getContentLength() < 1000){
						LOGGER.debug("Response content: " + temp);
					}
				}
				LOGGER.debug("------------------------------------");
			} finally {
				httpGet.releaseConnection();
				response.close();
//				httpClient.close();
			}
		} catch (Exception e) {
//			throw new RuntimeException(e);
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * 发送 get请求
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 */
	public static Header[] getResponseHeaders(String url, Map<String, String> headers, Map<String, String> params) {
		CloseableHttpClient httpClient = getHttpClient();
		try {
			if(params != null){
				Set<String> keys = params.keySet();
				if(url.contains("?")){
					url += "&";
				}else{
					url += "?";
				}
				for(String key : keys){
					String value = params.get(key);
					url += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8") + "&";
				}
				url = url.substring(0, url.length() - 1);
			}
			// 创建httpget.
			HttpGet httpGet = new HttpGet(url);
			httpGet.setConfig(getDefaultRequestConfig());
			if(headers != null){
				Set<String> keys = headers.keySet();
				for(String key : keys){
					httpGet.addHeader(key, headers.get(key));
				}
			}
			boolean success = false;
			for(int i = 0; i < 6; i ++){
				if(success){
					break;
				}
				try{
					try {
						LOGGER.debug("executing get request " + httpGet.getURI());
						// 执行get请求.
						CloseableHttpResponse response = httpClient.execute(httpGet);
						return response.getAllHeaders();
					} catch (Exception e) {
//						throw new RuntimeException(e);
						e.printStackTrace();
					}
				}catch (Exception e) {
					if(params != null){
						LOGGER.error("get {} error, params {}", url, params.toString());
					}else{
						LOGGER.error("get {} error", url);
					}
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			if(params != null){
				LOGGER.error("get {} error, params {}", url, params.toString());
			}else{
				LOGGER.error("get {} error", url);
			}
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
//			try {
//				httpClient.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		return null;
	}
	
	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
				KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}
		
		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
	
	public static CloseableHttpClient getHttpsClient() {
		if(httpsClient == null){
			synchronized (syncLock2) {
				if(httpsClient == null){
					try {
						KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//						File file = new File("/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/jre/lib/security/cacerts");
						File file = new File(ResourceUtils.getJavaCacertsPath());
						if(file != null && file.exists()){
							trustStore.load(new FileInputStream(file), null);
						}
//						trustStore.load(new FileInputStream(new File("/Users/jianxunji/Desktop/app.market.xiaomi.com.crt")), null);
						
						SSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
						socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
						
						Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().
								register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
						//创建ConnectionManager，添加Connection配置信息
						PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
						connectionManager.setMaxTotal(100); // 连接池最大并发连接数
						connectionManager.setDefaultMaxPerRoute(30); // 单路由最大并发数
						
						HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
							public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
								if (executionCount > 3) {
									return false;
								}
								if (exception instanceof NoHttpResponseException) {
									LOGGER.info("[NoHttpResponseException has retry request:" + context.toString() + "][executionCount:"
											+ executionCount + "]");
									return true;
								} else if (exception instanceof SocketException) {
									LOGGER.info("[SocketException has retry request:" + context.toString() + "][executionCount:"
											+ executionCount + "]");
									return true;
								}
								return false;
							}
						};
						
						httpsClient = HttpClients.custom().setConnectionManager(connectionManager).
								setDefaultRequestConfig(getDefaultRequestConfig()).setRetryHandler(retryHandler).build();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return httpsClient;
	}
	
	public static boolean testProxy(String host, int port){
		long startTime = System.currentTimeMillis();
		boolean success = false;
//		String url = "http://www.baidu.com";
		String url = "http://cdn.imalljoy.com/ping.htm";
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(createRequestConfig(6000, false, host, port));
		
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200 
					|| response.getStatusLine().getStatusCode() == 206
					|| response.getStatusLine().getStatusCode() == 301 
					|| response.getStatusLine().getStatusCode() == 302){
				success = true;
			}
		} catch (Exception e) {
			LOGGER.error("test proxy error", e);
		}
        if (!success){
            LOGGER.warn("test proxy: {}:{}, result: {}, consumed: {}, status code: {} from: {}",
                    host, port, success, System.currentTimeMillis() - startTime,
                    (response != null && response.getStatusLine() != null) ? response.getStatusLine().getStatusCode() : "", url);
        }
		return success;
	}

	/**
	 * 得到当前client的IP
	 *
	 * @param proxyHost
	 * @param proxyPort
	 * @return
	 */
	public static String getClientIp(String proxyHost, int proxyPort){
		String clientIp = null;
//		String url = "http://test.imalljoy.com/imall/ip";
		String url = "http://m.imalljoy.com/imall/ip";
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(createRequestConfig(6000, false, proxyHost, proxyPort));

		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            if(response.getStatusLine().getStatusCode() == 200
                    && !StringUtils.isBlank(content)){
                Map<String, Object> map = JsonUtils.fromJsonToMap(content);
                if (map.containsKey("data")){
                    clientIp = map.get("data").toString();
                }
            }
		} catch (Exception e) {
			LOGGER.error("get client ip error", e);
		}
		LOGGER.debug("get client IP: {}", clientIp);
		return clientIp;
	}
}