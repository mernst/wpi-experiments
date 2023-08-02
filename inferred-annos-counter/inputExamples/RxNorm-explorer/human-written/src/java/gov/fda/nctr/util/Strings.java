package gov.fda.nctr.util;

import java.io.*;
import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Pattern;

import gov.fda.nctr.data_access.JdbcUtils;

public final class Strings
{
  private static final String UPPER_ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final SecureRandom secureRandom = new SecureRandom();

  /// Return a string without any leading space characters (' ' only).
  public static String ltrim(String s)
  {
    int i = 0;
    while (i < s.length() && s.charAt(i) == ' ')
    {
      i++;
    }
    return s.substring(i);
  }

  public static String randomAlphaNumericString(int length)
  {
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++)
      sb.append(UPPER_ALPHANUM.charAt(secureRandom.nextInt(UPPER_ALPHANUM.length())));

    return sb.toString();
  }

  public static String loadTextResource(String resourcePath)
  {
    StringBuilder sb = new StringBuilder();

    try (InputStream inputStream = Nullables.requireNonNull(Nullables.requireNonNull(JdbcUtils.class.getClassLoader()).getResourceAsStream(resourcePath)))
    {
      BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = r.readLine()) != null)
      {
        line = ltrim(line);
        if (!line.startsWith("--"))
        {
          sb.append(line);
          sb.append('\n');
        }
      }

      return sb.toString();
    }
    catch (IOException e)
    {
      throw new IOError(e);
    }
  }

  public static String replaceAll(String s, List<PatternReplacement> patternReplacements)
  {
    String res = s;

    for (PatternReplacement patRepl: patternReplacements)
      res = patRepl.getPattern().matcher(res).replaceAll(patRepl.getReplacement());

    return res;
  }

  public static String replaceAll(String s, PatternReplacement... patternReplacements)
  {
    String res = s;

    for (PatternReplacement patRepl: patternReplacements)
      res = patRepl.getPattern().matcher(res).replaceAll(patRepl.getReplacement());

    return res;
  }

  public static class PatternReplacement
  {
    private final Pattern pattern;
    private final String replacement;

    public PatternReplacement(Pattern pattern, String replacement)
    {
      this.pattern = pattern;
      this.replacement = replacement;
    }

    public Pattern getPattern() { return pattern; }

    public String getReplacement() { return replacement; }
  }

  private Strings() {}
}
