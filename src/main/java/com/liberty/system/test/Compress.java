package com.liberty.system.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoCompressor;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoInputStream;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.LzoOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.xerial.snappy.Snappy;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class Compress {
	public static void main(String[] args) throws IOException {
		String a = "abcdsadkadlsjgslj";
		System.out.println(a.getBytes().length);
		long start = System.currentTimeMillis();
		byte[] deflateCompress = deflateCompress(a.getBytes());
		long end = System.currentTimeMillis();
		System.out.println("deflate:" + deflateCompress.length+","+(end-start));
		
		start = System.currentTimeMillis();
		byte[] gzipCompress = gzipCompress(a.getBytes());
		end = System.currentTimeMillis();
		System.out.println("gzip:" + gzipCompress.length+","+(end-start));
		
		start = System.currentTimeMillis();
		byte[] bzipCompress = bzipCompress(a.getBytes());
		end = System.currentTimeMillis();
		System.out.println("bzip:" + bzipCompress.length+","+(end-start));
		
		start = System.currentTimeMillis();
		byte[] lzoCompress = lzoCompress(a.getBytes());
		end = System.currentTimeMillis();
		System.out.println("lzo:" + lzoCompress.length+","+(end-start));
		
		start = System.currentTimeMillis();
		byte[] lz4Compress = lz4Compress(a.getBytes());
		end = System.currentTimeMillis();
		System.out.println("lz4:"+lz4Compress.length+","+(end-start));
		
		start = System.currentTimeMillis();
		byte[] snappyCompress = snappyCompress(a.getBytes());
		end = System.currentTimeMillis();
		System.out.println("snappy:"+snappyCompress.length+","+(end-start));
	}

	public static byte[] snappyCompress(byte srcBytes[]) throws IOException {
		return Snappy.compress(srcBytes);
	}

	public static byte[] snappyUncompress(byte[] bytes) throws IOException {
		return Snappy.uncompress(bytes);
	}

	public static byte[] lz4Compress(byte srcBytes[]) throws IOException {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		LZ4Compressor compressor = factory.fastCompressor();
		LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(byteOutput, 2048, compressor);
		compressedOutput.write(srcBytes);
		compressedOutput.close();
		return byteOutput.toByteArray();
	}

	public static byte[] lz4Uncompress(byte[] bytes) throws IOException {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LZ4FastDecompressor decompresser = factory.fastDecompressor();
		LZ4BlockInputStream lzis = new LZ4BlockInputStream(new ByteArrayInputStream(bytes), decompresser);
		int count;
		byte[] buffer = new byte[2048];
		while ((count = lzis.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}
		lzis.close();
		return baos.toByteArray();
	}

	public static byte[] deflateCompress(byte input[]) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Deflater compressor = new Deflater(9);
		try {
			compressor.setInput(input);
			compressor.finish();
			final byte[] buf = new byte[2048];
			while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
			}
		} finally {
			compressor.end();
		}
		return bos.toByteArray();
	}

	public static byte[] deflateUncompress(byte[] input) throws DataFormatException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Inflater decompressor = new Inflater();
		try {
			decompressor.setInput(input);
			final byte[] buf = new byte[2048];
			while (!decompressor.finished()) {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			}
		} finally {
			decompressor.end();
		}
		return bos.toByteArray();
	}

	public static byte[] gzipCompress(byte srcBytes[]) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(srcBytes);
			gzip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static byte[] gzipUncompress(byte[] bytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			GZIPInputStream ungzip = new GZIPInputStream(in);
			byte[] buffer = new byte[2048];
			int n;
			while ((n = ungzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public static byte[] bzipCompress(byte srcBytes[]) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BZip2CompressorOutputStream bcos = new BZip2CompressorOutputStream(out);
		bcos.write(srcBytes);
		bcos.close();
		return out.toByteArray();
	}

	public static byte[] bzipUncompress(byte[] bytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			BZip2CompressorInputStream ungzip = new BZip2CompressorInputStream(in);
			byte[] buffer = new byte[2048];
			int n;
			while ((n = ungzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public static byte[] lzoCompress(byte srcBytes[]) throws IOException {
		LzoCompressor compressor = LzoLibrary.getInstance().newCompressor(LzoAlgorithm.LZO1X, null);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		LzoOutputStream cs = new LzoOutputStream(os, compressor);
		cs.write(srcBytes);
		cs.close();

		return os.toByteArray();
	}

	public static byte[] lzoUncompress(byte[] bytes) throws IOException {
		LzoDecompressor decompressor = LzoLibrary.getInstance().newDecompressor(LzoAlgorithm.LZO1X, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		LzoInputStream us = new LzoInputStream(is, decompressor);
		int count;
		byte[] buffer = new byte[2048];
		while ((count = us.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}
		return baos.toByteArray();
	}
}
