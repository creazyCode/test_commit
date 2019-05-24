/**
 * 
 */
package com.imall.proxy;

import com.imall.common.utils.CommonConstants;
import com.imall.common.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jason
 *
 */
public class ProxyUtils {
    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ProxyUtils.class);

    //清理一些东西的时间间隔
    public final static long CLEAN_TIME_INTERVAL = 16 * CommonConstants.SECOND_M_S;

    /**
     * 必须使用代理的URL，优先级最高
     */
    private final static Set<String> USE_PROXY_URL = new HashSet<String>();
    /**
     * 优先级其次
     */
    private final static Set<String> DO_NOT_USE_PROXY_URL = new HashSet<String>();
    /**
     * 优先级最低
     */
    private final static Set<String> DO_NOT_USE_PROXY_SUFFIX = new HashSet<String>();

    private final static Set<String> PROXY_DROP_URL = new HashSet<String>();

    private final static String BAIDU_DOWNLOAD_APK_DOMAIN = "a.gdown.baidu.com";
    private final static Set<String> BAIDU_DOWNLOAD_IPS = new HashSet<String>();
    static {
        DO_NOT_USE_PROXY_URL.add("106.38.");
        BAIDU_DOWNLOAD_IPS.add("42.81.");
        BAIDU_DOWNLOAD_IPS.add("180.97.");
    }

	private final static Set<String> DO_NOT_USE_PROXY_CACHE_URL = new HashSet<String>();

	static{
		USE_PROXY_URL.add("nsclick.baidu.com");
		USE_PROXY_URL.add("hmma.baidu.com");

		USE_PROXY_URL.add("p.s.360.cn");
		USE_PROXY_URL.add("update.api.sj.360.cn");

        USE_PROXY_URL.add("android.rqd.qq.com");
        USE_PROXY_URL.add("yyb.str.mdt.qq.com");
        USE_PROXY_URL.add("yyb.eve.mdt.qq.com");
        USE_PROXY_URL.add("oth.str.mdt.qq.com");

        USE_PROXY_URL.add("gamecenter.vivo.com.cn");
        USE_PROXY_URL.add("stxz.appstore.vivo.com.cn");
        USE_PROXY_URL.add("stttbg.appstore.vivo.com.cn");
        USE_PROXY_URL.add("filter.appstore.vivo.com.cn");
        USE_PROXY_URL.add("stbg.appstore.vivo.com.cn");

		USE_PROXY_URL.add("w.api.pp.cn/api/resource.app.checkUpdateV1");
		USE_PROXY_URL.add("log.cs.pp.cn/api/log.upload");

		USE_PROXY_URL.add("applog.uc.cn/collect");

		DO_NOT_USE_PROXY_SUFFIX.add(".apk");
		DO_NOT_USE_PROXY_SUFFIX.add(".jpg");
		DO_NOT_USE_PROXY_SUFFIX.add(".jpeg");
		DO_NOT_USE_PROXY_SUFFIX.add(".png");
		DO_NOT_USE_PROXY_SUFFIX.add(".gif");
		DO_NOT_USE_PROXY_SUFFIX.add(".ico");
		DO_NOT_USE_PROXY_SUFFIX.add(".webp");
		DO_NOT_USE_PROXY_SUFFIX.add(".bmp");

		DO_NOT_USE_PROXY_URL.add("kaopu001.com");
		//https://appc.baidu.com/as?st=10a001&usertype=2&word=%E9%9F%B3%E4%B9%90&cen=cuid_cut_cua_uid&abi=armeabi-v7a&pkname=com.baidu.appsearch&province=qivtkjihetggRHi86iS3kliheug_MHf3odfqA&native_api=1&disp=NRD90M.G9350ZCU2BQI3&gms=true&from=1000561u&cct=qivtkjihetggRHi86iS3kliheug_MHf3odfqA&pu=ctv%401%2Ccuid%40lO2qil8a28jEaviKluH6fliOva_uuvuPliv5uYa9H86zuviJMLuLC%2Cosname%40baiduappsearch%2Ccua%40_a2IiyuC2ig-NE6lI5me6NI0-I_UCvhe3GNqA%2Ccfrom%401000561u%2Ccsrc%40app_box_txt%2Ccut%40pfs8ujux2i4jaXiDyavdCf41mjQm5BuGC&network=WF&operator=460010&tn=native&psize=5&country=CN&is_support_webp=true&cll=ga24N_aj2igFue8rlu-LNjue2iAHQqqqB&uid=lO2qil8a28jEaviKluH6fliOva_uuvuPliv5uYa9H8qYuHi3A&language=zh&apn=&platform_version_id=24&ver=16794028&&crid=1510651508558&native_api=1&pn=1&f=operatehotkey%401%40softw%40neww%40key%2B%E9%9F%B3%E4%B9%90%40cate%2B%E6%B8%B8%E6%88%8F%40pos%2B0&bannert=26%4027%4028%4029%4030%4032%4043&bdussid=luNFg4MDgwOWxpUC01ZHRnWDN6Y0xVTDV2NnVKU09XaXc4bDFOTkJUUVlMSk5aSUFBQUFBJCQAAAAAAAAAAAEAAAA8hYMFamlhbnh1bmppAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABifa1kYn2tZNE&ptl=hps

		DO_NOT_USE_PROXY_URL.add("qhimg.com");
		DO_NOT_USE_PROXY_URL.add("m.shouji.360tpcdn.com");

		DO_NOT_USE_PROXY_URL.add("serverfile.ac.uc.cn");
		DO_NOT_USE_PROXY_URL.add("android-artworks.25pp.com");
		DO_NOT_USE_PROXY_URL.add("android-screenimgs.25pp.com");
		DO_NOT_USE_PROXY_URL.add("android-apps.25pp.com");
		DO_NOT_USE_PROXY_URL.add("ucdl.25pp.com");
		DO_NOT_USE_PROXY_URL.add("static.1sapp.com");

        DO_NOT_USE_PROXY_URL.add("search.appstore.vivo.com.cn/port/packages");//vivo的search接口
        DO_NOT_USE_PROXY_URL.add("info.appstore.vivo.com.cn/port/package");//vivo的查看应用详情接口
        DO_NOT_USE_PROXY_URL.add("info.appstore.vivo.com.cn/app/rec");//vivo的查看应用详情中的推荐
        DO_NOT_USE_PROXY_URL.add("appstore.vivo.com.cn/appinfo/downloadApkFile");//vivo的下载链接，不使用代理
        DO_NOT_USE_PROXY_URL.add("img.wsdl.vivo.com.cn");
        DO_NOT_USE_PROXY_URL.add("apktxdl.vivo.com.cn");
        DO_NOT_USE_PROXY_URL.add("apklxdl.vivo.com.cn");
        DO_NOT_USE_PROXY_URL.add("apk.wsdl.vivo.com.cn");

		DO_NOT_USE_PROXY_URL.add("103.18.");

		DO_NOT_USE_PROXY_URL.add("101.");//新加的不使用代理的地址，应该是应用宝的search接口。
		DO_NOT_USE_PROXY_URL.add("dd.myapp.com");
		DO_NOT_USE_PROXY_URL.add("cms.gtimg.com");
		DO_NOT_USE_PROXY_URL.add("pp.myapp.com");
		DO_NOT_USE_PROXY_URL.add("i.gtimg.cn");

		//TODO 不行把华为的search、查看应用详情等接口也加到这
		//DO_NOT_USE_PROXY_URL.add("appdl.hicloud.com");
		DO_NOT_USE_PROXY_URL.add("appimg.hicloud.com");
		DO_NOT_USE_PROXY_URL.add("upfile1.hicloud.com");

		DO_NOT_USE_PROXY_URL.add("gss0.bdstatic.com");//https://gss0.bdstatic.com/6bMWfDe8BsgCpNKfpU_Y_D3/static/appsapi/plugins/pass.p4-1.0.3-1.0.3.png
		DO_NOT_USE_PROXY_URL.add("imgsrc.baidu.com");
		DO_NOT_USE_PROXY_URL.add("cdn00.baidu-img.cn");
		DO_NOT_USE_PROXY_URL.add("hiphotos.baidu.com");//https://hiphotos.baidu.com/fex/%70%69%63/item/a08b87d6277f9e2f5b0a98ba1430e924b899f341.jpg
		DO_NOT_USE_PROXY_URL.add("baidu-1img.cn");
		DO_NOT_USE_PROXY_URL.add("gdown.baidu.com");
        DO_NOT_USE_PROXY_URL.add(BAIDU_DOWNLOAD_APK_DOMAIN);//百度的搜索接口不使用代理
        DO_NOT_USE_PROXY_URL.addAll(BAIDU_DOWNLOAD_IPS);

        DO_NOT_USE_PROXY_URL.add("storedl3.nearme.com.cn");//oppo的下载接口因为是https，必须单独列出来，不然就使用代理了。

		PROXY_DROP_URL.add("google.com");
		PROXY_DROP_URL.add("googleapis.com");
		PROXY_DROP_URL.add(".apple.com");
		PROXY_DROP_URL.add(".icloud.com");
		PROXY_DROP_URL.add(".samsungcloud.com");
		PROXY_DROP_URL.add(".evernote.com");
        PROXY_DROP_URL.add("localhost");
        PROXY_DROP_URL.add("127.0.0.1");
        PROXY_DROP_URL.add("192.168.");
        PROXY_DROP_URL.addAll(ObjectUtils.getLocalIps());

		DO_NOT_USE_PROXY_CACHE_URL.add("nearme.com.cn");
		DO_NOT_USE_PROXY_CACHE_URL.add("m.shouji.360tpcdn.com");
		DO_NOT_USE_PROXY_CACHE_URL.add("dd.myapp.com");

		DO_NOT_USE_PROXY_CACHE_URL.add("gdown.baidu.com");
		DO_NOT_USE_PROXY_CACHE_URL.add(BAIDU_DOWNLOAD_APK_DOMAIN);
        DO_NOT_USE_PROXY_CACHE_URL.addAll(BAIDU_DOWNLOAD_IPS);

		DO_NOT_USE_PROXY_CACHE_URL.add("103.18.");

        DO_NOT_USE_PROXY_CACHE_URL.add("101.");

        DO_NOT_USE_PROXY_CACHE_URL.add("search.appstore.vivo.com.cn/port/packages");//vivo的search接口
        DO_NOT_USE_PROXY_CACHE_URL.add("info.appstore.vivo.com.cn/port/package");//vivo的查看应用详情接口
        DO_NOT_USE_PROXY_CACHE_URL.add("info.appstore.vivo.com.cn/app/rec");//vivo的查看应用详情中的推荐

		DO_NOT_USE_PROXY_CACHE_URL.add("appdl.hicloud.com");

		//DO_NOT_USE_PROXY_CACHE_URL.add("apktxdl.vivo.com.cn");
        //DO_NOT_USE_PROXY_CACHE_URL.add("apklxdl.vivo.com.cn");
        //DO_NOT_USE_PROXY_CACHE_URL.add("apk.wsdl.vivo.com.cn");
    }

	public static boolean shouldUseProxy(byte[] requestDataBytes){
		String requestLine = getRequestLine(requestDataBytes);
		return shouldUseProxy(requestLine);
	}

    public static boolean shouldUseProxy(String requestLine){
		boolean useProxy = true;
		if(requestLine != null){
			for(String suffix : USE_PROXY_URL){
				if(requestLine.toLowerCase().contains(suffix.toLowerCase())){
					return true;
				}
			}

			if(useProxy){
				for(String url : PROXY_DROP_URL){
					if(requestLine.toLowerCase().contains(url.toLowerCase())){
						useProxy = false;
						break;
					}
				}
			}

            if(useProxy){
                for(String url : DO_NOT_USE_PROXY_URL){
                    if(requestLine.toLowerCase().contains(url.toLowerCase())){
                        useProxy = false;
                        break;
                    }
                }
            }

			if(useProxy){
				for(String suffix : DO_NOT_USE_PROXY_SUFFIX){
					if(requestLine.toLowerCase().contains(suffix.toLowerCase())){
						useProxy = false;
						break;
					}
				}
			}


		}
		//TODO Test
//		useProxy = false;
//		if(useProxy){
//			LOGGER.debug("use proxy for: " + requestLine);
//		}else{
//			LOGGER.info("do not use proxy for: " + requestLine);
//		}
		return useProxy;
	}

	public static boolean shouldDrop(byte[] requestDataBytes){
		String request = getRequestLine(requestDataBytes);
		return shouldDrop(request);
	}

	public static boolean shouldDrop(String request){
		boolean shouldDrop = false;
		if(request != null && request.length() < 300){
			for(String url : PROXY_DROP_URL){
				if(request.toLowerCase().contains(url.toLowerCase())){
					shouldDrop = true;
					break;
				}
			}
		}
		if(shouldDrop){
			LOGGER.info("drop request for: " + request);
		}else{
			LOGGER.debug("do not drop request for: " + request);
		}
		return shouldDrop;
	}

	public static boolean shouldUseProxyCache(String url){
	    boolean should = false;
	    if(!url.contains("http")){
            url = "https://" + url;
        }
        boolean existed = false;
		for(String urlTemp : DO_NOT_USE_PROXY_CACHE_URL){
			if(url.toLowerCase().contains(urlTemp.toLowerCase())){
				existed = true;
				should = false;
				break;
			}
		}
		if(!existed){
			if(url.indexOf("https") != 0){
				if(ProxyConfig.PROXY_CACHE_USE && !shouldUseProxy(url)){
					URL urlObject = null;
					try {
						urlObject = new URL(url);
						if(!StringUtils.isBlank(urlObject.getPath()) && !"/".equals(urlObject.getPath().trim())){
							should = true;
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(!should){
			LOGGER.debug("cache: should not use cache for request url: {} ", url);
		}
	    return should;
	}

    /**
     * 处理一些地址，例如baidu的将某些IP的下载，替换为域名，不然下载不下来。
     *
     * @param uri
     * @return
     */
    public static String processUri(String uri){
        String processedUri = uri;
        uri = uri.toLowerCase();
        if(uri.contains(".apk") && !uri.contains("https") && !uri.contains(".myapp.")){
            for(String ip : BAIDU_DOWNLOAD_IPS){
                int index = uri.indexOf(ip);
                if(index >= 0){
                    String completeIp = uri.substring(index);
                    completeIp = completeIp.substring(0, completeIp.indexOf("/"));
                    processedUri = uri.replace(completeIp, BAIDU_DOWNLOAD_APK_DOMAIN);
                    LOGGER.info("processed uri: {} to: {}", uri, processedUri);
                    break;
                }
            }
        }
        return processedUri;
    }

	public static String getRequestLine(byte[] requestDataBytes){
		String requestData = null;
		try {
			requestData = new String(requestDataBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String request = "";
		if(requestData != null){
			String[] requestDataLines = requestData.split("\r\n");
			if(requestDataLines != null && requestDataLines.length > 0){
				request = requestDataLines[0];
			}
		}
		return request;
	}
	
	/**
	 * 从byte中读取可读的数据
	 * 
	 * @param data
	 */
	public static String getHumanString(byte[] data){
		String string = null;
		try {
			string = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		if(string != null){
			String[] lines = string.split("\r\n", -1);
			if(lines != null && lines.length > 0){
				int continuesEmptyLines = 0;
				for(int index = 0; index < lines.length; index ++){
					if (sb.length() > 200){
						break;
					}
					String line = lines[index];
					
					//有乱码的时候不打印
					boolean contains = false;
					for(int i = 0; i < line.length() && i < 20; i ++){
						if (!(line.charAt(i) >= 32 && line.charAt(i) <= 122)) {
							contains = true;
							break;
						}
					}
					if(contains){
						continue;
					}
					if("".equals(line)){
						continuesEmptyLines ++;
					}else{
						continuesEmptyLines = 0;
						sb.append(line);
						sb.append("\r\n");
					}
					if(continuesEmptyLines >= 1){
						break;
					}
				}
			}
		}
		return sb.toString();
	}

	public static void main(String[] args){
	    String url = "http://106.38.179.47/data/wisegame/783c5cb9b3facdb9/dianjingshijie_3.apk";
	    boolean shouldUseProxy = ProxyUtils.shouldUseProxy(url);
	    System.out.println(shouldUseProxy);

	    url = "http://180.97.34.211/data/wisegame/783c5cb9b3facdb9/dianjingshijie_3.apk";
        url = ProxyUtils.processUri(url);
        System.out.println(url);
    }

}
