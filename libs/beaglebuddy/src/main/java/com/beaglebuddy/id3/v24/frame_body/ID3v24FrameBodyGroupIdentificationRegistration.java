package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v24.Encoding;
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
 * An <i>group identification registration</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#GROUP_IDENTIFICATION_REGISTRATION GRID} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to group otherwise unrelated frames.
 * To identify which frames belongs to a set of frames, a group identifier must be registered in the tag with this frame. The  <i>group identification registration</i> frame body contains the
 * following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Group Identification Registration Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">ownerId     </td><td class="beaglebuddy">URL containing an e-mail address, or a link to a location where an e-mail address can be found, that belongs
 *                                                                                                           to the organization responsible for this specific encrypted audio file. Questions regarding the grouping should
 *                                                                                                           be sent to the indicated e-mail address.                                                                        </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">groupSymbol</td><td class="beaglebuddy">id which uniquely identifies a group and with which all associated frames will references. Values below 0x80
 *                                                                                                           are reserved.                                                                                                   </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">data       </td><td class="beaglebuddy">This optional field contains group specific data, such as a digital signature.  If not used, it will contain
 *                                                                                                           a byte array of length 0.                                                                                       </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>group identification registration</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}, but only one containing the same
 * <i>owner</i> and only one containing the same <i>group symbol</i>. The group symbol must be used somewhere in another frame in the tag.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyGroupIdentificationRegistration extends ID3v24FrameBody
{
   // data members
   private String ownerId;       // e-mail address, or a link to a location where an e-mail address can be found, that belongs to the organization responsible for this grouping.
   private byte   groupSymbol;   // id of the group
   private byte[] data;          // group specific data, such as a digital signature.   this field is optional and may be empty.




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>empty owner id</li>
    *    <li>0x80 group symbol</li>
    *    <li>no data (byte array of length 0)</li>
    * </ul>
    */
   public ID3v24FrameBodyGroupIdentificationRegistration()
   {
      this("", (byte)0x80, new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param ownerId       e-mail address, or a link to a location where an e-mail address can be found, that belongs to the organization responsible for this grouping.
    * @param groupSymbol   id of the group.
    * @param data          group specific data, such as a digital signature.  this field is optional and may be empty.
    */
   public ID3v24FrameBodyGroupIdentificationRegistration(String ownerId, byte groupSymbol, byte[] data)
   {
      super(FrameType.GROUP_IDENTIFICATION_REGISTRATION);

      setOwnerId    (ownerId);
      setGroupSymbol(groupSymbol);
      setData       (data);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a group identification registration frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyGroupIdentificationRegistration(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.GROUP_IDENTIFICATION_REGISTRATION, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      nullTerminatorIndex = getNextNullTerminator(0, Encoding.ISO_8859_1);
      ownerId = new String(buffer, 0, nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim();
      nullTerminatorIndex++;
      groupSymbol = buffer[nullTerminatorIndex];
      nullTerminatorIndex++;
      data = new byte[buffer.length - nullTerminatorIndex];
      System.arraycopy(buffer, nullTerminatorIndex, data, 0, data.length);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the owner id of the frame.
    * @return the owner id of the frame.
    * @see #setOwnerId(String)
    */
   public String getOwnerId()
   {
      return ownerId;
   }

   /**
    * sets the owner id, which is a URL containing an e-mail address, or a link to a location where an e-mail address can be found, that belongs to the
    * organization responsible for the frame. Questions regarding the frame should be sent to the indicated e-mail address.
    * @param ownerId    the id of the owner of the data.
    * @see #getOwnerId()
    */
   public void setOwnerId(String ownerId)
   {
      if (ownerId == null || ownerId.length() == 0)
         throw new IllegalArgumentException("The owner id field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty   = true;
      this.ownerId = ownerId;
   }

   /**
    * gets the group symbol.
    * @return the group symbol.
    * @see #setGroupSymbol(byte)
    */
   public byte getGroupSymbol()
   {
      return groupSymbol;
   }

   /**
    * sets the group symbol.
    * @param groupSymbol   the group symbol.
    * @see #getGroupSymbol()
    */
   public void setGroupSymbol(byte groupSymbol)
   {
      if (groupSymbol < 0x80)
         throw new IllegalArgumentException("The group symbol field in the " + frameType.getId() + " frame contains an invalid value, " + groupSymbol + ".\nIt may not be less than 0x80, as those values are reserved.");

      this.dirty       = true;
      this.groupSymbol = groupSymbol;
   }

   /**
    * gets the group specific data, such as a digital signature.  If not used, it will contain a byte[] array of length 0.
    * @return the group specific data.
    * @see #setData(byte[])
    */
   public byte[] getData()
   {
      return data;
   }

   /**
    * sets the group specific data.  this field is optional and may be empty.
    * @param data   the group specific data.
    * @see #getData()
    */
   public void setData(byte[] data)
   {
      this.dirty = true;
      this.data  = (data == null ? new byte[0] : data);
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
         byte[] ownerIdBytes = stringToBytes(Encoding.ISO_8859_1, ownerId);
         int    index        = 0;

         buffer = new byte[ownerIdBytes.length + 1 + data.length];

         System.arraycopy(ownerIdBytes, 0, buffer, 0, ownerIdBytes.length);
         index = ownerIdBytes.length;
         buffer[index] = groupSymbol;
         System.arraycopy(data, 0, buffer, index + 1, data.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>group identification registration<i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: group identification registration\n");
      buffer.append("   bytes.....: " + this.buffer.length   + " bytes\n");
      buffer.append("               " + hex(this.buffer, 15) + "\n");     // todo: fix this - object could be huge byte array
      buffer.append("   owner id..: " + ownerId              + "\n");
      buffer.append("   group id..: " + groupSymbol          + "\n");
      buffer.append("   group data: " + data.length          + " bytes\n");

      return buffer.toString();
   }
}
