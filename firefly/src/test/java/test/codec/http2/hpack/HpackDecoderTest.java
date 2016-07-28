package test.codec.http2.hpack;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.junit.Test;

import com.firefly.codec.http2.hpack.HpackDecoder;
import com.firefly.codec.http2.model.HttpField;
import com.firefly.codec.http2.model.HttpHeader;
import com.firefly.codec.http2.model.HttpScheme;
import com.firefly.codec.http2.model.MetaData;
import com.firefly.utils.lang.TypeUtils;

public class HpackDecoderTest {

	@Test
	public void testDecodeD_3() {
		HpackDecoder decoder = new HpackDecoder(4096, 8192);

		// First request
		String encoded = "828684410f7777772e6578616d706c652e636f6d";
		ByteBuffer buffer = ByteBuffer.wrap(TypeUtils.fromHexString(encoded));

		MetaData.Request request = (MetaData.Request) decoder.decode(buffer);

		assertEquals("GET", request.getMethod());
		assertEquals(HttpScheme.HTTP.asString(), request.getURI().getScheme());
		assertEquals("/", request.getURI().getPath());
		assertEquals("www.example.com", request.getURI().getHost());
		assertFalse(request.iterator().hasNext());

		// Second request
		encoded = "828684be58086e6f2d6361636865";
		buffer = ByteBuffer.wrap(TypeUtils.fromHexString(encoded));

		request = (MetaData.Request) decoder.decode(buffer);

		assertEquals("GET", request.getMethod());
		assertEquals(HttpScheme.HTTP.asString(), request.getURI().getScheme());
		assertEquals("/", request.getURI().getPath());
		assertEquals("www.example.com", request.getURI().getHost());
		Iterator<HttpField> iterator = request.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(new HttpField("cache-control", "no-cache"), iterator.next());
		assertFalse(iterator.hasNext());

		// Third request
		encoded = "828785bf400a637573746f6d2d6b65790c637573746f6d2d76616c7565";
		buffer = ByteBuffer.wrap(TypeUtils.fromHexString(encoded));

		request = (MetaData.Request) decoder.decode(buffer);

		assertEquals("GET", request.getMethod());
		assertEquals(HttpScheme.HTTPS.asString(), request.getURI().getScheme());
		assertEquals("/index.html", request.getURI().getPath());
		assertEquals("www.example.com", request.getURI().getHost());
		iterator = request.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(new HttpField("custom-key", "custom-value"), iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDecodeD_4() {
		HpackDecoder decoder = new HpackDecoder(4096, 8192);

		// First request
		String encoded = "828684418cf1e3c2e5f23a6ba0ab90f4ff";
		ByteBuffer buffer = ByteBuffer.wrap(TypeUtils.fromHexString(encoded));

		MetaData.Request request = (MetaData.Request) decoder.decode(buffer);

		assertEquals("GET", request.getMethod());
		assertEquals(HttpScheme.HTTP.asString(), request.getURI().getScheme());
		assertEquals("/", request.getURI().getPath());
		assertEquals("www.example.com", request.getURI().getHost());
		assertFalse(request.iterator().hasNext());

		// Second request
		encoded = "828684be5886a8eb10649cbf";
		buffer = ByteBuffer.wrap(TypeUtils.fromHexString(encoded));

		request = (MetaData.Request) decoder.decode(buffer);

		assertEquals("GET", request.getMethod());
		assertEquals(HttpScheme.HTTP.asString(), request.getURI().getScheme());
		assertEquals("/", request.getURI().getPath());
		assertEquals("www.example.com", request.getURI().getHost());
		Iterator<HttpField> iterator = request.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(new HttpField("cache-control", "no-cache"), iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDecodeWithArrayOffset() {
		String value = "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";

		HpackDecoder decoder = new HpackDecoder(4096, 8192);
		String encoded = "8682418cF1E3C2E5F23a6bA0Ab90F4Ff841f0822426173696320515778685a475270626a70766347567549484e6c633246745a513d3d";
		byte[] bytes = TypeUtils.fromHexString(encoded);
		byte[] array = new byte[bytes.length + 1];
		System.arraycopy(bytes, 0, array, 1, bytes.length);
		ByteBuffer buffer = ByteBuffer.wrap(array, 1, bytes.length).slice();

		MetaData.Request request = (MetaData.Request) decoder.decode(buffer);

		assertEquals("GET", request.getMethod());
		assertEquals(HttpScheme.HTTP.asString(), request.getURI().getScheme());
		assertEquals("/", request.getURI().getPath());
		assertEquals("www.example.com", request.getURI().getHost());
		assertEquals(1, request.getFields().size());
		HttpField field = request.iterator().next();
		assertEquals(HttpHeader.AUTHORIZATION, field.getHeader());
		assertEquals(value, field.getValue());
	}

	@Test
	public void testDecodeHuffmanWithArrayOffset() {
		HpackDecoder decoder = new HpackDecoder(4096, 8192);

		String encoded = "8286418cf1e3c2e5f23a6ba0ab90f4ff84";
		byte[] bytes = TypeUtils.fromHexString(encoded);
		byte[] array = new byte[bytes.length + 1];
		System.arraycopy(bytes, 0, array, 1, bytes.length);
		ByteBuffer buffer = ByteBuffer.wrap(array, 1, bytes.length).slice();

		MetaData.Request request = (MetaData.Request) decoder.decode(buffer);

		assertEquals("GET", request.getMethod());
		assertEquals(HttpScheme.HTTP.asString(), request.getURI().getScheme());
		assertEquals("/", request.getURI().getPath());
		assertEquals("www.example.com", request.getURI().getHost());
		assertFalse(request.iterator().hasNext());
	}

	@Test
	public void testNghttpx() {
		// Response encoded by nghttpx
		String encoded = "886196C361Be940b6a65B6850400B8A00571972e080a62D1Bf5f87497cA589D34d1f9a0f0d0234327690Aa69D29aFcA954D3A5358980Ae112e0f7c880aE152A9A74a6bF3";
		ByteBuffer buffer = ByteBuffer.wrap(TypeUtils.fromHexString(encoded));

		HpackDecoder decoder = new HpackDecoder(4096, 8192);
		MetaData.Response response = (MetaData.Response) decoder.decode(buffer);

		assertThat(response.getStatus(), is(200));
		assertThat(response.getFields().size(), is(6));
		assertTrue(response.getFields().contains(new HttpField(HttpHeader.DATE, "Fri, 15 Jul 2016 02:36:20 GMT")));
		assertTrue(response.getFields().contains(new HttpField(HttpHeader.CONTENT_TYPE, "text/html")));
		assertTrue(response.getFields().contains(new HttpField(HttpHeader.CONTENT_ENCODING, "")));
		assertTrue(response.getFields().contains(new HttpField(HttpHeader.CONTENT_LENGTH, "42")));
		assertTrue(response.getFields().contains(new HttpField(HttpHeader.SERVER, "nghttpx nghttp2/1.12.0")));
		assertTrue(response.getFields().contains(new HttpField(HttpHeader.VIA, "1.1 nghttpx")));
	}

}
