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
 * A <i>user defined text information</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#USER_DEFINED_TEXT_INFORMATION TXXX} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to include one-string text
 * information concerning the audio file in a similar way to the other {@link ID3v24FrameBodyTextInformation text information} frames. The <i>user defined text information</i> frame body
 * contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>User Defined Text Information Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v24.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>description</i> and <i>text</i> fields.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">description                                        </td><td class="beaglebuddy">description of the <i>text</i> field.                                                                     </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">text                                               </td><td class="beaglebuddy">the actual text.                                                                                          </td></tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * There may be more than one <i>user defined text information</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}, but only one with the same <i>description</i> field.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyUserDefinedTextInformation extends ID3v24FrameBody
{
   // data members
   private Encoding encoding;      // charset used to encode the description and the text fields.
   private String   description;   // description of the text
   private String   text;          // text




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>empty description</li>
    *    <li>empty text</li>
    * <ul>
    */
   public ID3v24FrameBodyUserDefinedTextInformation()
   {
      this(Encoding.ISO_8859_1, "", "");
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param encoding       character set used to encode the description.
    * @param description    description of the text.
    * @param text           text;
    */
   public ID3v24FrameBodyUserDefinedTextInformation(Encoding encoding, String description, String text)
   {
      super(FrameType.USER_DEFINED_TEXT_INFORMATION);

      setEncoding   (encoding);
      setDescription(description);
      setText       (text);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a user defined text information frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyUserDefinedTextInformation(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.USER_DEFINED_TEXT_INFORMATION, frameBodySize);
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
         setEncoding(Encoding.valueOf(buffer[0]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to ISO-8859-1 so we can continue parsing the tag
         setEncoding(Encoding.ISO_8859_1);
      }
      nullTerminatorIndex = getNextNullTerminator(1, encoding);
      description = new String(buffer, 1, nullTerminatorIndex - 1, encoding.getCharacterSet()).trim();
      nullTerminatorIndex += encoding.getNumBytesInNullTerminator();
      text = new String(buffer, nullTerminatorIndex, buffer.length - nullTerminatorIndex, encoding.getCharacterSet()).trim();
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the description and the text..
    * @return the character encoding of the description and the text..
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the description and the text..
    * @param encoding    the character set used to encode the description and the text. Only those supported by the {@link com.beaglebuddy.id3.enums.v24.Encoding} enum are allowed.
    * @see #getEncoding()
    */
   public void setEncoding(Encoding encoding)
   {
      if (encoding == null)
         throw new IllegalArgumentException("The encoding field in the " + frameType.getId() + " frame may not be null.");

      this.encoding = encoding;
      this.dirty    = true;
   }

   /**
    * gets the description of the text.
    * @return the description of the text.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the text.
    * @param description   the description of the text.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description;
   }

   /**
    * gets the text.
    * @return the text.
    * @see #setText(String)
    */
   public String getText()
   {
      return text;
   }

   /**
    * sets the text.
    * @param text   the text.
    * @see #getText()
    */
   public void setText(String text)
   {
      if (text == null || text.length() == 0)
         throw new IllegalArgumentException("The text field in the " + frameType.getId() + " frame may not be empty.");

      this.text  = text;
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
         byte[]  descriptionBytes = stringToBytes(encoding, description);
         byte[]  textBytes        = stringToBytes(encoding, text       );
         int     index            = 1;

         buffer = new byte[1 + descriptionBytes.length + textBytes.length];
         buffer[0] = (byte)encoding.ordinal();
         System.arraycopy(descriptionBytes, 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         System.arraycopy(textBytes       , 0, buffer, index, textBytes.length);

         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>user defined text information</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame's body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: user defined text information\n");
      buffer.append("   bytes......: " + this.buffer.length   + " bytes\n");
      buffer.append("                " + hex(this.buffer, 16) + "\n");
      buffer.append("   encoding...: " + encoding             + "\n");
      buffer.append("   description: " + description          + "\n");
      buffer.append("   text.......: " + text                 + "\n");

      return buffer.toString();
   }
}
