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
 * A <i>signature</i> frame body enables a group of frames, grouped with {@link com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyGroupIdentificationRegistration GRID} frames, to be signed.
 * Although signatures can reside inside the {@link com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyGroupIdentificationRegistration group id registration frame},
 * it might be desirable to store the signature elsewhere, e.g. in watermarks.
 * <table class="beaglebuddy">
 *    <caption><b>Seek Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">id       </td><td class="beaglebuddy">a number from 0 - 255 which uniquely identifies the group.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">signature</td><td class="beaglebuddy">binary signature of the group.                            </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <br/><br/>
 * <p class="beaglebuddy">
 * There may be more than one <i>signature</i> frame in in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}, but no two may have the same group id.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"   target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"  target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodySignature extends ID3v24FrameBody
{
   // data members
   private byte   id;         // the group id
   private byte[] signature;  // binary signature of the group




   /**
    * The constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>group id of 0</li>
    *    <li>empty signature</li>
    * </ul>
    * <br/><br/>
    */
   public ID3v24FrameBodySignature()
   {
      this((byte)0, new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param id         a number from 0 - 255 which uniquely identifies the group.
    * @param signature  the binary signature of the group.
    */
   public ID3v24FrameBodySignature(byte id, byte[] signature)
   {
      super(FrameType.SIGNATURE);

      setId(id);
      setSignature(signature);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a signature frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodySignature(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.SIGNATURE, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      id        = buffer[0];
      signature = new byte[buffer.length - 1];
      System.arraycopy(buffer, 1, signature, 0, signature.length);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the unique id of the group.
    * @return a number from 0 - 255 which uniquely identifies the group.
    * @see #setId(byte)
    */
   public byte getId()
   {
      return id;
   }

   /**
    * sets the unique id of the group.
    * @param id   a number from 0 - 255 which uniquely identifies the group
    * @see #getId()
    */
   public void setId(byte id)
   {
      this.id    = id;
      this.dirty = true;
   }

   /**
    * gets the binary signature of the group.
    * @return the binary signature of the group.
    * @see #setSignature(byte[])
    */
   public byte[] getSignature()
   {
      return signature;
   }

   /**
    * sets the binary signature of the group.
    * @param signature   the binary signature of the group.
    * @see #getSignature()
    */
   public void setSignature(byte[] signature)
   {
      this.signature = signature == null ? new byte[0] : signature;
      this.dirty     = true;
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
         buffer = new byte[1 + signature.length];

         buffer[0] = id;
         System.arraycopy(signature, 0, buffer, 1, signature.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>signature</i> frame body.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: signature\n");
      buffer.append("   bytes....: " + this.buffer.length   + " bytes\n");
      buffer.append("              " + hex(this.buffer, 14) + "\n");
      buffer.append("   id.......: " + id);
      buffer.append("   signature: " + signature.length     + " bytes\n");
      buffer.append("              " + hex(signature, 14)   + "\n");

      return buffer.toString();
   }
}
