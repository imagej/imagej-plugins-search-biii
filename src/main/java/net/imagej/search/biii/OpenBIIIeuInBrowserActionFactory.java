/*-
 * #%L
 * Search plugins to enable BIII search in SciJava.
 * %%
 * Copyright (C) 2020 - 2023 ImageJ developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.search.biii;

import org.scijava.Priority;
import org.scijava.log.LogService;
import org.scijava.platform.PlatformService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.search.DefaultSearchAction;
import org.scijava.search.SearchAction;
import org.scijava.search.SearchActionFactory;
import org.scijava.search.SearchResult;

import java.io.IOException;
import java.net.URL;

/**
 * This factory creates actions for opening web search results in a browser.
 *
 * @author Robert Haase (MPI-CBG)
 */
@Plugin(type = SearchActionFactory.class, priority = Priority.LOW)
public class OpenBIIIeuInBrowserActionFactory implements SearchActionFactory {

	@Parameter
	private PlatformService platformService;

	@Parameter
	private LogService log;

	@Override
	public boolean supports(final SearchResult result) {
		return result instanceof BIIIeuSearchResult;
	}

	@Override
	public SearchAction create(final SearchResult result) {
		return new DefaultSearchAction("Visit BioImage Informatics Index (BIII) website", () -> {
			try {
				platformService.open(new URL("https://biii.eu"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
