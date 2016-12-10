/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
 *
 *  Part of this come from OmegaT.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * *************************************************************************/

package org.tmpotter.segmentation;

import org.tmpotter.util.Language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The class that sentences the paragraphs
 * into sentences and glues translated sentences together to form a
 * paragraph.
 *
 * @author Maxym Mykhalchuk
 */
public final class Segmenter {

    public static volatile SRX srx;

    synchronized public static void setSrx(SRX newSrx) {
        srx = newSrx;
    }

    /**
     * private to disallow creation
     */
    private Segmenter() {
    }

    /**
     * Segments the paragraph to sentences according to currently setup rules.
     * <p>
     * Bugfix for
     * <a href="http://sourceforge.net/support/tracker.php?aid=1288742">issue
     * 1288742</a>: Sentences are returned without spaces in the beginning and at
     * the end of a sentence.
     * <p>
     * An additional list with space information is returned to be able to glue
     * translation together with the same spaces between them as in original
     * paragraph.
     *
     * @param lang      language to analyze
     * @param paragraph the paragraph text
     * @param spaces    list to store information about spaces between sentences
     * @param ruleList  list to store rules that account to breaks
     * @return list of sentences (String objects)
     */
    public static List<String> segment(Language lang, String paragraph,
                                       List<StringBuilder> spaces,
                                       List<Rule> ruleList) {
        if (paragraph == null) {
            return null;
        }
        assert (spaces != null);
        assert (ruleList != null);

        List<String> segments = breakParagraph(lang, paragraph, ruleList);
        List<String> sentences = new ArrayList<>(segments.size());
        spaces.clear();
        for (String one : segments) {
            int len = one.length();
            int b = 0;
            StringBuilder bs = new StringBuilder();
            for (int cp; b < len; b += Character.charCount(cp)) {
                cp = one.codePointAt(b);
                if (!Character.isWhitespace(cp)) {
                    break;
                }
                bs.appendCodePoint(cp);
            }

            int e = len;
            StringBuilder es = new StringBuilder();
            for (int cp; e > b; e -= Character.charCount(cp)) {
                cp = one.codePointBefore(e);
                if (!Character.isWhitespace(cp)) {
                    break;
                }
                es.appendCodePoint(cp);
            }
            es.reverse();

            String trimmed = one.substring(b, e);
            sentences.add(trimmed);
            spaces.add(bs);
            spaces.add(es);
        }
        return sentences;
    }

    /**
     * Returns pre-sentences (sentences with spaces between), computed by breaking
     * paragraph into chunks of text. Also returns the list with "the reasons" why
     * the breaks were made, i.e. the list of break rules that contributed to each
     * of the breaks made.
     * <p>
     * If glued back together, these strings form the same paragraph text as this
     * function was fed.
     *
     * @param paragraph the paragraph text
     * @param ruleList  list to store rules that account to breaks
     */
    private static List<String> breakParagraph(Language lang, String paragraph,
                                               List<Rule> ruleList) {
        List<Rule> rules = Segmenter.srx.lookupRulesForLanguage(lang);

        // determining the applicable break positions
        Set<BreakPosition> noBreakPositions = new TreeSet<BreakPosition>();
        Set<BreakPosition> breakPositions = new TreeSet<BreakPosition>();
        for (int i = rules.size() - 1; i >= 0; i--) {
            Rule rule = rules.get(i);
            List<BreakPosition> rulebreaks = getBreaks(paragraph, rule);
            if (rule.isBreakRule()) {
                breakPositions.addAll(rulebreaks);
                noBreakPositions.removeAll(rulebreaks);
            } else {
                noBreakPositions.addAll(rulebreaks);
                breakPositions.removeAll(rulebreaks);
            }
        }
        breakPositions.removeAll(noBreakPositions);

        // and now breaking the string according to the positions
        List<String> segments = new ArrayList<>();
        ruleList.clear();
        int prevpos = 0;
        for (BreakPosition bposition : breakPositions) {
            String oneseg = paragraph.substring(prevpos, bposition.position);
            segments.add(oneseg);
            ruleList.add(bposition.reason);
            prevpos = bposition.position;
        }
        try {
            String oneseg = paragraph.substring(prevpos);

            // Sometimes the last segment may be empty,
            // it happens for paragraphs like "Rains. "
            // So if it's an empty segment and there's a previous segment
            if (oneseg.trim().isEmpty() && !segments.isEmpty()) {
                String prev = segments.get(segments.size() - 1);
                prev += oneseg;
                segments.set(segments.size() - 1, prev);
            } else {
                segments.add(oneseg);
            }
        } catch (IndexOutOfBoundsException iobe) {
            // FIXME
        }

        return segments;
    }

    private static final Pattern DEFAULT_BEFOREBREAK_PATTERN = Pattern.compile(".", Pattern.DOTALL);

    /**
     * Returns the places of possible breaks between sentences.
     */
    private static List<BreakPosition> getBreaks(String paragraph, Rule rule) {
        List<BreakPosition> res = new ArrayList<>();

        Matcher bbm = null;
        if (rule.getBeforebreak() != null) {
            bbm = rule.getCompiledBeforebreak().matcher(paragraph);
        }
        Matcher abm = null;
        if (rule.getAfterbreak() != null) {
            abm = rule.getCompiledAfterbreak().matcher(paragraph);
        }

        if (bbm == null && abm == null) {
            return res;
        }

        if (abm != null) {
            if (!abm.find()) {
                return res;
            }
        }

        if (bbm == null) {
            bbm = DEFAULT_BEFOREBREAK_PATTERN.matcher(paragraph);
        }

        while (bbm.find()) {
            int bbe = bbm.end();
            if (abm == null) {
                res.add(new BreakPosition(bbe, rule));
            } else {
                int abs = abm.start();
                while (abs < bbe) {
                    boolean found = abm.find();
                    if (!found) {
                        return res;
                    }
                    abs = abm.start();
                }
                if (abs == bbe) {
                    res.add(new BreakPosition(bbe, rule));
                }
            }
        }

        return res;
    }

    /**
     * A class for a break position that knows which rule contributed to it.
     */
    static class BreakPosition implements Comparable<BreakPosition> {

        /**
         * Break/Exception position.
         */
        int position;
        /**
         * Rule that contributed to the break.
         */
        Rule reason;

        /**
         * Creates a new break position.
         */
        BreakPosition(int position, Rule reason) {
            this.position = position;
            this.reason = reason;
        }

        /**
         * Other BreakPosition is "equal to" this one iff it has the same position.
         */
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof BreakPosition)) {
                return false;
            }
            BreakPosition that = (BreakPosition) obj;

            return this.position == that.position;
        }

        /**
         * Returns a hash code == position for the object.
         */
        @Override
        public int hashCode() {
            return this.position;
        }

        /**
         * Compares this break position with another.
         *
         * @return a negative integer if its position is less than the another's,
         * zero if they are equal, or a positive integer as its position is greater
         * than the another's.
         * @throws ClassCastException if the specified object's type prevents it
         *                            from being compared to this Object.
         */
        public int compareTo(BreakPosition that) {
            return this.position - that.position;
        }
    }

    /**
     * CJK languages.
     */
    private static final Pattern CJK_LINE_BREAK_OR_TAB_PATTERN = Pattern
        .compile("^( *)[\\r\\n\\t]");
    private static final Set<String> CJK_LANGUAGES = new HashSet<String>();

    static {
        CJK_LANGUAGES.add("ZH");
        CJK_LANGUAGES.add("JA");
        CJK_LANGUAGES.add("KO");
    }
}
