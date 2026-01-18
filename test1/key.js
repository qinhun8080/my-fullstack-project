// [修改] 从 'crypto-js' 引入 HmacSHA1 和 Base64 编码
import HmacSHA1 from 'crypto-js/hmac-sha1';
import * as encBase64 from 'crypto-js/enc-base64';

// [修改] 使用 'export function' 以支持 ES Module 语法
export function createCommonToken(params) {
	
	// [修改] crypto-js 需要将 Base64 密钥解析为 WordArray
    const access_key = encBase64.parse(params.author_key);

    const version = params.version;
    let res = 'userid' + '/' + params.user_id;
	// 有效期一年
    const et = Math.ceil((Date.now() + 365 * 24 * 3600 * 1000) / 1000);   
    const method = 'sha1';

	// 待签名的字符串
    const key = et + "\n" + method + "\n" + res + "\n" + version;
    
	// [修改] 使用 HmacSHA1(key, secret)
    const hmac = HmacSHA1(key, access_key);
	
	// [修改] 将 Hmac 结果转换为 Base64 字符串
	let sign = encBase64.stringify(hmac);

	// URL编码
    res = encodeURIComponent(res);
    sign = encodeURIComponent(sign);
    const token = `version=${version}&res=${res}&et=${et}&method=${method}&sign=${sign}`;

    return token;
}