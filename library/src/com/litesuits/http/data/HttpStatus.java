package com.litesuits.http.data;

/**
 * http status, contains status code and description.
 * be careful, we use java reflect to get Description in Chinese.
 * @author MaTianyu
 *         2014-1-30上午1:22:23
 */
public class HttpStatus {

	private int code;
	private String des;
	private String chiDes;

	public HttpStatus(int code, String des) {
		this.code = code;
		this.des = des;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return "code: " + code + ", " + des;
	}

	public boolean isSuccess() {
		return code < 300 || code == 600;
	}

	@Override
	public String toString() {
		return "HttpStatus [code=" + code + ", des=" + des + ", chiDes=" + chiDes + "]";
	}

	public String getDescriptionInChinese() {
		if (chiDes != null) return chiDes;
		// 注意，这里用反射，一定要检查变量名字前缀（）的正确性。
		String fieldName = "STATUS_" + code;
		Object obj = null;
		try {
			obj = HttpStatus.class.getDeclaredField(fieldName).get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chiDes = obj == null ? des : "code: " + code + ", " + obj;
	}
	/**
	 * 这个临时响应是用来通知客户端它的部分请求已经被服务器接收，且仍未被拒绝。客户端应当继续发送请求的剩余部分，
	 * 或者如果请求已经完成，忽略这个响应。服务器必须在请求完成后向客户端发送一个最终响应。
	 */
	public static final String STATUS_100 = "继续发送请求";
	/**
	 * 服务器已经理解了客户端的请求，并将通过Upgrade消息头通知客户端采用不同的协议来完成这个请求。
	 * 只有在切换新的协议更有好处的时候才应该采取类似措施。
	 */
	public static final String STATUS_101 = "需要切换协议";
	/**
	 * 由WebDAV（RFC 2518）扩展的状态码，代表处理将被继续执行。
	 */
	public static final String STATUS_102 = "正在处理中";
	/**
	 * 请求已成功，请求所希望的响应头或数据体将随此响应返回。
	 */
	public static final String STATUS_200 = "成功";
	/**
	 * 请求已经被实现，而且有一个新的资源已经依据请求的需要而建立，且其 URI 已经随Location 头信息返回。
	 * 假如需要的资源无法及时建立的话，应当返回 '202 Accepted'。
	 */
	public static final String STATUS_201 = "成功，请求已被处理（新资源已创建）";
	/**
	 * 服务器已接受请求，但尚未处理。正如它可能被拒绝一样，最终该请求可能会也可能不会被执行。
	 * 在异步操作的场合下，没有比发送这个状态码更方便的做法了。 　　
	 */
	public static final String STATUS_202 = "成功，请求已被接受，但尚未处理";
	/**
	 * 服务器已成功处理了请求，但返回的实体头部元信息不是在原始服务器上有效的确定集合，而是来自本地或者第三方的拷贝。
	 * 当前的信息可能是原始版本的子集或者超集。
	 */
	public static final String STATUS_203 = "成功，非有效信息";
	/**
	 * 服务器成功处理了请求，但不需要返回任何实体内容
	 */
	public static final String STATUS_204 = "成功，但无信息内容";
	/**
	 * 服务器成功处理了请求，且没有返回任何内容。但是与204响应不同，返回此状态码的响应要求请求者重置文档视图。
	 */
	public static final String STATUS_205 = "成功，无信息内容，但需重置旧内容";
	/**
	 * 服务器已经成功处理了部分 GET 请求。
	 */
	public static final String STATUS_206 = "成功，返回部分内容";
	/**
	 * 由WebDAV(RFC 2518)扩展的状态码，代表之后的消息体将是一个XML消息，并且可能依照之前子请求数量的不同，
	 * 包含一系列独立的响应代码。
	 */
	public static final String STATUS_207 = "成功，各部分处理均成功";

	/**
	 * 被请求的资源有一系列可供选择的回馈信息，每个都有自己特定的地址和浏览器驱动的商议信息。
	 */
	public static final String STATUS_300 = "重定向，多地址可选";
	/**
	 * 被请求的资源已永久移动到新位置，并且将来任何对此资源的引用都应该使用本响应返回的若干个 URI 之一。
	 */
	public static final String STATUS_301 = "永久性转移";
	/**
	 * 请求的资源现在临时从不同的 URI 响应请求。由于这样的重定向是临时的，客户端应当继续向原有地址发送以后的请求。
	 */
	public static final String STATUS_302 = "暂时性转移";
	/**
	 * 对应当前请求的响应可以在另一个 URI 上被找到，而且客户端应当采用 GET 的方式访问那个资源。
	 */
	public static final String STATUS_303 = "重定向，应该使用另外一个地址";
	/**
	 * 如果客户端发送了一个带条件的 GET 请求且该请求已被允许，而文档的内容（自上次访问以来或者根据请求的条件）并没有改变，
	 * 则服务器应当返回这个状态码。304响应禁止包含消息体，因此始终以消息头后的第一个空行结尾。
	 */
	public static final String STATUS_304 = "内容未改变";
	/**
	 * 被请求的资源必须通过指定的代理才能被访问。Location 域中将给出指定的代理所在的 URI 信息，
	 * 接收者需要重复发送一个单独的请求，通过这个代理才能访问相应资源。只有原始服务器才能建立305响应。
	 */
	public static final String STATUS_305 = "应该使用代理访问";
	/**
	 * 在最新版的规范中，306状态码已经不再被使用。
	 * @deprecated
	 */
	public static final String STATUS_306 = "306状态码已经不再被使用";
	/**
	 * 请求的资源现在临时从不同的URI 响应请求。由于这样的重定向是临时的，客户端应当继续向原有地址发送以后的请求。
	 */
	public static final String STATUS_307 = "暂时重定向";
	/**
	 * 1、语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。 　　2、请求参数有误。
	 */
	public static final String STATUS_400 = "请求错误（请检查语义和参数）";
	/**
	 * 当前请求需要用户验证。该响应必须包含一个适用于被请求资源的 WWW-Authenticate 信息头用以询问用户信息。
	 */
	public static final String STATUS_401 = "鉴权失败";
	/**
	 * 该状态码是为了将来可能的需求而预留的。
	 */
	public static final String STATUS_402 = "预留状态码";
	/**
	 * 服务器已经理解请求，但是拒绝执行它。与401响应不同的是，身份验证并不能提供任何帮助，而且这个请求也不应该被重复提交。
	 */
	public static final String STATUS_403 = "拒绝执行";
	/**
	 * 请求失败，请求所希望得到的资源未被在服务器上发现。没有信息能够告诉用户这个状况到底是暂时的还是永久的。
	 */
	public static final String STATUS_404 = "未发现内容";
	/**
	 * 请求行中指定的请求方法不能被用于请求相应的资源。该响应必须返回一个Allow 头信息用以表示出当前资源能够接受的请求方法的列表。
	 */
	public static final String STATUS_405 = "请求方法不被允许";
	/**
	 * 请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。
	 */
	public static final String STATUS_406 = "内容不符合条件";
	/**
	 * 与401响应类似，只不过客户端必须在代理服务器上进行身份验证。
	 */
	public static final String STATUS_407 = "代理服务器上鉴权失败";
	/**
	 * 请求超时。客户端没有在服务器预备等待的时间内完成一个请求的发送。
	 */
	public static final String STATUS_408 = "客户端请求已超时";
	/**
	 * 由于和被请求的资源的当前状态之间存在冲突，请求无法完成。
	 */
	public static final String STATUS_409 = "冲突";
	/**
	 * 被请求的资源在服务器上已经不再可用，而且没有任何已知的转发地址。这样的状况应当被认为是永久性的。
	 */
	public static final String STATUS_410 = "资源不再可用";
	/**
	 * 服务器拒绝在没有定义 Content-Length 头的情况下接受请求。
	 */
	public static final String STATUS_411 = "请求需要消息体长度";
	/**
	 * 服务器在验证在请求的头字段中给出先决条件时，没能满足其中的一个或多个。
	 */
	public static final String STATUS_412 = "请求不满足前提条件";
	/**
	 * 服务器拒绝处理当前请求，因为该请求提交的实体数据大小超过了服务器愿意或者能够处理的范围。
	 */
	public static final String STATUS_413 = "提交数据大小超过服务器接受范围";
	/**
	 * 请求的URI 长度超过了服务器能够解释的长度，因此服务器拒绝对该请求提供服务。
	 */
	public static final String STATUS_414 = "请求URI太长";
	/**
	 * 对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝。
	 */
	public static final String STATUS_415 = "请求提交的格式不被服务器支持";
	/**
	 * 如果请求中包含了 Range 请求头，并且 Range 中指定的任何数据范围不合法。
	 */
	public static final String STATUS_416 = "请求的数据区域不合法";
	/**
	 * 在请求头 Expect 中指定的预期内容无法被服务器满足，或者这个服务器是一个代理服务器，
	 * 它有明显的证据证明在当前路由的下一个节点上，Expect 的内容无法被满足。
	 */
	public static final String STATUS_417 = "请求Expect中的内容无法被满足";
	/**
	 * 从当前客户端所在的IP地址到服务器的连接数超过了服务器许可的最大范围。
	 */
	public static final String STATUS_421 = "客户端IP地址到服务器的连接数超过了最大范围";
	/**
	 * 请求格式正确，但是由于含有语义错误，无法响应。
	 */
	public static final String STATUS_422 = "请求格式正确，但语义错误";
	/**
	 * 当前资源被锁定
	 */
	public static final String STATUS_423 = "当前资源被锁定";
	/**
	 * 由于之前的某个请求发生的错误，导致当前请求失败，例如 PROPPATCH。
	 */
	public static final String STATUS_424 = "之前的请求发生错误";
	/**
	 * 由于之前的某个请求发生的错误，导致当前请求失败，例如 PROPPATCH。
	 */
	public static final String STATUS_425 = "之前的请求发生错误";
	/**
	 * 客户端应当切换到TLS/1.0。
	 */
	public static final String STATUS_426 = "客户端应当切换到TLS/1.0";
	/**
	 * 由微软扩展，代表请求应当在执行完适当的操作后进行重试。
	 */
	public static final String STATUS_449 = "请在执行完适当的操作后进行重试";
	/**
	 * 服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理。
	 */
	public static final String STATUS_500 = "服务器出错";
	/**
	 * 当服务器无法识别请求的方法，并且无法支持其对任何资源的请求。
	 */
	public static final String STATUS_501 = "方法未实现";
	/**
	 * 作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应。
	 */
	public static final String STATUS_502 = "错误网关";
	/**
	 * 由于临时的服务器维护或者过载，服务器当前无法处理请求。
	 */
	public static final String STATUS_503 = "服务当前不可用";
	/**
	 * 作为网关或者代理工作的服务器尝试执行请求时，未能及时从上游服务器（URI标识出的服务器，例如HTTP、FTP、LDAP）或者辅助服务器（例如DNS
	 * ）收到响应。
	 */
	public static final String STATUS_504 = "网关超时";
	/**
	 * 服务器不支持，或者拒绝支持在请求中使用的 HTTP 版本。
	 */
	public static final String STATUS_50 = "不支持的HTTP版本";
	/**
	 * 由《透明内容协商协议》（RFC 2295）扩展，代表服务器存在内部配置错误
	 */
	public static final String STATUS_506 = "服务器配置有误";
	/**
	 * 服务器无法存储完成请求所必须的内容。这个状况被认为是临时的。
	 */
	public static final String STATUS_507 = "服务器空间不足";
	/**
	 * 服务器达到带宽限制。这不是一个官方的状态码，但是仍被广泛使用。
	 */
	public static final String STATUS_509 = "服务器达到带宽限制(非官方状态码)";
	/**
	 * 获取资源所需要的策略并没有没满足。
	 */
	public static final String STATUS_510 = "获取资源的策略并没有满足";
	/**
	 * 源站没有返回响应头部，只返回实体内容
	 */
	public static final String STATUS_600 = "只有消息体，没有返回响应头";
}
