/*
 * Copyright 2016 Caulfield IP Holdings (Caulfield) and affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * Software Code is protected by copyright. Caulfield hereby
 * reserves all rights and copyrights and no license is
 * granted under said copyrights in this Software License Agreement.
 * Caulfield generally licenses software for commercialization
 * pursuant to the terms of either a Standard Software Source Code
 * License Agreement or a Standard Product License Agreement.
 * A copy of these agreements may be obtained by sending a request
 * via email to info@caufield.org.
 */
package ch.keybridge.lib.sig.utility;

import java.util.regex.Matcher;

/**
 * UTF-8 encoding table and Unicode characters, code points U+2000 to U+207F.
 * <p>
 * An enumerated class to facilitate translation / substitution of
 * double-encoded UTF characters.
 *
 * @author Key Bridge LLC 02/10/16
 * @see <a href="http://www.utf8-chartable.de/">UTF-8 encoding table</a>
 */
public enum UTFCharacterUtility {

  EM_QUAD("\u2001", "\\xe2\\x80\\x81"),
  EN_SPACE("\u2002", "\\xe2\\x80\\x82"),
  EM_SPACE("\u2003", "\\xe2\\x80\\x83"),
  THREE_PER_EM_SPACE("\u2004", "\\xe2\\x80\\x84"),
  FOUR_PER_EM_SPACE("\u2005", "\\xe2\\x80\\x85"),
  SIX_PER_EM_SPACE("\u2006", "\\xe2\\x80\\x86"),
  FIGURE_SPACE("\u2007", "\\xe2\\x80\\x87"),
  PUNCTUATION_SPACE("\u2008", "\\xe2\\x80\\x88"),
  THIN_SPACE("\u2009", "\\xe2\\x80\\x89"),
  HAIR_SPACE("\u200A", "\\xe2\\x80\\x8a"),
  ZERO_WIDTH_SPACE("\u200B", "\\xe2\\x80\\x8b"),
  ZERO_WIDTH_NON_JOINER("\u200C", "\\xe2\\x80\\x8c"),
  ZERO_WIDTH_JOINER("\u200D", "\\xe2\\x80\\x8d"),
  LEFT_TO_RIGHT_MARK("\u200E", "\\xe2\\x80\\x8e"),
  RIGHT_TO_LEFT_MARK("\u200F", "\\xe2\\x80\\x8f"),
  HYPHEN("\u2010", "\\xe2\\x80\\x90"),
  NON_BREAKING_HYPHEN("\u2011", "\\xe2\\x80\\x91"),
  FIGURE_DASH("\u2012", "\\xe2\\x80\\x92"),
  EN_DASH("\u2013", "\\xe2\\x80\\x93"),
  EM_DASH("\u2014", "\\xe2\\x80\\x94"),
  HORIZONTAL_BAR("\u2015", "\\xe2\\x80\\x95"),
  DOUBLE_VERTICAL_LINE("\u2016", "\\xe2\\x80\\x96"),
  DOUBLE_LOW_LINE("\u2017", "\\xe2\\x80\\x97"),
  LEFT_SINGLE_QUOTATION_MARK("\u2018", "\\xe2\\x80\\x98"),
  RIGHT_SINGLE_QUOTATION_MARK("\u2019", "\\xe2\\x80\\x99"),
  SINGLE_LOW_9_QUOTATION_MARK("\u201A", "\\xe2\\x80\\x9a"),
  SINGLE_HIGH_REVERSED_9_QUOTATION_MARK("\u201B", "\\xe2\\x80\\x9b"),
  LEFT_DOUBLE_QUOTATION_MARK("\u201C", "\\xe2\\x80\\x9c"),
  RIGHT_DOUBLE_QUOTATION_MARK("\u201D", "\\xe2\\x80\\x9d"),
  DOUBLE_LOW_9_QUOTATION_MARK("\u201E", "\\xe2\\x80\\x9e"),
  DOUBLE_HIGH_REVERSED_9_QUOTATION_MARK("\u201F", "\\xe2\\x80\\x9f"),
  DAGGER("\u2020", "\\xe2\\x80\\xa0"),
  DOUBLE_DAGGER("\u2021", "\\xe2\\x80\\xa1"),
  BULLET("\u2022", "\\xe2\\x80\\xa2"),
  TRIANGULAR_BULLET("\u2023", "\\xe2\\x80\\xa3"),
  ONE_DOT_LEADER("\u2024", "\\xe2\\x80\\xa4"),
  TWO_DOT_LEADER("\u2025", "\\xe2\\x80\\xa5"),
  HORIZONTAL_ELLIPSIS("\u2026", "\\xe2\\x80\\xa6"),
  HYPHENATION_POINT("\u2027", "\\xe2\\x80\\xa7"),
  LINE_SEPARATOR("\u2028", "\\xe2\\x80\\xa8"),
  PARAGRAPH_SEPARATOR("\u2029", "\\xe2\\x80\\xa9"),
  LEFT_TO_RIGHT_EMBEDDING("\u202A", "\\xe2\\x80\\xaa"),
  RIGHT_TO_LEFT_EMBEDDING("\u202B", "\\xe2\\x80\\xab"),
  POP_DIRECTIONAL_FORMATTING("\u202C", "\\xe2\\x80\\xac"),
  LEFT_TO_RIGHT_OVERRIDE("\u202D", "\\xe2\\x80\\xad"),
  RIGHT_TO_LEFT_OVERRIDE("\u202E", "\\xe2\\x80\\xae"),
  NARROW_NO_BREAK_SPACE("\u202F", "\\xe2\\x80\\xaf"),
  PER_MILLE_SIGN("\u2030", "\\xe2\\x80\\xb0"),
  PER_TEN_THOUSAND_SIGN("\u2031", "\\xe2\\x80\\xb1"),
  PRIME("\u2032", "\\xe2\\x80\\xb2"),
  DOUBLE_PRIME("\u2033", "\\xe2\\x80\\xb3"),
  TRIPLE_PRIME("\u2034", "\\xe2\\x80\\xb4"),
  REVERSED_PRIME("\u2035", "\\xe2\\x80\\xb5"),
  REVERSED_DOUBLE_PRIME("\u2036", "\\xe2\\x80\\xb6"),
  REVERSED_TRIPLE_PRIME("\u2037", "\\xe2\\x80\\xb7"),
  CARET("\u2038", "\\xe2\\x80\\xb8"),
  SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK("\u2039", "\\xe2\\x80\\xb9"),
  SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK("\u203A", "\\xe2\\x80\\xba"),
  REFERENCE_MARK("\u203B", "\\xe2\\x80\\xbb"),
  DOUBLE_EXCLAMATION_MARK("\u203C", "\\xe2\\x80\\xbc"),
  INTERROBANG("\u203D", "\\xe2\\x80\\xbd"),
  OVERLINE("\u203E", "\\xe2\\x80\\xbe"),
  UNDERTIE("\u203F", "\\xe2\\x80\\xbf"),
  CHARACTER_TIE("\u2040", "\\xe2\\x81\\x80"),
  CARET_INSERTION_POINT("\u2041", "\\xe2\\x81\\x81"),
  ASTERISM("\u2042", "\\xe2\\x81\\x82"),
  HYPHEN_BULLET("\u2043", "\\xe2\\x81\\x83"),
  FRACTION_SLASH("\u2044", "\\xe2\\x81\\x84"),
  LEFT_SQUARE_BRACKET_WITH_QUILL("\u2045", "\\xe2\\x81\\x85"),
  RIGHT_SQUARE_BRACKET_WITH_QUILL("\u2046", "\\xe2\\x81\\x86"),
  DOUBLE_QUESTION_MARK("\u2047", "\\xe2\\x81\\x87"),
  QUESTION_EXCLAMATION_MARK("\u2048", "\\xe2\\x81\\x88"),
  EXCLAMATION_QUESTION_MARK("\u2049", "\\xe2\\x81\\x89"),
  TIRONIAN_SIGN_ET("\u204A", "\\xe2\\x81\\x8a"),
  REVERSED_PILCROW_SIGN("\u204B", "\\xe2\\x81\\x8b"),
  BLACK_LEFTWARDS_BULLET("\u204C", "\\xe2\\x81\\x8c"),
  BLACK_RIGHTWARDS_BULLET("\u204D", "\\xe2\\x81\\x8d"),
  LOW_ASTERISK("\u204E", "\\xe2\\x81\\x8e"),
  REVERSED_SEMICOLON("\u204F", "\\xe2\\x81\\x8f"),
  CLOSE_UP("\u2050", "\\xe2\\x81\\x90"),
  TWO_ASTERISKS_ALIGNED_VERTICALLY("\u2051", "\\xe2\\x81\\x91"),
  COMMERCIAL_MINUS_SIGN("\u2052", "\\xe2\\x81\\x92"),
  SWUNG_DASH("\u2053", "\\xe2\\x81\\x93"),
  INVERTED_UNDERTIE("\u2054", "\\xe2\\x81\\x94"),
  FLOWER_PUNCTUATION_MARK("\u2055", "\\xe2\\x81\\x95"),
  THREE_DOT_PUNCTUATION("\u2056", "\\xe2\\x81\\x96"),
  QUADRUPLE_PRIME("\u2057", "\\xe2\\x81\\x97"),
  FOUR_DOT_PUNCTUATION("\u2058", "\\xe2\\x81\\x98"),
  FIVE_DOT_PUNCTUATION("\u2059", "\\xe2\\x81\\x99"),
  TWO_DOT_PUNCTUATION("\u205A", "\\xe2\\x81\\x9a"),
  FOUR_DOT_MARK("\u205B", "\\xe2\\x81\\x9b"),
  DOTTED_CROSS("\u205C", "\\xe2\\x81\\x9c"),
  TRICOLON("\u205D", "\\xe2\\x81\\x9d"),
  VERTICAL_FOUR_DOTS("\u205E", "\\xe2\\x81\\x9e"),
  MEDIUM_MATHEMATICAL_SPACE("\u205F", "\\xe2\\x81\\x9f"),
  WORD_JOINER("\u2060", "\\xe2\\x81\\xa0"),
  FUNCTION_APPLICATION("\u2061", "\\xe2\\x81\\xa1"),
  INVISIBLE_TIMES("\u2062", "\\xe2\\x81\\xa2"),
  INVISIBLE_SEPARATOR("\u2063", "\\xe2\\x81\\xa3"),
  INVISIBLE_PLUS("\u2064", "\\xe2\\x81\\xa4"),
  U_2065("\u2065", "\\xe2\\x81\\xa5"),
  LEFT_TO_RIGHT_ISOLATE("\u2066", "\\xe2\\x81\\xa6"),
  RIGHT_TO_LEFT_ISOLATE("\u2067", "\\xe2\\x81\\xa7"),
  FIRST_STRONG_ISOLATE("\u2068", "\\xe2\\x81\\xa8"),
  POP_DIRECTIONAL_ISOLATE("\u2069", "\\xe2\\x81\\xa9"),
  INHIBIT_SYMMETRIC_SWAPPING("\u206A", "\\xe2\\x81\\xaa"),
  ACTIVATE_SYMMETRIC_SWAPPING("\u206B", "\\xe2\\x81\\xab"),
  INHIBIT_ARABIC_FORM_SHAPING("\u206C", "\\xe2\\x81\\xac"),
  ACTIVATE_ARABIC_FORM_SHAPING("\u206D", "\\xe2\\x81\\xad"),
  NATIONAL_DIGIT_SHAPES("\u206E", "\\xe2\\x81\\xae"),
  NOMINAL_DIGIT_SHAPES("\u206F", "\\xe2\\x81\\xaf"),
  SUPERSCRIPT_ZERO("\u2070", "\\xe2\\x81\\xb0"),
  SUPERSCRIPT_LATIN_SMALL_LETTER_I("\u2071", "\\xe2\\x81\\xb1"),
  U_2072("\u2072", "\\xe2\\x81\\xb2"),
  U_2073("\u2073", "\\xe2\\x81\\xb3"),
  SUPERSCRIPT_FOUR("\u2074", "\\xe2\\x81\\xb4"),
  SUPERSCRIPT_FIVE("\u2075", "\\xe2\\x81\\xb5"),
  SUPERSCRIPT_SIX("\u2076", "\\xe2\\x81\\xb6"),
  SUPERSCRIPT_SEVEN("\u2077", "\\xe2\\x81\\xb7"),
  SUPERSCRIPT_EIGHT("\u2078", "\\xe2\\x81\\xb8"),
  SUPERSCRIPT_NINE("\u2079", "\\xe2\\x81\\xb9"),
  SUPERSCRIPT_PLUS_SIGN("\u207A", "\\xe2\\x81\\xba"),
  SUPERSCRIPT_MINUS("\u207B", "\\xe2\\x81\\xbb"),
  SUPERSCRIPT_EQUALS_SIGN("\u207C", "\\xe2\\x81\\xbc"),
  SUPERSCRIPT_LEFT_PARENTHESIS("\u207D", "\\xe2\\x81\\xbd"),
  SUPERSCRIPT_RIGHT_PARENTHESIS("\u207E", "\\xe2\\x81\\xbe"),
  SUPERSCRIPT_LATIN_SMALL_LETTER_N("\u207F", "\\xe2\\x81\\xbf");

  /**
   * The "normal" UTF character code.
   */
  private final String utf;
  /**
   * The double encoded UTF character code.
   */
  private final String encoded;

  private UTFCharacterUtility(String utf, String encoded) {
    this.utf = utf;
    this.encoded = encoded;
  }

  /**
   * Get the "normal" UTF character code.
   *
   * @return the "normal" UTF character code.
   */
  public String getUtf() {
    return utf;
  }

  /**
   * Get the double encoded UTF character code.
   *
   * @return the double encoded UTF character code.
   */
  public String getEncoded() {
    return encoded;
  }

  /**
   * Substitute all double encoded UTF characters in the input string with
   * "normal" UTF characters.
   *
   * @param input the input string
   * @return the input string with (hopefully) corrected UTF characters.
   */
  public static String substitute(String input) {
    /**
     * Use Matcher.quoteReplacement in the call to replaceAll since the encoded
     * value includes a back slash "\" character.
     */
    String output = input;
    for (UTFCharacterUtility value : UTFCharacterUtility.values()) {
      output = output.replaceAll(Matcher.quoteReplacement(value.getEncoded()), value.getUtf());
    }
    return output;
  }

}
