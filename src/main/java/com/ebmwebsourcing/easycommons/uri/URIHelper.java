/**
 * Copyright (c) 2010-2012 EBM WebSourcing, 2012-2023 Linagora
 * 
 * This program/library is free software: you can redistribute it and/or modify
 * it under the terms of the New BSD License (3-clause license).
 *
 * This program/library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the New BSD License (3-clause license)
 * for more details.
 *
 * You should have received a copy of the New BSD License (3-clause license)
 * along with this program/library; If not, see http://directory.fsf.org/wiki/License:BSD_3Clause/
 * for the New BSD License (3-clause license).
 */
package com.ebmwebsourcing.easycommons.uri;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URIHelper {

	public static URI filePathToUri(String path) {
		URI uri;
		try {
			path = path.replaceAll("\\\\", "/");
			uri = new URI(path);
		} catch (URISyntaxException e1) {
			uri = new File(path).toURI();
			uri.normalize();
		}
		return uri;
	}

	public static URI resolve(URI baseURI, String systemId)
			throws MalformedURLException, URISyntaxException {
		URI res = null;
		if (baseURI == null) {
			if (systemId == null) {
				return null;
			}
			return URI.create(systemId);
		}
		if ("jar".equals(baseURI.getScheme())) {
			res = new URL(baseURI.toURL(), systemId).toURI();
		} else if(baseURI.isAbsolute()){
			res = baseURI.resolve(systemId);
		}

		return res;
	}
}
