package com.heran.launcher2.util;

import info.monitorenter.cpdetector.io.JChardetFacade;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class CharsetDetector {
	
	/**
	 * 检测当前文件的编码方式
	 */
	public static Charset detect(InputStream in) {
		JChardetFacade detector = JChardetFacade.getInstance();
		Charset charset = null;
		try {
			in.mark(100);
			charset = detector.detectCodepage(in, 100);
			in.reset();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return charset;
	}
	
}
