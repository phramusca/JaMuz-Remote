package com.beaglebuddy.mpeg.enums;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * The list of source frequencies supported by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a>.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html" target="_blank">LAME Header Format</a>
 */
public enum SourceFrequency
{                                          /** 32 khz or smaller  */
   FREQ_32KHZ    ("32 kHz or smaller"),    /** 44.1 khz           */
   FREQ_44_1KHZ  ("44.1 kHz"         ),    /** 48 khz             */
   FREQ_48_KHZ   ("48 kHz"           ),    /** higher than 48 khz */
   FREQ_HIGHER   ("higher than 48kHz");

   // data members
   private String name;

   /**
    * constructor.
    * @param name    name of the source frequency.
    */
   private SourceFrequency(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the source frequency.
    * @return the name of the source frequency.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding LAME source frequency enum.
    * @return the LAME source frequency enum corresponding to the integral value.
    * @param frequency  integral value to be converted to a LAME source frequency enum.
    * @throws IllegalArgumentException   if the value is not a valid LAME source frequency.
    */
   public static SourceFrequency valueOf(int frequency) throws IllegalArgumentException
   {
      for (SourceFrequency f : SourceFrequency.values())
         if (frequency == f.ordinal())
            return f;
      throw new IllegalArgumentException("Invalid source frequency " + frequency + ".  It must be 0 <= source frequency <= " + FREQ_HIGHER.ordinal() + ".");
   }

   /**
    * gets a string representation of the source frequency enum.
    * @return a string representation of the source frequency enum.
    */
   public String toString()
   {
      return name;
   }
}
