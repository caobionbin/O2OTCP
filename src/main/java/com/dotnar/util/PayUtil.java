package com.dotnar.util;

import com.dotnar.bean.pay.PayJsRequest;
import com.dotnar.bean.pay.PayNativeReply;
import com.dotnar.bean.pay.PayNativeRequest;
import com.dotnar.bean.pay.PayPackage;
import com.dotnar.bean.paymch.MchPayApp;
import com.dotnar.bean.paymch.MchPayNativeReply;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PayUtil {


	/**
	 * 生成支付JS请求JSON
	 * @param payPackage
	 * @param appId
	 * @param paternerKey
	 * @param paySignkey	appkey
	 * @return
	 */
	public static String generatePayJsRequestJson(PayPackage payPackage,
				String appId,
				String paternerKey,
				String paySignkey) throws Exception {
		Map<String, String> mapP = MapUtil.objectToMap(payPackage);
		String package_ = SignatureUtil.generatePackage(mapP, paternerKey);
		PayJsRequest payJsRequest = new PayJsRequest();
		payJsRequest.setAppId(appId);
		payJsRequest.setNonceStr(UUID.randomUUID().toString());
		payJsRequest.setPackage_(package_);
		payJsRequest.setSignType("sha1");
		payJsRequest.setTimeStamp(System.currentTimeMillis() / 1000 + "");
		Map<String, String> mapS = MapUtil.objectToMap(payJsRequest,"signType","paySign");
		String paySign = SignatureUtil.generatePaySign(mapS, paySignkey);
		payJsRequest.setPaySign(paySign);
		return JsonUtil.toJSONString(payJsRequest);
	}



	/**
	 * 生成Native支付JS请求URL
	 * @param appid
	 * @param productid
	 * @param paySignkey
	 * @return
	 */
	public static String generatePayNativeRequestURL(
			String appid,
			String productid,
			String paySignkey) throws Exception {

		PayNativeRequest payNativeRequest = new PayNativeRequest();
		payNativeRequest.setAppid(appid);
		payNativeRequest.setNoncestr(UUID.randomUUID().toString());
		payNativeRequest.setProductid(productid);
		payNativeRequest.setTimestamp(System.currentTimeMillis() / 1000 + "");
		Map<String, String> mapS = MapUtil.objectToMap(payNativeRequest,"sign");
		String sign = SignatureUtil.generatePaySign(mapS, paySignkey);
		payNativeRequest.setSign(sign);

		Map<String, String> map = MapUtil.objectToMap(payNativeRequest);
		return "weixin://wxpay/bizpayurl?" + MapUtil.mapJoin(map, false, false);
	}

	/**
	 * 生成 native 支付回复XML
	 * @param payPackage
	 * @param appId
	 * @param retCode 0 正确
	 * @param retErrMsg
	 * @param paternerKey
	 * @return
	 */
	public static String generatePayNativeReplyXML(PayPackage payPackage,
			String appId,
			String retCode,
			String retErrMsg,
			String paternerKey) throws Exception {

		PayNativeReply payNativeReply = new PayNativeReply();
		payNativeReply.setAppid(appId);
		payNativeReply.setNoncestr(UUID.randomUUID().toString());
		payNativeReply.setRetcode(retCode);
		payNativeReply.setReterrmsg(retErrMsg);
		payNativeReply.setTimestamp(System.currentTimeMillis() + "");
		String package_ = SignatureUtil.generatePackage(MapUtil.objectToMap(payPackage),paternerKey);
		payNativeReply.setPackage_(package_);
		payNativeReply.setSignmethod("sha1");
		String appSignature = SignatureUtil.generatePackage(
				MapUtil.objectToMap(payNativeReply, "appsignature", "signmethod"),
				paternerKey);
		payNativeReply.setAppsignature(appSignature);

		return XMLConverUtil.convertToXML(payNativeReply);
	}









	//MCH -------------------------------------------------


	/**
	 * (MCH)生成支付JS请求对象
	 * @param prepay_id	预支付订单号
	 * @param appId
	 * @param key 商户支付密钥
	 * @return
	 */
	public static String generateMchPayJsRequestJson(String prepay_id,String appId,String key,String codeUrl) throws Exception {
		String package_ = "prepay_id="+prepay_id;
		PayJsRequest payJsRequest = new PayJsRequest();
		payJsRequest.setAppId(appId);
		payJsRequest.setNonceStr(UUID.randomUUID().toString().replace("-", ""));
		payJsRequest.setPackage_(package_);
		payJsRequest.setSignType("MD5");
		payJsRequest.setTimeStamp(System.currentTimeMillis()/1000+"");
		payJsRequest.setCodeUrl(codeUrl);//二维码
		//@fantycool 提交修正bug
		//Map<String, String> mapS = MapUtil.objectToMap(payJsRequest,"signType","paySign");
		Map<String, String> mapS = MapUtil.objectToMap(payJsRequest);
		String paySign = SignatureUtil.generateSign(mapS,key);
		payJsRequest.setPaySign(paySign);
		return JsonUtil.toJSONString(payJsRequest);
	}


	/**
	 * (MCH)生成Native支付请求URL
	 * @param appid
	 * @param mch_id
	 * @param productid
	 * @param key
	 * @return
	 */
	public static String generateMchPayNativeRequestURL(
			String appid,
			String mch_id,
			String productid,
			String key) throws Exception {

		PayNativeRequest payNativeRequest = new PayNativeRequest();
		payNativeRequest.setAppid(appid);
		payNativeRequest.setNoncestr(UUID.randomUUID().toString().replace("-", ""));
		payNativeRequest.setProductid(productid);
		payNativeRequest.setTimestamp(System.currentTimeMillis()/1000+"");
		Map<String, String> mapS = MapUtil.objectToMap(payNativeRequest,"sign");
		mapS.put("mch_id",mch_id);
		String sign = SignatureUtil.generatePaySign(mapS,key);
		payNativeRequest.setSign(sign);

		Map<String, String> map = MapUtil.objectToMap(payNativeRequest);
		return "weixin://wxpay/bizpayurl?" + MapUtil.mapJoin(map, false, false);
	}

	/**
	 * (MCH)生成 native 支付回复XML
	 * @param mchPayNativeReply
	 * @param key
	 * @return
	 */
	public static String generateMchPayNativeReplyXML(MchPayNativeReply mchPayNativeReply,String key) throws Exception {
		Map<String, String> map = MapUtil.objectToMap(mchPayNativeReply);
		String sign = SignatureUtil.generateSign(map,key);
		mchPayNativeReply.setSign(sign);
		return XMLConverUtil.convertToXML(mchPayNativeReply);
	}

	/**
	 * (MCH)生成支付APP请求数据
	 * @param prepay_id	预支付订单号
	 * @param appId
	 * @param partnerid 商户平台号
	 * @param key 商户支付密钥
	 * @return
	 */
	public static MchPayApp generateMchAppData(String prepay_id,String appId,String partnerid,String key) throws Exception {
		Map<String, String> wx_map = new LinkedHashMap<String, String>();
		wx_map.put("appid", appId);
		wx_map.put("partnerid", partnerid);
		wx_map.put("prepayid", prepay_id);
		wx_map.put("package", "Sign=WXPay");
		wx_map.put("noncestr", UUID.randomUUID().toString().replace("-", ""));
		wx_map.put("timestamp", System.currentTimeMillis()/1000+"");
		String sign = SignatureUtil.generateSign(wx_map,key);
		MchPayApp mchPayApp = new MchPayApp();
		mchPayApp.setAppid(appId);
		mchPayApp.setPartnerid(partnerid);
		mchPayApp.setPrepayid(prepay_id);
		mchPayApp.setPackage_(wx_map.get("package"));
		mchPayApp.setNoncestr(wx_map.get("noncestr"));
		mchPayApp.setTimestamp(wx_map.get("timestamp"));
		mchPayApp.setSign(sign);
		return mchPayApp;
	}

}