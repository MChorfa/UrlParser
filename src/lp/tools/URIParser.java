package lp.tools;

import static org.apache.commons.lang.StringUtils.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nowind_lee@qq.com
 * @version 0.5
 */
public class URIParser {

	private String host;

	private Integer port;

	private String scheme;

	// use LinkedHashMap to keep the order of items
	private LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();

	private String path;

	private String userInfo;

	private String fragment;

	private String charset;

	public URIParser(String uri) throws URISyntaxException {
		this(uri, "utf-8");
	}

	/**
	 * http://user:password@host:port/aaa/bbb;xxx=xxx?eee=fff&eee=ddd&eee= lll#ref
	 */
	public URIParser(String uri, String charset) throws URISyntaxException {
		checkNull(uri, "uri");
		if (charset != null && !Charset.isSupported(charset)) {
			throw new IllegalArgumentException("charset is not supported: " + charset);
		}

		URI u = new URI(uri);
		this.charset = charset;
		this.scheme = u.getScheme();
		this.userInfo = u.getUserInfo();
		this.host = u.getHost();
		this.port = u.getPort();
		if (this.port == -1) {
			this.port = null;
		}
		this.path = u.getPath();
		this.params = parseQueryString(substringAfter(uri, "?"));
		this.fragment = u.getFragment();
	}

	public void addParam(String name, String value) {
		addParams(name, Arrays.asList(encode(value)));
	}

	public void addParams(String name, List<String> values) {
		List<String> list = getOrCreate(params, name);
		for (String value : values) {
			list.add(encode(value));
		}
	}

	public void removeParams(String name) {
		if (name == null) {
			return;
		}
		this.params.remove(name);
	}

	public void updateParam(String name, String value) {
		updateParams(name, value);
	}

	public void updateParams(String name, String... values) {
		checkNull(name, "name");
		if (values.length == 0) {
			throw new IllegalArgumentException("values should not be empty");
		}
		List<String> list = getOrCreate(params, name);
		list.clear();
		for (String value : values) {
			list.add(encode(value));
		}
	}

	public List<String> getRawParams(String name) {
		checkNull(name, "name");
		return this.params.get(name);
	}

	public String getRawParam(String name) {
		List<String> params = getRawParams(name);
		return params == null ? null : params.get(0);
	}

	public String getParam(String name) throws UnsupportedEncodingException {
		String value = getRawParam(name);
		return value == null ? null : decode(value);
	}

	public List<String> getParams(String name) {
		List<String> list = getRawParams(name);
		if (list == null) {
			return null;
		}
		List<String> params = new ArrayList<String>();
		for (String value : params) {
			params.add(encode(value));
		}
		return params;
	}

	public Map<String, String> getSimple() {
		Map<String, String> map = new HashMap<String, String>();
		for (String name : this.params.keySet()) {
			String value = this.params.get(name).get(0);
			map.put(name, encode(value));
		}
		return map;
	}

	public String createQueryString() {
		if (this.params.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String name : this.params.keySet()) {
			List<String> values = this.params.get(name);
			for (String value : values) {
				if (sb.length() > 0) {
					sb.append("&");
				}
				sb.append(name).append("=").append(encode(value));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.scheme != null) {
			sb.append(this.scheme).append("://");
		}
		if (this.userInfo != null) {
			sb.append(this.userInfo).append("@");
		}
		if (this.host != null) {
			sb.append(host);
		}
		if (this.port != null) {
			sb.append(":").append(this.port);
		}
		sb.append(this.path);
		String query = createQueryString();
		if (query != null) {
			sb.append("?").append(query);
		}
		if (this.fragment != null) {
			sb.append("#").append(fragment);
		}

		return sb.toString();
	}

	private String decode(String value) {
		checkNull(value, "value to decode");
		try {
			return charset == null ? value : URLDecoder.decode(value, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String encode(String value) {
		checkNull(value, "value to encode");
		try {
			return charset == null ? value : URLEncoder.encode(value, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<String> getOrCreate(Map<String, List<String>> map, String name) {
		checkNull(name, "name");
		List<String> list = map.get(name);
		if (list == null) {
			list = new ArrayList<String>();
			map.put(name, list);
		}
		return list;
	}

	private static void checkNull(Object value, String fieldName) {
		if (value == null) {
			throw new IllegalArgumentException(fieldName + " should not be null");
		}
	}

	private static LinkedHashMap<String, List<String>> parseQueryString(String query) {
		LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
		if (isBlank(query)) {
			return params;
		}
		String[] items = query.split("&");
		for (String item : items) {
			String name = substringBefore(item, "=");
			String value = substringAfter(item, "=");
			List<String> values = getOrCreate(params, name);
			values.add(value);
		}
		return params;
	}

}
