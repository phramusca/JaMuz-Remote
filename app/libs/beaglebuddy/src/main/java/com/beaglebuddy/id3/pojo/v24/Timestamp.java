package com.beaglebuddy.id3.pojo.v24;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * This timestamp class is a read only version supported by the <a href="http://id3.org/id3v2.4.0-structure">ID3v2.4 specification</a>.
 * It is used in the following Id3v2.4 frame types:
 * <ul>
 *    <li>{@link com.beaglebuddy.id3.enums.v24.FrameType#ENCODING_TIME}        </li>
 *    <li>{@link com.beaglebuddy.id3.enums.v24.FrameType#ORIGINAL_RELEASE_TIME}</li>
 *    <li>{@link com.beaglebuddy.id3.enums.v24.FrameType#RECORDING_TIME}       </li>
 *    <li>{@link com.beaglebuddy.id3.enums.v24.FrameType#RELEASE_TIME}         </li>
 *    <li>{@link com.beaglebuddy.id3.enums.v24.FrameType#TAGGING_TIME}         </li>
 * <ul>
 * </p>
 * <p class="beaglebuddy">
 * The timestamp fields are based on a subset of ISO 8601. When being as precise as possible the format of a time string is
 * yyyy-MM-ddTHH:mm:ss (year, "-", month, "-", day, "T", hour (out of 24), ":", minutes, ":", seconds), but the precision may be reduced by removing as many time indicators as wanted.
 * Hence, there are six valid timestamps:
 * <ul>
 *    <li>yyyy</li>
 *    <li>yyyy-MM</li>
 *    <li>yyyy-MM-dd</li>
 *    <li>yyyy-MM-ddTHH</li>
 *    <li>yyyy-MM-ddTHH:mm</li>
 *    <li>yyyy-MM-ddTHH:mm:ss</li>
 * </ul>
 * All time stamps are UTC. For durations, use the slash character as described in 8601, and for multiple non-contiguous dates, use multiple strings, if allowed by the frame definition.
 * </p>
 * @see <a href="http://www.iso.org/iso/home/standards/iso8601.htm">ISO 8601</a>
 * @see <a href="http://www.w3.org/TR/NOTE-datetime">supported subset of ISO 8601</a>
 */
public class Timestamp
{
   // data members
   private String timestamp;    // time stamp as a string


   /**
    * constructor.  constructs the time stamp yyyy.
    * @param year   four digit year
    * @throws IllegalArgumentException   if the year is less than or equal to 0.
    */
   public Timestamp(int year) throws IllegalArgumentException
   {
      setYear(year);
   }

   /**
    * constructor.  constructs the time stamp yyyy-MM.
    * @param year    four digit year
    * @param month   two digit month (1-12)
    * @throws IllegalArgumentException   if the year is less than or equal to 0 or if the month is not in the range 1-12.
    */
   public Timestamp(int year, int month) throws IllegalArgumentException
   {
      this(year);
      timestamp += "-";
      setMonth(month);
   }

   /**
    * constructor.  constructs the time stamp yyyy-MM-dd.
    * @param year    four digit year
    * @param month   two digit month (1-12)
    * @param day     two digit day   (1-31)
    * @throws IllegalArgumentException   if the year is less than or equal to 0, the month is not in the range 1-12, or if the day is not in the range 1-31.
    */
   public Timestamp(int year, int month, int day) throws IllegalArgumentException
   {
      this(year, month);
      timestamp += "-";
      setDay(day);
   }

   /**
    * constructor.  constructs the time stamp yyyy-MM-ddTHH.
    * @param year    four digit year
    * @param month   two digit month (1-12)
    * @param day     two digit day   (1-31)
    * @param hour    two digit hour  (1-24)
    * @throws IllegalArgumentException   if the year is less than or equal to 0, the month is not in the range 1-12, the day is not in the range 1-31, or if the hour is not in the range 1-24.
    */
   public Timestamp(int year, int month, int day, int hour) throws IllegalArgumentException
   {
      this(year, month, day);
      timestamp += "T";
      setHour(hour);
   }

   /**
    * constructor.  constructs the time stamp yyyy-MM-ddTHH:mm.
    * @param year    four digit year
    * @param month   two digit month  (1-12)
    * @param day     two digit day    (1-31)
    * @param hour    two digit hour   (1-24)
    * @param minute  two digit minute (1-59)
    * @throws IllegalArgumentException   if the year is less than or equal to 0, the month is not in the range 1-12, the day is not in the range 1-31, the hour is not in the range 1-24,
    *                                    or if minute is not in the range 1 - 59.
    */
   public Timestamp(int year, int month, int day, int hour, int minute) throws IllegalArgumentException
   {
      this(year, month, day, hour);
      timestamp += ":";
      setMinute(minute);
   }

   /**
    * constructor.  constructs the time stamp yyyy-MM-ddTHH:mm:ss.
    * @param year    four digit year
    * @param month   two digit month  (1-12)
    * @param day     two digit day    (1-31)
    * @param hour    two digit hour   (1-24)
    * @param minute  two digit minute (1-59)
    * @param second  two digit second (1-59)
    * @throws IllegalArgumentException   if the year is less than or equal to 0, the month is not in the range 1-12, the day is not in the range 1-31, the hour is not in the range 1-24,
    *                                    the minute is not in the range 1 - 59, or if the second is not in the range 1 - 59.
    */
   public Timestamp(int year, int month, int day, int hour, int minute, int second) throws IllegalArgumentException
   {
      this(year, month, day, hour, minute);
      timestamp += ":";
      setSecond(second);
   }

   /**
    * sets the year in the time stamp.
    * @param year   the year part of the time stamp.
    * throws IllegalArgumentException   if the year is less than or equal to 0.
    */
   private void setYear(int year) throws IllegalArgumentException
   {
      if (year <= 0)
         throw new IllegalArgumentException("Invalid timestamp year, " + year + ".  It must be greater than 0.");
      else if (year < 10)
         timestamp = "000";
      else if (year < 100)
         timestamp = "00";
      else if (year < 1000)
         timestamp = "0";
      timestamp += Integer.toString(year);
   }

   /**
    * sets the month in the time stamp.
    * @param month   the month part of the time stamp.
    * throws IllegalArgumentException   if the month is not in the range 1 - 12.
    */
   private void setMonth(int month) throws IllegalArgumentException
   {
      if (month < 1 || month > 12)
         throw new IllegalArgumentException("Invalid timestamp month, " + month + ".  It must be in the range 1 - 12.");
      else if (month < 10)
         timestamp += "0";
      timestamp += Integer.toString(month);
   }

   /**
    * sets the day in the time stamp.
    * @param day   the day part of the time stamp.
    * throws IllegalArgumentException   if the day is not in the range 1 - 31.
    */
   private void setDay(int day) throws IllegalArgumentException
   {
      if (day < 1 || day > 31)
         throw new IllegalArgumentException("Invalid timestamp day, " + day + ".  It must be in the range 1 - 31.");
      else if (day < 10)
         timestamp += "0";
      timestamp += Integer.toString(day);
   }

   /**
    * sets the hour in the time stamp.
    * @param hour   the hours part of the time stamp.
    * throws IllegalArgumentException   if the hour is not in the range 1 - 24.
    */
   private void setHour(int hour) throws IllegalArgumentException
   {
      if (hour < 1 || hour > 24)
         throw new IllegalArgumentException("Invalid timestamp hour, " + hour + ".  It must be in the range 1 - 24.");
      else if (hour < 10)
         timestamp += "0";
      timestamp += Integer.toString(hour);
   }

   /**
    * sets the minute in the time stamp.
    * @param minute   the minutes part of the time stamp.
    * throws IllegalArgumentException   if the minute is not in the range 1 - 59.
    */
   private void setMinute(int minute) throws IllegalArgumentException
   {
      if (minute < 1 || minute > 59)
         throw new IllegalArgumentException("Invalid timestamp minute, " + minute + ".  It must be in the range 1 - 59.");
      else if (minute < 10)
         timestamp += "0";
      timestamp += Integer.toString(minute);
   }

   /**
    * sets the second in the time stamp.
    * @param second   the seconds part of the time stamp.
    * throws IllegalArgumentException   if the second is not in the range 1 - 59.
    */
   private void setSecond(int second) throws IllegalArgumentException
   {
      if (second < 1 || second > 59)
         throw new IllegalArgumentException("Invalid timestamp second, " + second + ".  It must be in the range 1 - 59.");
      else if (second < 10)
         timestamp += "0";
      timestamp += Integer.toString(second);
   }

   /**
    * get a string representation of the time stamp.
    * @return a string representation of the time stamp.
    */
   @Override
   public String toString()
   {
      return timestamp;
   }
}
