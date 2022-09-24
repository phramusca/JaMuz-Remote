package com.beaglebuddy.id3.pojo;

import com.beaglebuddy.id3.enums.v23.FrameType;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * An ID3v2.x synchronized lyric consists of some text and a corresponding timestamp specifying when the text occurs in the .mp3 file.
 * <p>
 * <table class="beaglebuddy">
 *    <caption><b>Synchronized Lyric Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">text     </td><td class="beaglebuddy">the text of the lyric.                                               </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">timeStamp</td><td class="beaglebuddy">when the lyric occurs within the song.
 *                                                                                                         See {@link com.beaglebuddy.id3.enums.TimeStampFormat} for a list of
 *                                                                                                         the supported kinds of time stamps.                                  </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p>
 * An example of a synchronized lyric using ms as the time stamp unit is:
 * <code>
 *    <pre class="beaglebuddy">
 *    SynchronizedLyric[] synchronizedLyrics = {new SynchronizedLyric("Tom-"   , 20350),
 *                                              new SynchronizedLyric("my"     , 20400),
 *                                              new SynchronizedLyric("used"   , 20625),
 *                                              new SynchronizedLyric("to"     , 20700),
 *                                              new SynchronizedLyric("work"   , 20800),
 *                                              new SynchronizedLyric("on"     , 20900),
 *                                              new SynchronizedLyric("the"    , 21000),
 *                                              new SynchronizedLyric("docks"  , 21100),
 *                                              new SynchronizedLyric("Un-"    , 22800),
 *                                              new SynchronizedLyric("ions"   , 22900),
 *                                              new SynchronizedLyric("been"   , 23000),
 *                                              new SynchronizedLyric("on"     , 23100),
 *                                              new SynchronizedLyric("strike" , 23200),
 *                                              new SynchronizedLyric("He's"   , 23800),
 *                                              new SynchronizedLyric("down"   , 23900),
 *                                              new SynchronizedLyric("on"     , 24000),
 *                                              new SynchronizedLyric("his"    , 24100),
 *                                              new SynchronizedLyric("luck"   , 24200),
 *                                              new SynchronizedLyric("it's"   , 25000),
 *                                              new SynchronizedLyric("tough"  , 25300),
 *                                              new SynchronizedLyric("so"     , 26900),
 *                                              new SynchronizedLyric("tough"  , 27200)};
 *    </pre>
 * </code>
 * </p>
 */
public class SynchronizedLyric
{
   // data members
   private String text;
   private int    timeStamp;

   /**
    * constructor
    * @param text        the text of the lyric, usually a syllable, word, phrase, or sentance.
    * @param timeStamp   location within the .mp3 audio file where the the synchronized lyric occurs.
    */
   public SynchronizedLyric(String text, int timeStamp)
   {
      if (timeStamp < 0)
         throw new IllegalArgumentException("The time stamp field in a synchronized lyric, " + text + ", in the " + FrameType.SYNCHRONIZED_LYRIC_TEXT.getId() + " frame contains an invalid value, " + timeStamp + ".  It must be >= 0.");

      this.text      = text == null ? "" : text;
      this.timeStamp = timeStamp;
   }

   /**
    * gets the lyric text.
    * @return the lyric.
    */
   public String getText()
   {
      return text;
   }

   /**
    * gets when the lyric is sung/spoken in the song.
    * @return when the lyric is sung/spoken in the song.
    */
   public int getTimeStamp()
   {
      return timeStamp;
   }

   /**
    * gets a string representation of the synchronized lyric.
    * @return a string representation of the synchronized lyric.
    */
   public String toString()
   {
      return "" + timeStamp + ": " + text;
   }
}
