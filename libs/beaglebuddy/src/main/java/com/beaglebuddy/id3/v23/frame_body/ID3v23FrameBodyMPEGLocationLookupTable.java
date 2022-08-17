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
 * An <i>MPEG location lookup table</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#MPEG_LOCATION_LOOKUP_TABLE MLLT} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to ncrease performance and accuracy of jumps within an MPEG audio file.
 * If anyone has a clue as to how this frame works (including the authors of the ID32.3 specification, please let me know).The <i>MPEG location lookup table</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>MPEG Location Lookup Table Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">framesBetweenReferences</td><td class="beaglebuddy">indicates how much the <i>frame counter</i> should increase for every reference. If this value is set to 2, then the first reference points to the second frame, the
 *                                                                                                                       2nd reference the 4th frame, the 3rd reference the 6th frame etc.                                                                                                     </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">bytesBetweenReferences </td><td class="beaglebuddy">similarly to the <i>framesBetweenReferences</i> field, this field points out how much the <i>byte counter</i> should increase (in bytes) for every reference.         </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">msBetweenReferences    </td><td class="beaglebuddy">similarly to the <i>framesBetweenReferences</i> field, this field points out how much the <i>ms counter</i> should increase (in milliseconds) for every reference.    </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">bitsForBytesDeviation  </td><td class="beaglebuddy">                                                                                                                                                                      </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">bitsForMSDeviation     </td><td class="beaglebuddy">                                                                                                                                                                      </td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">                       </td><td class="beaglebuddy">                                                                                                                                                                      </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyMPEGLocationLookupTable extends ID3v23FrameBody
{
   /**
    * The default constructor is called when creating a new frame.
    */
   public ID3v23FrameBodyMPEGLocationLookupTable()
   {
      super(FrameType.MPEG_LOCATION_LOOKUP_TABLE);

      dirty = true;
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an mpeg location lookup table frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyMPEGLocationLookupTable(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.MPEG_LOCATION_LOOKUP_TABLE, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
         dirty = false;    // data has already been saved
   }

   /**
    * gets a string representation of the <i>MPEG location lookup table</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: MPEG location lookup table\n");
      buffer.append("   bytes..........: " + this.buffer.length    + " bytes\n");
      buffer.append("                    " + hex(this.buffer, 20)  + "\n");

      return buffer.toString();
   }
}
