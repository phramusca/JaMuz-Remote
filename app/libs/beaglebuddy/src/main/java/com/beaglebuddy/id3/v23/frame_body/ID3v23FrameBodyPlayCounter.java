package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v23.FrameType;




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
 * A <i>play counter</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#PLAY_COUNTER PCNT} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to count the number of times an .mp3 file has
 * been played.  The value is increased by one every time the file begins to play.  The <i>play counter</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Play Counter Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">counter</td><td class="beaglebuddy">number of times the .mp3 file has been played.</td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 *  There may only be one <i>play counter</i> frame in each tag.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyPlayCounter extends ID3v23FrameBody
{
   // data members
   private int counter;       // number of times the song has been played




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>counter at 0</li>
    * </ul>
    */
   public ID3v23FrameBodyPlayCounter()
   {
      this(0);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param counter   the number of times the song has been played.
    */
   public ID3v23FrameBodyPlayCounter(int counter)
   {
      super(FrameType.PLAY_COUNTER);

      setCounter(counter);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a play counter frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyPlayCounter(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.PLAY_COUNTER, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      if (buffer.length == 4)
      {
         counter = ((buffer[3] & 0xFF ) << 24) + ((buffer[2] & 0xFF) << 16) + ((buffer[1] & 0xFF) << 8) + (buffer[0] & 0xFF);
      }
      else
      {
//       int numBytesInCounter = frameBodySize;
         throw new IllegalArgumentException("The size of the counter field in the " + frameType.getId() + " can not be " + buffer.length + " bytes.  It must be 4 bytes.");
      }
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the number of times the song has been played.
    * @return the number of times the song has been played.
    * @see #setCounter(int)
    */
   public int getCounter()
   {
     return counter;
   }

   /**
    * sets the number of times the song has been played.
    * @param counter    the number of times the song has been played.
    * @see #getCounter()
    */
   public void setCounter(int counter)
   {
      if (counter < 0)
         throw new IllegalArgumentException("The counter field in the " + frameType.getId() + " frame contains an invalid value, " + counter + ".  It must be >= 0.");

      this.counter = counter;
      this.dirty   = true;
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
         buffer = new byte[4];
         System.arraycopy(intToBytes(counter), 0, buffer, 0, 4);

         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>play counter</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: play counter\n");
      buffer.append("   bytes...........: " + this.buffer.length   + " bytes\n");
      buffer.append("                     " + hex(this.buffer, 21) + "\n");
      buffer.append("   num times played: " + counter              + "\n");

      return buffer.toString();
   }
}
