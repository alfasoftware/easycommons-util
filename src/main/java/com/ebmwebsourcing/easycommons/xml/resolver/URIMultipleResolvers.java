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
package com.ebmwebsourcing.easycommons.xml.resolver;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

public class URIMultipleResolvers implements URIResolver {


	protected List<URIResolver> resolvers = new ArrayList<URIResolver>();	

	public URIMultipleResolvers() {
		this(new DefaultURIResolver(), new ClasspathURIResolver());
	}

	public URIMultipleResolvers(URIResolver... resolvers) {
		for(URIResolver resolver: resolvers) {
			this.resolvers.add(resolver);
		}
	}


	@Override
	public Source resolve(String href, String base) throws TransformerException {
		Source res = null;
		for(URIResolver resolver: this.resolvers) {
			try {
				synchronized(resolver) {
					res = resolver.resolve(href, base);
					if(res != null) {
						break;
					}
				}
			} catch(TransformerException e) {
				// do nothing
			}
		}


		return res;
	}

	public URIResolver[] getURIResolvers() {
		return this.resolvers.toArray(new URIResolver[this.resolvers.size()]);
	}
}
