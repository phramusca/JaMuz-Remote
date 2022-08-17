package com.beaglebuddy.id3.enums;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * ID3v2.x time stamp units.
 * Numerous frames defined in the ID3v2.x specification contain a timestamp field, indicating when something happened in the .mp3 song.
 * The units of these timestamp fields can be either in MPEG frames or in millisconds (ms).
 */
public enum TimeStampFormat
{                                                                        /** absolute time, 32 bit sized, using MPEG frames as units  */
   MPEG(1, "absolute time, 32 bit sized, using MPEG frames as units" ),  /** absolute time, 32 bit sized, using milliseconds as units */
   MS  (2, "absolute time, 32 bit sized, using milliseconds as units");

   // data members
   private String description;
   private int    value;

   /**
    * constructor.
    * @param value        enum intgral value of the time stamp (since Java enums can not start at a number besides 0).
    * @param description  description of the time stamp format.
    */
   private TimeStampFormat(int value, String description)
   {
      this.value       = value;
      this.description = description;
   }

   /**
    * the integral value of the time stamp format.  Use this instead of the enum's ordinal() method.
    * @return the integral value of the time stamp format.
    */
   public int getValue()
   {
      return value;
   }

   /**
    * gets the description of the time stamp format.
    * @return the description of the time stamp format.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * converts an integral value to its corresponding time stamp format enum where 1 == MPEG frames and 2 = milliseconds.
    * @return a time stamp format enum corresponding to the integral value.
    * @param timeStampFormat  integral value to be converted to a time stamp format enum.
    * @throws IllegalArgumentException   if the value is not a valid time stamp format.
    */
   public static TimeStampFormat valueOf(byte timeStampFormat) throws IllegalArgumentException
   {
      for (TimeStampFormat t : TimeStampFormat.values())
         if (timeStampFormat == t.getValue())
            return t;
      throw new IllegalArgumentException("Invalid time stamp format " + timeStampFormat + ".  It must be either 1 or 2.");
   }

   /**
    * gets a string representation of the time stamp format enum.
    * @return a string representation of the time stamp format enum.
    */
   public String toString()
   {
      return "" + value + " - " + description;
   }
}
