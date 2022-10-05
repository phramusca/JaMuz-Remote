package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v24.FrameType;







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
 * A <i>music CD identifier</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#MUSIC_CD_IDENTIFIER MCDI} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to identify .mp3 songs that are ripped
 * from a CD, so that they can be identified in a database, such as CDDB.  The frame body consists of a binary dump of the CD's Table Of Contents (TOC) which is a header of 4 bytes
 * and then 8 bytes/track on the CD plus 8 bytes for the 'lead out' making a maximum of 804 bytes. The offset to the beginning of every track on the CD should be described with a four byte
 * absolute CD-frame address per track, and not with absolute time. This frame requires a present and valid {@link ID3v24FrameBodyTextInformation Track} frame, even if the CD's only got one track.
 * The <i>music CD identifier</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Music CD Identifier Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">cdTOC</td><td class="beaglebuddy">raw binary data of the CD's table of contents from which the .mp3 file was ripped.</td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>music CD identifier</i> frame in each tag.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyMusicCDIdentifier extends ID3v24FrameBody
{
   // data members
   private byte[] cdTOC;   // raw binary data



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>empty CD TOC (byte array of length 0)</li>
    * </ul>
    */
   public ID3v24FrameBodyMusicCDIdentifier()
   {
      this(new byte[1]);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param cdTOC   raw binary data of the cd's table of contents from which this .mp3 was ripped.
    */
   public ID3v24FrameBodyMusicCDIdentifier(byte[] cdTOC)
   {
      super(FrameType.MUSIC_CD_IDENTIFIER);

      setCdTOC(cdTOC);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a music cd identifier frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyMusicCDIdentifier(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.MUSIC_CD_IDENTIFIER, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      cdTOC = new byte[buffer.length];
      System.arraycopy(buffer, 0, cdTOC, 0, buffer.length);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the raw binary data of the cd's table of contents from which this .mp3 was ripped.
    * @return the raw binary data of the cd's table of contents from which this .mp3 was ripped.
    * @see #setCdTOC(byte[])
    */
   public byte[] getCdTOC()
   {
      return cdTOC;
   }

   /**
    * sets the raw binary data of the cd's table of contents from which this .mp3 was ripped.
    * @param cdTOC   the binary data of the cd table of contents.
    * @see #getCdTOC()
    */
   public void setCdTOC(byte[] cdTOC)
   {
      if (cdTOC == null || cdTOC.length == 0)
         throw new IllegalArgumentException("The cd Table of contents (cdTOC) field in the " + frameType.getId() + " frame may not be empty.");

      this.cdTOC = cdTOC;
      this.dirty = true;
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
         buffer = new byte[cdTOC.length];

         System.arraycopy(cdTOC, 0, buffer, 0, cdTOC.length);
         dirty = false;
      }
   }
   /**
    * gets a string representation of the <i>music CD identifier</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: music cd identifier\n");
      buffer.append("   cd table of contents: " + cdTOC.length   + " bytes\n");
      buffer.append("                         " + hex(cdTOC, 25) + "\n");

      return buffer.toString();
   }
}
