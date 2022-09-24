package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;

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
 * An <i>position synchronization</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#POSITION_SYNCHRONIZATION POSS} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to
 * deliver information to the listener on how far into the audio stream he picked up.  In effect, it states the time offset of the first frame in the stream.
 * The <i>position synchronization</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Position Synchronization Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.TimeStampFormat time stamp format}</td><td class="beaglebuddy">{@link #setTimeStampFormat(TimeStampFormat) units} of the <i>position</i> field.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">position                                                           </td><td class="beaglebuddy">the position in the audio the listener starts to receive, i.e. the beginning of the next frame.</td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <table class="beaglebuddy">
 * There may only be one <i>position synchronization</i> frame in the tag.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyPositionSynchronization extends ID3v23FrameBody
{
   // data members
   private TimeStampFormat timeStampFormat;   // units of the time stamp
   private int             position;          // position within song




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>time stamp format in milliseconds</li>
    *    <li>position 0</li>
    * <ul>
    */
   public ID3v23FrameBodyPositionSynchronization()
   {
      this(TimeStampFormat.MS, 0);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param timeStampFormat   units of the time stamp.  see {@link #setTimeStampFormat(TimeStampFormat)}
    * @param position          position within song
    */
   public ID3v23FrameBodyPositionSynchronization(TimeStampFormat timeStampFormat, int  position)
   {
      super(FrameType.POSITION_SYNCHRONIZATION);

      setTimeStampFormat(timeStampFormat);
      setPosition       (position);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a position synchronization frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading in the frame body.
    */
   public ID3v23FrameBodyPositionSynchronization(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.POSITION_SYNCHRONIZATION, frameBodySize);
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
      position        = ((buffer[1] & 0xFF) << 24) + ((buffer[2] & 0xFF) << 16) + ((buffer[3] & 0xFF) << 8) + (buffer[4] & 0xFF);
      dirty           = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the position within the song.
    * @return the position within the song.
    * @see #setPosition(int)
    */
   public int getPosition()
   {
      return position;
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
    * sets the position within the song.
    * @param position    the position within the song.
    * @throws IllegalArgumentException   if the position is less than 0.
    * @see #getPosition()
    */
   public void setPosition(int position) throws IllegalArgumentException
   {
      if (position < 0)
         throw new IllegalArgumentException("The position field in the " + frameType.getId() + " frame contains an invalid value, " + position + ".  It must be >= 0.");

      this.dirty    = true;
      this.position = position;
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
         buffer = new byte[5];

         buffer[0] = (byte)timeStampFormat.getValue();
         System.arraycopy(intToBytes(position), 0, buffer, 1, 4);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>position synchronization</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: sychronized lyrics/text\n");
      buffer.append("   time stamp format: " + timeStampFormat + "\n");
      buffer.append("   position.........: " + position        + "\n");

      return buffer.toString();
   }
}
