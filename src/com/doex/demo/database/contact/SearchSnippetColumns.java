package com.doex.demo.database.contact;

/**
 * Additional column returned by the {@link Contacts#CONTENT_FILTER_URI} providing the
 * explanation of why the filter matched the contact.  Specifically, it contains the
 * data elements that matched the query.  The overall number of words in the snippet
 * can be capped.
 *
 * @hide
 */
public class SearchSnippetColumns {

    /**
     * The search snippet constructed according to the SQLite rules, see
     * http://www.sqlite.org/fts3.html#snippet
     * <p>
     * The snippet may contain (parts of) several data elements comprising
     * the contact.
     *
     * @hide
     */
    public static final String SNIPPET = "snippet";


    /**
     * Comma-separated parameters for the generation of the snippet:
     * <ul>
     * <li>The "start match" text. Default is &lt;b&gt;</li>
     * <li>The "end match" text. Default is &lt;/b&gt;</li>
     * <li>The "ellipsis" text. Default is &lt;b&gt;...&lt;/b&gt;</li>
     * <li>Maximum number of tokens to include in the snippet. Can be either
     * a positive or a negative number: A positive number indicates how many
     * tokens can be returned in total. A negative number indicates how many
     * tokens can be returned per occurrence of the search terms.</li>
     * </ul>
     *
     * @hide
     */
    public static final String SNIPPET_ARGS_PARAM_KEY = "snippet_args";

    /**
     * A key to ask the provider to defer the snippeting to the client if possible.
     * Value of 1 implies true, 0 implies false when 0 is the default.
     * When a cursor is returned to the client, it should check for an extra with the name
     * {@link ContactsContract#DEFERRED_SNIPPETING} in the cursor. If it exists, the client
     * should do its own snippeting using {@link ContactsContract#snippetize}. If
     * it doesn't exist, the snippet column in the cursor should already contain a snippetized
     * string.
     *
     * @hide
     */
    public static final String DEFERRED_SNIPPETING_KEY = "deferred_snippeting";
}

