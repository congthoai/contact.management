package com.contact.management.component.jdbc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * These code has been adapted from Spring Framework
 *
 * @see https://github.com/spring-projects/spring-framework/blob/master/spring-jdbc/src/main/java/org/springframework/jdbc/core/namedparam/NamedParameterUtils.java
 *
 * @author Hoang Dang
 */
@SuppressWarnings("PMD.GodClass")
public class NamedQueryParser {

    private static final char Colon = ':';

    private static final char QuestionMark = '?';

    /**
     * Set of characters that qualify as comment or quotes starting characters.
     */
    private static final String[] StartSkip = { "'", "\"", "--", "/*" };

    /**
     * Set of characters that at are the corresponding comment or quotes ending
     * characters.
     */
    private static final String[] StopSkip = { "'", "\"", "\n", "*/" };

    /**
     * Set of characters that qualify as parameter separators, indicating that a
     * parameter name in an SQL String has ended.
     */
    private static final String ParameterSeparator = "\"':&,;()|=+-*%/\\<>^";

    /**
     * An index with separator flags per character code. Technically only needed
     * between 34 and 124 at this point.
     */
    private static final boolean[] SeparatorIndex = new boolean[128];

    static {
        for (final char ch : ParameterSeparator.toCharArray()) {
            SeparatorIndex[ch] = true;
        }
    }

    private static final int DefaultCacheLimit = 1000;

    private final Map<String, ParsedNamedQuery> cache;

    public NamedQueryParser() {
        this(DefaultCacheLimit);
    }

    @SuppressWarnings("serial")
    public NamedQueryParser(final int cacheLimit) {
        cache = new LinkedHashMap<>(cacheLimit, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, ParsedNamedQuery> eldest) {
                return size() > cacheLimit;
            }
        };
    }

    public ParsedNamedQuery parse(final String sql) {
        if (sql == null) {
            throw new IllegalArgumentException();
        }

        ParsedNamedQuery result = cache.get(sql);
        if (result != null) {
            return result;
        }

        result = parseSqlStatement(sql);
        cache.put(sql, result);

        return result;
    }

    @SuppressWarnings({ "PMD.AvoidLiteralsInIfCondition", "PMD.ShortVariable", "PMD.CyclomaticComplexity",
            "PMD.NcssCount", "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    private ParsedNamedQuery parseSqlStatement(final String sql) {
        final StringBuilder builder = new StringBuilder();
        final char[] statement = sql.toCharArray();
        final List<String> parameters = new ArrayList<>();

        int lastPosition = 0;
        int i = 0;
        while (i < statement.length) {
            while (i < statement.length) {
                final int skipToPosition = skipCommentsAndQuotes(statement, i);

                if (i == skipToPosition) {
                    break;
                } else {
                    i = skipToPosition;
                }
            }

            if (i >= statement.length) {
                break;
            }

            final char ch = statement[i];
            if (ch == Colon || ch == '&') {
                int j = i + 1;

                // Postgres-style "::" casting operator should be skipped
                if (ch == Colon && j < statement.length && statement[j] == Colon) {
                    i = i + 2;
                    continue;
                }

                // :{x} style parameter
                if (ch == ':' && j < statement.length && statement[j] == '{') {
                    while (statement[j] != '}') {
                        j++;
                        if (j >= statement.length) {
                            throw new IllegalArgumentException("Non-terminated named parameter declaration "
                                    + "at position " + i + " in statement: " + sql);
                        }

                        if (statement[j] == Colon || statement[j] == '{') {
                            throw new IllegalArgumentException("Parameter name contains invalid character '"
                                    + statement[j] + "' at position " + i + " in statement: " + sql);
                        }
                    }

                    if (j - i > 2) {
                        final String parameter = sql.substring(i + 2, j);
                        parameters.add(parameter);

                        builder.append(sql.substring(lastPosition, i))
                                .append(QuestionMark);
                        lastPosition = j + 1;
                    }

                    j++;
                } else {
                    while (j < statement.length && !isParameterSeparator(statement[j])) {
                        j++;
                    }

                    if (j - i > 1) {
                        final String parameter = sql.substring(i + 1, j);
                        parameters.add(parameter);

                        builder.append(sql.substring(lastPosition, i))
                                .append(QuestionMark);
                        lastPosition = j;
                    }
                }

                i = j - 1;
            } else {
                if (ch == '\\') {
                    final int nextI = i + 1;
                    // escaped ":" should be skipped

                    if (nextI < statement.length && statement[nextI] == Colon) {
                        builder.append(sql.substring(lastPosition, i))
                                .append(String.valueOf(Colon));
                        lastPosition = i + 2;

                        i = i + 2;
                        continue;
                    }
                }

                if (ch == '?') {
                    final int nextI = i + 1;

                    // Postgres-style "??", "?|", "?&" operator should be skipped
                    if (nextI < statement.length
                            && (statement[nextI] == '?' || statement[nextI] == '|' || statement[nextI] == '&')) {
                        i = i + 2;
                        continue;
                    }
                }
            }

            i++;
        }

        if (lastPosition < statement.length) {
            builder.append(sql.substring(lastPosition));
        }

        return new ParsedNamedQuery(builder.toString(), parameters);
    }

    /**
     * Skip over comments and quoted names present in an SQL statement.
     *
     * @param statement character array containing SQL statement
     * @param position  current position of statement
     * @return next position to process after any comments or quotes are skipped
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private int skipCommentsAndQuotes(final char[] statement, final int position) {
        for (int i = 0; i < StartSkip.length; i++) {
            final String startSkip = StartSkip[i];
            final String stopSkip = StopSkip[i];
            final int length = startSkip.length();

            if (statement[position] == startSkip.charAt(0)) {
                boolean match = true;

                for (int j = 1; j < length; j++) {
                    if (statement[position + j] != startSkip.charAt(j)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    for (int m = position + length; m < statement.length; m++) {
                        if (statement[m] == stopSkip.charAt(0)) {
                            boolean endMatch = true;
                            int endPos = m;

                            for (int n = 1; n < stopSkip.length(); n++) {
                                // last comment not closed properly
                                if (m + n >= statement.length) {
                                    return statement.length;
                                }

                                if (statement[m + n] != stopSkip.charAt(n)) {
                                    endMatch = false;
                                    break;
                                }

                                endPos = m + n;
                            }

                            // found character sequence ending comment or quote
                            if (endMatch) {
                                return endPos + 1;
                            }
                        }
                    }

                    // character sequence ending comment or quote not found
                    return statement.length;
                }
            }
        }

        return position;
    }

    /**
     * Determine whether a parameter name ends at the current position, that is,
     * whether the given character qualifies as a separator.
     */
    private boolean isParameterSeparator(final char ch) {
        return ch < 128 && SeparatorIndex[ch] || Character.isWhitespace(ch);
    }

}