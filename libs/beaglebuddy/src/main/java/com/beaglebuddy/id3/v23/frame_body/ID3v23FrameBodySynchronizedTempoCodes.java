package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.enums.TimeStampFormat;




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
 * A <i>synchronized tempo codes</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SYNCHRONIZED_TEMPO_CODES <i>SYTC</i>} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to specify tempo changes in the .mp3 song.
 * A tempo is specified as the metronome setting, ie, the number of beats per minute.  Its range is from 2 - 510 bpm.  The <i>synchronized tempo codes</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Synchronized Tempo Codes Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.TimeStampFormat time stamp format}</td><td class="beaglebuddy">{@link #setTimeStampFormat(TimeStampFormat) units} of the <i>time stamp</i> field in the {@link TempoChange tempo changes}.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">tempoChanges                                                       </td><td class="beaglebuddy">list of {@link TempoChange tempo changes} within the .mp3 song.                                                            </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be only one <i>synchronized tempo codes</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodySynchronizedTempoCodes extends ID3v23FrameBody
{
   /**
    * used to indicate a tempo change within an .mp3 song.
    */
   public class TempoChange
   {
      // data members
      private int beatsPerMinute;    // 0 - 510
      private int timeStamp;         // point within the song where the tempo changes



      /**
       * constructor used to create a tempo change.
       * @param beatsPerMinute   0 indicates a beat-free time period. 1 indicates a single beat-stroke followed by a beat-free period.  2 - 510 indicates the metronome setting for the song, ie,
       *                         the number of beats per minute.
       * @param timeStamp        the timestamp in the song where the tempo change occurs.  The units of the timestamp are specified by the {@link com.beaglebuddy.id3.enums.TimeStampFormat time stamp format} field.
       */
      public TempoChange(int beatsPerMinute, int timeStamp)
      {
         if (beatsPerMinute < 0 || beatsPerMinute > 510)
            throw new IllegalArgumentException("The beats per minute field in the " + FrameType.SYNCHRONIZED_TEMPO_CODES.getId() + " contains an invalid value, " + beatsPerMinute + ". It must be 0 <= beats per minute <= 510.");
         if (timeStamp < 0)
            throw new IllegalArgumentException("The time stamp field in the " + FrameType.SYNCHRONIZED_TEMPO_CODES.getId() + " contains an invalid value, " + timeStamp + ".  It must be >= 0.");

         this.beatsPerMinute = beatsPerMinute;
         this.timeStamp      = timeStamp;
      }

      /**
       * gets the metronome setting for the song, ie, the number of beats per minute.
       * @return the number of beats per minute.
       */
      public int getBeatsPerMinute()
      {
         return beatsPerMinute;
      }

      /**
       * gets the timeStamp where the tempo change occurs in the song.
       * @return the time in the song where the tempo change occurs.
       */
      public int getTimeStamp()
      {
         return timeStamp;
      }

      /**
       * gets a string representation of the tempo change.
       * @return a string representation of the tempo change.
       */
      public String toString()
      {
         return "time stamp: " + timeStamp + " - " + beatsPerMinute + " bpm";
      }
   }

   // data members
   private TimeStampFormat   timeStampFormat;   // units of the temp change time stamps
   private List<TempoChange> tempoChanges;      // list of the tempo changes that occur in the song



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>time stamp format in ms</li>
    *    <li>no tempo changes</li>
    * </ul>
    */
   public ID3v23FrameBodySynchronizedTempoCodes()
   {
      this(TimeStampFormat.MS, new Vector<TempoChange>());
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param timeStampFormat   the units of the timestamps in the <i>timestamp</i> field of the tempo changes.
    * @param tempoChanges      list of tempo changes within the .mp3 song.
    */
   public ID3v23FrameBodySynchronizedTempoCodes(TimeStampFormat timeStampFormat, List<TempoChange> tempoChanges)
   {
      super(FrameType.SYNCHRONIZED_TEMPO_CODES);

      setTimeStampFormat(timeStampFormat);
      setTempoChanges   (tempoChanges);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a synchronized tempo codes frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodySynchronizedTempoCodes(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.SYNCHRONIZED_TEMPO_CODES, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      try
      {
         setTimeStampFormat(TimeStampFormat.valueOf(buffer[0]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to milliseconds so we can continue parsing the tag
         setTimeStampFormat(TimeStampFormat.MS);
      }
      int index     = 1;
      int bpm       = 0;
      int timeStamp = 0;

      tempoChanges = new Vector<TempoChange>();
      while ((index + 5) < buffer.length)
      {
         if (buffer[index] == (byte)0x00 && buffer[index] == (byte)0x01)
            bpm = buffer[index];
         else
            bpm = (buffer[index] == (byte)0xFF ? 255 + buffer[index + 1] : buffer[index]);
         index  += (buffer[index] == (byte)0xFF ? 2 : 1);
         timeStamp = ((buffer[index] & 0xFF ) << 24) + ((buffer[index + 1] & 0xFF) << 16) + ((buffer[index + 2] & 0xFF) << 8) + (buffer[index + 3] & 0xFF);
         tempoChanges.add(new TempoChange(bpm, timeStamp));
         index += 4;
      }
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * get the time stamp format.
    * @return the time stamp format.
    * @see #setTimeStampFormat(TimeStampFormat)
    */
   public TimeStampFormat getTimeStampFormat()
   {
      return timeStampFormat;
   }

   /**
    * sets the time stamp format.
    * @param timeStampFormat    the units of the time stamp.
    * @see #getTimeStampFormat()
    */
   public void setTimeStampFormat(TimeStampFormat timeStampFormat)
   {
      if (timeStampFormat == null)
         throw new IllegalArgumentException("The time stamp format field in the " + frameType.getId() + " frame may not be null.");

      this.dirty           = true;
      this.timeStampFormat = timeStampFormat;
   }

   /**
    * gets a list of the tempo changes that occur within the song.
    * @return a list of the tempo changes that occur within the song.
    * @see #setTempoChanges(List)
    */
   public List<TempoChange> getTempoChanges()
   {
      return tempoChanges;
   }

   /**
    * sets the list of the tempo changes that occur within the song.
    * @param  tempoChanges    a list of the tempo changes that occur within the song.
    * @see #getTempoChanges()
    */
   public void setTempoChanges(List<TempoChange> tempoChanges)
   {
      if (tempoChanges == null)
         throw new IllegalArgumentException("The tempo changes field in the " + frameType.getId() + " frame may not be empty.");

      this.tempoChanges = tempoChanges;
      this.dirty        = true;
   }

   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
      if (isDirty())
      {
         int numTempoChangeBytes = 0;
         for(TempoChange tempoChange : tempoChanges)
            numTempoChangeBytes += ((tempoChange.getBeatsPerMinute() < 256 ? 1 : 2) + 4);

         buffer = new byte[1 + numTempoChangeBytes];

         int    index  = 0;
         buffer[index] = (byte)timeStampFormat.getValue();
         index++;
         for(TempoChange tempoChange : tempoChanges)
         {
            int bpm = tempoChange.getBeatsPerMinute();
            if (bpm != 0xFF)
            {
               buffer[index] = (byte)bpm;
               index++;
            }
            else
            {
               buffer[index]     = (byte)0xFF;
               buffer[index + 1] = (byte)(bpm - 0xFF);
               index += 2;
            }
            System.arraycopy(intToBytes(tempoChange.getTimeStamp()), 0, buffer, index, 4);
         }
         dirty = false;    // data has already been saved
      }
   }

   /**
    * gets a string representation of the <i>synchronized tempo codes</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: synchronized tempo codes\n");
      buffer.append("   bytes............: " + this.buffer.length    + " bytes\n");
      buffer.append("                      " + hex(this.buffer, 22)  + "\n");
      buffer.append("   time stamp format: " + timeStampFormat       + "\n");
      buffer.append("   tempo changes....: " + tempoChanges.size()   + "\n");
      for(TempoChange tempoChange : tempoChanges)
         buffer.append(pad(24) + tempoChange + "\n");

      return buffer.toString();
   }
}
