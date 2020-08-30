/*-
 * #%L
 * Search plugins to enable BIII search in SciJava.
 * %%
 * Copyright (C) 2020 ImageJ developers.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.search.SearchResult;
import org.scijava.search.Searcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * A searcher for the <a href="http://biii.eu/search">Bio-Imaging Search
 * Engine</a>.
 *
 * @author Robert Haase (MPI-CBG)
 */
@Plugin(type = Searcher.class, enabled = false)
public class BIIIeuSearcher implements Searcher {

	private static final String DEFAULT_ICON = "/icons/search/NEUBIASlogo.png";

	private final ArrayList<SearchResult> searchResults = new ArrayList<>();

	@Parameter
	private LogService log;

	@Override
	public String title() {
		return "BioImage Informatics Index";
	}

	@Override
	public List<SearchResult> search(final String text, final boolean fuzzy) {
		searchResults.clear();
		try {
			URL url = new URL("http://biii.eu/searchjsonexport?search_api_fulltext=(?="
					+ URLEncoder.encode(text, "utf-8") + ")(?=ImageJ)&_format=json&source=imagej");
			JsonArray json = JsonParser.parseReader(new InputStreamReader(url.openStream())).getAsJsonArray();
			json.forEach(e -> searchResults.add(createResult(e)));
		} catch (MalformedURLException exc) {
			log.debug(exc);
		} catch (UnsupportedEncodingException exc) {
			log.debug(exc);
		} catch (JsonIOException exc) {
			log.debug(exc);
		} catch (JsonSyntaxException exc) {
			log.debug(exc);
		} catch (IOException exc) {
			log.debug(exc);
		}
		
		return searchResults;
	}

	private SearchResult createResult(JsonElement e) {
		JsonObject obj = e.getAsJsonObject();
		String title = obj.get("title").getAsString();
		String link = "http://biii.eu/" + correctStringForShortLink(title);
		String summary = obj.get("body").getAsString();
		return new BIIIeuSearchResult(title, link, summary, DEFAULT_ICON, null);
	}

	private String correctStringForShortLink(String title) {
		title = title.replace(" ", "-");
		title = title.toLowerCase();

		// remove words and special characters
		String[] stringsToRemove = {
				"(", ")", "{", "}", "[", "]",
				".", ",", ";", ";",
				"&", "*", "@", "!", "$","%","~","`",
				" a ",
				" an ",
				" as ",
				" at ",
				" before ",
				" but ",
				" by ",
				" for ",
				" from ",
				" is ",
				" in ",
				" into ",
				" like ",
				" of " ,
				" off ",
				" on ",
				" onto ",
				" per ",
				" since ",
				" than ",
				" the ",
				" this ",
				" that ",
				" to ",
				" up ",
				" via ",
				" with "
		};
		for (String remove : stringsToRemove) {
			title = title.replace(remove, "");

			String trimmedRemove = remove.trim();
			if (title.startsWith(trimmedRemove + " ")) {
				title = title.substring(trimmedRemove.length() + 1);
			}
		}

		// remove special charactes
		title = Normalizer.normalize(title, Normalizer.Form.NFD);
		title = title.replaceAll("[^\\p{ASCII}]", "");

		// shorten
		title = title.trim();
		if (title.length() > 100) {
			title = title.substring(0, 100);
		}

		return title;
	}

	// for testing
	public static void main(String... args) {
		List<SearchResult> results = new BIIIeuSearcher().search("neuro", false);
		for (SearchResult result : results) {
			System.out.println(result.properties().get("Title"));
		}
	}
}
