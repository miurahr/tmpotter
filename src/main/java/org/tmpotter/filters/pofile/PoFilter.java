/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file come from OmegaT.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
 *                2006 Thomas Huriaux
 *                2008 Martin Fleurke
 *                2009 Alex Buloichik
 *                2011 Didier Briel
 *                2013-1014 Alex Buloichik, Enrique Estevez
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

package org.tmpotter.filters.pofile;

import org.tmpotter.util.Language;
import org.tmpotter.util.Localization;
import org.tmpotter.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tmpotter.filters.AbstractFilter;
import org.tmpotter.filters.FilterContext;


/**
 * Filter to support po files (in various encodings).
 *
 * Format described on http://www.gnu.org/software/hello/manual/gettext/PO-Files.html
 *
 * Filter is not thread-safe !
 *
 * Filter uses msgctx field as path, and plural index as suffix of path.
 *
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 * @author Thomas Huriaux
 * @author Martin Fleurke
 * @author Alex Buloichik
 * @author Didier Briel
 * @author Enrique Estevez
 * @author Hiroshi Miura
 */
public class PoFilter extends AbstractFilter {

  /**
   * Pattern for detecting the placeholders in a printf-function string which can occur in languages
   * like php, C and others. placeholder ::= "%" [ARGUMENTSWAPSPECIFIER] [SIGNSPECIFIER]
   * [PADDINGSPECIFIER] [ALIGNMENTSPECIFIER] [WIDTHSPECIFIER] [PRECISIONSPECIFIER] TYPESPECIFIER
   * NUMBER ::= { "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" } ARGUMENTSWAPSPECIFIER
   * = NUMBER "$" SIGNSPECIFIER ::= "+" | "-" PADDINGSPECIFIER ::= " " | "0" | "'" CHARACTER
   * ALIGNMENTSPECIFIER ::= "" | "-" WIDTHSPECIFIER ::= NUMBER PRECISIONSPECIFIER ::= "." NUMBER
   * TYPESPECIFIER ::= "b" | "c" | "d" | "e" | "E" | "f" | "F" | "g" | "G" | "i" | "n" | "o" | "p" |
   * "s" | "u" | "x" | "X" | "%" //c++: [cdieEfgGosuxXpn%] //php: [bcdeufFosxX%] NB: Because having
   * space as paddingspecifier leads to many false matches in regular text, and space being the
   * default padding specifier in php, and being able to have space or 0 as padding specifier by
   * prefixing it with ', and having the padding specifier not being used frequently in most cases,
   * the regular expression only corresponds with quote+paddingspecifier. NB2: The argument swap
   * specifier gives explicit ordering of variables, without it, the ordering is implicit (first in
   * sequence is first in order) Example in code:
   * <code>echo printf(gettext("%s is very %s"), "OmegaT", "great");</code>
   */
  private static final String RE_PRINTF_VARS = 
      "%([1-9]+\\$)?([+-])?('.)?(-)?([0-9]*)(\\.[0-9]*)?[bcdeEfFgGinopsuxX%]";
  public static final Pattern PRINTF_VARS = Pattern
      .compile(RE_PRINTF_VARS);

  private static class PluralInfo {

    public int plurals;
    public String expression;

    public PluralInfo(int nrOfPlurals, String pluralExpression) {
      plurals = nrOfPlurals;
      expression = pluralExpression;
    }
  }

  private static final Map<String, PluralInfo> pluralInfos;

  static {
    HashMap<String, PluralInfo> info = new HashMap<>();
    //list taken from http://translate.sourceforge.net/wiki/l10n/pluralforms d.d. 14-09-2012
    //See also http://unicode.org/repos/cldr-tmp/trunk/diff/supplemental/language_plural_rules.html
    info.put("ach", new PluralInfo(2, "(n > 1)"));
    info.put("af", new PluralInfo(2, "(n != 1)"));
    info.put("ak", new PluralInfo(2, "(n > 1)"));
    info.put("am", new PluralInfo(2, "(n > 1)"));
    info.put("an", new PluralInfo(2, "(n != 1)"));
    info.put("ar", new PluralInfo(6,
        " n==0 ? 0 : n==1 ? 1 : n==2 ? 2 : n%100>=3 && n%100<=10 ? 3 : n%100>=11 ? 4 : 5"));
    info.put("arn", new PluralInfo(2, "(n > 1)"));
    info.put("ast", new PluralInfo(2, "(n != 1)"));
    info.put("ay", new PluralInfo(1, "0"));
    info.put("az", new PluralInfo(2, "(n != 1) "));
    info.put("be", new PluralInfo(3,
        "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"));
    info.put("bg", new PluralInfo(2, "(n != 1)"));
    info.put("bn", new PluralInfo(2, "(n != 1)"));
    info.put("bo", new PluralInfo(1, "0"));
    info.put("br", new PluralInfo(2, "(n > 1)"));
    info.put("brx", new PluralInfo(2, "(n != 1)"));
    info.put("bs", new PluralInfo(3,
        "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2) "));
    info.put("ca", new PluralInfo(2, "(n != 1)"));
    info.put("cgg", new PluralInfo(1, "0"));
    info.put("cs", new PluralInfo(3, "(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2"));
    info.put("csb", new PluralInfo(3,
        "n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2"));
    info.put("cy", new PluralInfo(4, " (n==1) ? 0 : (n==2) ? 1 : (n != 8 && n != 11) ? 2 : 3"));
    info.put("da", new PluralInfo(2, "(n != 1)"));
    info.put("de", new PluralInfo(2, "(n != 1)"));
    info.put("doi", new PluralInfo(2, "(n != 1)"));
    info.put("dz", new PluralInfo(1, "0"));
    info.put("el", new PluralInfo(2, "(n != 1)"));
    info.put("en", new PluralInfo(2, "(n != 1)"));
    info.put("eo", new PluralInfo(2, "(n != 1)"));
    info.put("es", new PluralInfo(2, "(n != 1)"));
    info.put("et", new PluralInfo(2, "(n != 1)"));
    info.put("eu", new PluralInfo(2, "(n != 1)"));
    info.put("fa", new PluralInfo(1, "0"));
    info.put("ff", new PluralInfo(2, "(n != 1)"));
    info.put("fi", new PluralInfo(2, "(n != 1)"));
    info.put("fil", new PluralInfo(2, "n > 1"));
    info.put("fo", new PluralInfo(2, "(n != 1)"));
    info.put("fr", new PluralInfo(2, "(n > 1)"));
    info.put("fur", new PluralInfo(2, "(n != 1)"));
    info.put("fy", new PluralInfo(2, "(n != 1)"));
    info.put("ga", new PluralInfo(5, "n==1 ? 0 : n==2 ? 1 : n<7 ? 2 : n<11 ? 3 : 4"));
    info.put("gd", new PluralInfo(4, 
        "(n==1 || n==11) ? 0 : (n==2 || n==12) ? 1 : (n > 2 && n < 20) ? 2 : 3"));
    info.put("gl", new PluralInfo(2, "(n != 1)"));
    info.put("gu", new PluralInfo(2, "(n != 1)"));
    info.put("gun", new PluralInfo(2, "(n > 1)"));
    info.put("ha", new PluralInfo(2, "(n != 1)"));
    info.put("he", new PluralInfo(2, "(n != 1)"));
    info.put("hi", new PluralInfo(2, "(n != 1)"));
    info.put("hne", new PluralInfo(2, "(n != 1)"));
    info.put("hy", new PluralInfo(2, "(n != 1)"));
    info.put("hr", new PluralInfo(3,
        "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"));
    info.put("hu", new PluralInfo(2, "(n != 1)"));
    info.put("ia", new PluralInfo(2, "(n != 1)"));
    info.put("id", new PluralInfo(1, "0"));
    info.put("is", new PluralInfo(2, "(n%10!=1 || n%100==11)"));
    info.put("it", new PluralInfo(2, "(n != 1)"));
    info.put("ja", new PluralInfo(1, "0"));
    info.put("jbo", new PluralInfo(1, "0"));
    info.put("jv", new PluralInfo(2, "n!=0"));
    info.put("ka", new PluralInfo(1, "0"));
    info.put("kk", new PluralInfo(1, "0"));
    info.put("km", new PluralInfo(1, "0"));
    info.put("kn", new PluralInfo(2, "(n!=1)"));
    info.put("ko", new PluralInfo(1, "0"));
    info.put("ku", new PluralInfo(2, "(n!= 1)"));
    info.put("kw", new PluralInfo(4, " (n==1) ? 0 : (n==2) ? 1 : (n == 3) ? 2 : 3"));
    info.put("ky", new PluralInfo(1, "0"));
    info.put("lb", new PluralInfo(2, "(n != 1)"));
    info.put("ln", new PluralInfo(2, "n>1"));
    info.put("lo", new PluralInfo(1, "0"));
    info.put("lt", new PluralInfo(3,
        "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 or n%100>=20) ? 1 : 2)"));
    info.put("lv", new PluralInfo(3, "(n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2)"));
    info.put("mai", new PluralInfo(2, "(n != 1)"));
    info.put("mfe", new PluralInfo(2, "(n > 1)"));
    info.put("mg", new PluralInfo(2, "(n > 1)"));
    info.put("mi", new PluralInfo(2, "(n > 1)"));
    info.put("mk", new PluralInfo(2, " n==1 || n%10==1 ? 0 : 1"));
    info.put("ml", new PluralInfo(2, "(n != 1)"));
    info.put("mn", new PluralInfo(2, "(n != 1)"));
    info.put("mni", new PluralInfo(2, "(n != 1)"));
    info.put("mnk", new PluralInfo(3, "(n==0 ? 0 : n==1 ? 1 : 2"));
    info.put("mr", new PluralInfo(2, "(n != 1)"));
    info.put("ms", new PluralInfo(1, "0"));
    info.put("mt", new PluralInfo(4, 
        "(n==1 ? 0 : n==0 || ( n%100>1 && n%100<11) ? 1 : (n%100>10 && n%100<20 ) ? 2 : 3)"));
    info.put("my", new PluralInfo(1, "0"));
    info.put("nah", new PluralInfo(2, "(n != 1)"));
    info.put("nap", new PluralInfo(2, "(n != 1)"));
    info.put("nb", new PluralInfo(2, "(n != 1)"));
    info.put("ne", new PluralInfo(2, "(n != 1)"));
    info.put("nl", new PluralInfo(2, "(n != 1)"));
    info.put("se", new PluralInfo(2, "(n != 1)"));
    info.put("nn", new PluralInfo(2, "(n != 1)"));
    info.put("no", new PluralInfo(2, "(n != 1)"));
    info.put("nso", new PluralInfo(2, "(n != 1)"));
    info.put("oc", new PluralInfo(2, "(n > 1)"));
    info.put("or", new PluralInfo(2, "(n != 1)"));
    info.put("ps", new PluralInfo(2, "(n != 1)"));
    info.put("pa", new PluralInfo(2, "(n != 1)"));
    info.put("pap", new PluralInfo(2, "(n != 1)"));
    info.put("pl", new PluralInfo(3,
        "(n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"));
    info.put("pms", new PluralInfo(2, "(n != 1)"));
    info.put("pt", new PluralInfo(2, "(n != 1)"));
    info.put("rm", new PluralInfo(2, "(n!=1)"));
    info.put("ro", new PluralInfo(3, "(n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2)"));
    info.put("ru", new PluralInfo(3,
        "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"));
    info.put("rw", new PluralInfo(2, "(n != 1)"));
    info.put("sah", new PluralInfo(1, "0"));
    info.put("sat", new PluralInfo(2, "(n != 1)"));
    info.put("sco", new PluralInfo(2, "(n != 1)"));
    info.put("sd", new PluralInfo(2, "(n != 1)"));
    info.put("si", new PluralInfo(2, "(n != 1)"));
    info.put("sk", new PluralInfo(3, "(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2"));
    info.put("sl", new PluralInfo(4,
        "(n%100==1 ? 1 : n%100==2 ? 2 : n%100==3 || n%100==4 ? 3 : 0)"));
    info.put("so", new PluralInfo(2, "n != 1"));
    info.put("son", new PluralInfo(2, "(n != 1)"));
    info.put("sq", new PluralInfo(2, "(n != 1)"));
    info.put("sr", new PluralInfo(3,
        "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"));
    info.put("su", new PluralInfo(1, "0"));
    info.put("sw", new PluralInfo(2, "(n != 1)"));
    info.put("sv", new PluralInfo(2, "(n != 1)"));
    info.put("ta", new PluralInfo(2, "(n != 1)"));
    info.put("te", new PluralInfo(2, "(n != 1)"));
    info.put("tg", new PluralInfo(2, "(n > 1)"));
    info.put("ti", new PluralInfo(2, "n > 1"));
    info.put("th", new PluralInfo(1, "0"));
    info.put("tk", new PluralInfo(2, "(n != 1)"));
    info.put("tr", new PluralInfo(2, "(n>1)"));
    info.put("tt", new PluralInfo(1, "0"));
    info.put("ug", new PluralInfo(1, "0"));
    info.put("uk", new PluralInfo(3,
        "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"));
    info.put("ur", new PluralInfo(2, "(n != 1)"));
    info.put("uz", new PluralInfo(2, "(n > 1)"));
    info.put("vi", new PluralInfo(1, "0"));
    info.put("wa", new PluralInfo(2, "(n > 1)"));
    info.put("wo", new PluralInfo(1, "0"));
    info.put("yo", new PluralInfo(2, "(n != 1)"));
    info.put("zh", new PluralInfo(1, "0 "));
    pluralInfos = Collections.unmodifiableMap(info);
  }

  private static final Pattern COMMENT_FUZZY = Pattern.compile("#, fuzzy");
  private static final Pattern COMMENT_FUZZY_OTHER = Pattern.compile("#,.* fuzzy.*");
  private static final Pattern COMMENT_NOWRAP = Pattern.compile("#,.* no-wrap.*");
  private static final Pattern COMMENT_TRANSLATOR = Pattern.compile("# (.*)");
  private static final Pattern COMMENT_EXTRACTED = Pattern.compile("#\\. (.*)");
  private static final Pattern COMMENT_REFERENCE = Pattern.compile("#: (.*)");
  private static final Pattern MSG_ID = Pattern.compile("msgid(_plural)?\\s+\"(.*)\"");
  private static final Pattern MSG_STR = Pattern.compile("msgstr(\\[([0-9]+)\\])?\\s+\"(.*)\"");
  private static final Pattern MSG_CTX = Pattern.compile("msgctxt\\s+\"(.*)\"");
  private static final Pattern MSG_OTHER = Pattern.compile("\"(.*)\"");

  enum Mode { MSGID, MSGSTR, MSGID_PLURAL, MSGSTR_PLURAL, MSGCTX }

  private StringBuilder[] sources;
  private StringBuilder[] targets;
  private StringBuilder translatorComments;
  private StringBuilder extractedComments;
  private StringBuilder references;
  private int plurals = 2;
  private String expression = "";
  private String path;
  private boolean fuzzy;

  @Override
  public String getFileFormatName() {
    return "pofile";
  }

  @Override
  public boolean isSourceEncodingVariable() {
    return true;
  }

  @Override
  public boolean isTargetEncodingVariable() {
    return true;
  }

  @Override
  public String getFuzzyMark() {
    return "PO-fuzzy";
  }

  @Override
  protected void processFile(BufferedReader sourceFile, BufferedReader translatedFile,
      FilterContext fc) throws IOException {
    processFile(translatedFile, fc);
  }

  @Override
  public void processFile(File inFile, FilterContext fc) throws IOException {
    inEncodingLastParsedFile = getInputEncoding(fc, inFile);
    try (BufferedReader reader = createReader(inFile, inEncodingLastParsedFile)) {
      processFile(reader, fc);
    }
  }

  @Override
  public void processFile(BufferedReader in, FilterContext fc) throws IOException {
    // BOM (byte order mark) bugfix
    in.mark(1);
    int ch = in.read();
    if (ch != 0xFEFF) {
      in.reset();
    }
    fuzzy = false;
    Mode currentMode = null;
    int currentPlural = 0;

    sources = new StringBuilder[2];
    sources[0] = new StringBuilder();
    sources[1] = new StringBuilder();
    targets = new StringBuilder[2]; //can be overridden when header has been read and the number of
                                    //plurals is different.
    targets[0] = new StringBuilder();
    targets[1] = new StringBuilder();

    translatorComments = new StringBuilder();
    extractedComments = new StringBuilder();
    references = new StringBuilder();
    path = "";

    String src;
    while ((src = in.readLine()) != null) {
      // We trim trailing spaces, otherwise the regexps could fail.
      src = src.trim();

      if (COMMENT_FUZZY.matcher(src).matches()) {
        currentPlural = 0;
        fuzzy = true;
        flushTranslation(currentMode, fc);
        continue;
      } else if (COMMENT_FUZZY_OTHER.matcher(src).matches()) {
        currentPlural = 0;
        fuzzy = true;
        flushTranslation(currentMode, fc);
        src = src.replaceAll("(.*), fuzzy(.*)", "$1$2");
      }

      // FSM for po files
      if (COMMENT_NOWRAP.matcher(src).matches()) {
        currentPlural = 0;
        flushTranslation(currentMode, fc);
        continue;
      }

      Matcher mat;
      if ((mat = MSG_ID.matcher(src)).matches()) { //msg_id(_plural)
        currentPlural = 0;
        String text = mat.group(2);
        if (mat.group(1) == null) {
          // non-plural ID ('msg_id')
          //we can start a new translation. Flush current translation.
          //This has not happened when no empty lines are in between 'segments'.
          if (sources[0].length() > 0) {
            flushTranslation(currentMode, fc);
          }
          currentMode = Mode.MSGID;
          sources[0].append(text);
        } else {
          // plural ID ('msg_id_plural')
          currentMode = Mode.MSGID_PLURAL;
          sources[1].append(text);
        }

        continue;
      }

      if ((mat = MSG_STR.matcher(src)).matches()) {
        String text = mat.group(3);
        if (mat.group(1) == null) {
          // non-plural lines
          currentMode = Mode.MSGSTR;
          targets[0].append(text);
          currentPlural = 0;
        } else {
          currentMode = Mode.MSGSTR_PLURAL;
          // plurals, i.e. msgstr[N] lines
          currentPlural = Integer.parseInt(mat.group(2));
          if (currentPlural < plurals) {
            targets[currentPlural].append(text);
          }
        }
        continue;
      }

      if ((mat = MSG_CTX.matcher(src)).matches()) {
        currentMode = Mode.MSGCTX;
        currentPlural = 0;
        path = mat.group(1);
        continue;
      }
      if ((mat = COMMENT_REFERENCE.matcher(src)).matches()) {
        currentPlural = 0;
        references.append(mat.group(1));
        references.append("\n");
        continue;
      }
      if ((mat = COMMENT_EXTRACTED.matcher(src)).matches()) {
        currentPlural = 0;
        extractedComments.append(mat.group(1));
        extractedComments.append("\n");
        continue;
      }
      if ((mat = COMMENT_TRANSLATOR.matcher(src)).matches()) {
        currentPlural = 0;
        translatorComments.append(mat.group(1));
        translatorComments.append("\n");
        continue;
      }

      if ((mat = MSG_OTHER.matcher(src)).matches()) {
        String text = mat.group(1);
        if (currentMode == null) {
          throw new IOException(Localization.getString("POFILTER_INVALID_FORMAT"));
        }
        switch (currentMode) {
          case MSGID:
            sources[0].append(text);
            break;
          case MSGID_PLURAL:
            sources[1].append(text);
            break;
          case MSGSTR:
            targets[0].append(text);
            break;
          case MSGSTR_PLURAL:
            targets[currentPlural].append(text);
            break;
          case MSGCTX:
            break;
          default:
            break;
        }
        continue;
      }

      flushTranslation(currentMode, fc);
    }
    flushTranslation(currentMode, fc);
  }

  protected void align(int pair) {
    String pathSuffix;
    String src;
    String tgt;
    String cmt = "";
    if (pair > 0) {
      src = unescape(sources[1].toString());
      pathSuffix = "[" + pair + "]";
      cmt += StringUtil.format(Localization.getString("POFILTER_PLURAL_FORM_COMMENT"), pair) + "\n";
    } else {
      src = unescape(sources[pair].toString());
      pathSuffix = "";
    }
    tgt = unescape(targets[pair].toString());

    if (translatorComments.length() > 0) {
      cmt += Localization.getString("POFILTER_TRANSLATOR_COMMENTS") + "\n"
          + unescape(translatorComments.toString() + "\n");
    }
    if (extractedComments.length() > 0) {
      cmt += Localization.getString("POFILTER_EXTRACTED_COMMENTS") + "\n"
          + unescape(extractedComments.toString() + "\n");
    }
    if (references.length() > 0) {
      cmt += Localization.getString("POFILTER_REFERENCES") + "\n"
          + unescape(references.toString() + "\n");
    }
    if (cmt.length() == 0) {
      cmt = null;
    }
    align(src, tgt, cmt, pathSuffix);
  }

  /**
   *
   * @param source source
   * @param translation translation
   * @param comments comments
   * @param pathSuffix suffix for path to distinguish plural forms. It will be empty for first one,
   *     and [1],[2],... for next
   */
  protected void align(String source, String translation, String comments, String pathSuffix) {
    if (entryParseCallback != null) {
      entryParseCallback.addEntry(null, source, translation, fuzzy, comments, path + pathSuffix,
            this);
    } else {
      System.out.println("WARN: No ParseCallback defined!");
    }
  }

  /**
   * Flush translation text.
   * 
   * @param currentMode Which test is processing.
   * @param fc Filter context.
   * @throws IOException when error at File I/O
   */
  protected void flushTranslation(Mode currentMode, FilterContext fc) throws IOException {
    if (sources[0].length() == 0 && path.isEmpty()) {
      if (targets[0].length() == 0) {
        // there is no text to translate yet
        return;
      } else {
        // header
        //check existing plural statement. If it contains the number of plurals, then use it!
        StringBuilder targets0 = targets[0];
        String header = targets[0].toString();
        Pattern pluralPattern = Pattern.compile("Plural-Forms: *nplurals= *([0-9]+) *; *plural",
            Pattern.CASE_INSENSITIVE);
        Matcher pluralMatcher = pluralPattern.matcher(header);
        if (pluralMatcher.find()) {
          String nrOfPluralsString = header.substring(pluralMatcher.start(1), pluralMatcher.end(1));
          plurals = Integer.parseInt(nrOfPluralsString);
        } else {
          //else use predefined number of plurals, if it exists
          Language targetLang = fc.getTargetLang();
          String lang = targetLang.getLanguageCode().toLowerCase();
          PluralInfo pluralInfo = pluralInfos.get(lang);
          if (pluralInfo != null) {
            plurals = pluralInfo.plurals;
            expression = pluralInfo.expression;
          }
        }
        //update the number of targets according to new plural number
        targets = new StringBuilder[plurals];
        targets[0] = targets0;
        for (int i = 1; i < plurals; i++) {
          targets[i] = new StringBuilder();
        }
      }
      fuzzy = false;
    } else {
      // source exist
      if (sources[1].length() == 0) {
        // non-plurals
        align(0);
      } else {
        // plurals
        align(0);
        for (int i = 1; i < plurals; i++) {
          align(i);
        }
      }
      fuzzy = false;
    }
    sources[0].setLength(0);
    sources[1].setLength(0);
    for (int i = 0; i < plurals; i++) {
      targets[i].setLength(0);
    }
    path = "";
    translatorComments.setLength(0);
    extractedComments.setLength(0);
    references.setLength(0);
  }

  protected static final Pattern R1 = Pattern.compile("(?<!\\\\)((\\\\\\\\)*)\\\\\"");
  protected static final Pattern R2 = Pattern.compile("(?<!\\\\)((\\\\\\\\)*)\\\\n");
  protected static final Pattern R3 = Pattern.compile("(?<!\\\\)((\\\\\\\\)*)\\\\t");
  protected static final Pattern R4 = Pattern.compile("^\\\\n");

  /**
   * Unescape text from .po format.
   */
  private String unescape(String entry) {
        // Removes escapes from quotes. ( \" becomes " unless the \
    // was escaped itself.) The number of preceding slashes before \"
    // should not be odd, else the \ is escaped and not part of \".
    // The regex is: no backslash before an optional even number
    // of backslashes before \". Replace only the \" with " and keep the
    // other escaped backslashes )
    entry = R1.matcher(entry).replaceAll("$1\"");
        // Interprets newline sequence, except when preceded by \
    // \n becomes Linefeed, unless the \ was escaped itself.
    // The number of preceding slashes before \n should not be odd,
    // else the \ is escaped and not part of \n.
    // The regex is: no backslash before an optional even number of
    // backslashes before \n. Replace only the \n with <newline> and
    // keep
    // the other escaped backslashes.
    entry = R2.matcher(entry).replaceAll("$1\n");
    // same for \t, the tab character
    entry = R3.matcher(entry).replaceAll("$1\t");
    // Interprets newline sequence at the beginning of a line
    entry = R4.matcher(entry).replaceAll("\\\n");
    // Removes escape from backslash
    entry = entry.replace("\\\\", "\\");

    return entry;
  }

}
