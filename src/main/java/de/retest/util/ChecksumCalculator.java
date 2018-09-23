package de.retest.util;

import java.nio.charset.Charset;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class ChecksumCalculator {

	public static final int LENGTH_OF_SHA256 = 64;

	private static ChecksumCalculator instance;

	public static ChecksumCalculator getInstance() {
		if ( instance == null ) {
			instance = new ChecksumCalculator();
		}
		return instance;
	}

	private final HashFunction sha256 = Hashing.sha256();
	private final HashFunction md5 = Hashing.md5();

	public String sha256( final String input ) {
		return sha256.hashString( input, Charset.defaultCharset() ).toString();
	}

	public String sha256( final byte[] input ) {
		return sha256.hashBytes( input ).toString();
	}

	/**
	 * @param input
	 *            string to hash
	 * @return hashed input string
	 * @deprecated use {@link ChecksumCalculator#sha256(String)}
	 */
	@Deprecated
	public String md5( final String input ) {
		return md5.hashString( input, Charset.defaultCharset() ).toString();
	}

	/**
	 * @param input
	 *            bytes to hash
	 * @return hashed input bytes
	 * @deprecated use {@link ChecksumCalculator#sha256(byte[])}
	 */
	@Deprecated
	public String md5( final byte[] input ) {
		return md5.hashBytes( input ).toString();
	}

}
