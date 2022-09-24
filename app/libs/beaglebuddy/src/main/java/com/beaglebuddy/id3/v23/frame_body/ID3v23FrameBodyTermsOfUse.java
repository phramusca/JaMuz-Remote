package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.enums.Language;




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
 * A <i>terms of use</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#TERMS_OF_USE USER} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to include a brief description of the
 * terms of use and/or ownership of the .mp3 file.  More detailed information concerning the legal terms might be available through the
 * {@link ID3v23FrameBodyURLLink Copyright/Legal information} frame.  The <i>terms of use</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Terms of Use Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>text</i> field.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.Language language}</td><td class="beaglebuddy">language that the <i>text</i> field is written in.                                </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">text                                               </td><td class="beaglebuddy">the actual terms of use or ownership of the .mp3 file.                            </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>terms of use</i> frame in an {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tag}.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyTermsOfUse extends ID3v23FrameBody
{
   // data members
   private Encoding encoding;      // charset used to encode the text field.
   private Language language;      // ISO-639-2 language code
   private String   text;          // text




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>english language</li>
    *    <li>empty text</li>
    * </ul>
    */
   public ID3v23FrameBodyTermsOfUse()
   {
      this(Encoding.ISO_8859_1, Language.ENG, " ");
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param encoding   character set used to encode the terms of use.
    * @param language   ISO-639-2 language code.
    * @param text       text.
    */
   public ID3v23FrameBodyTermsOfUse(Encoding encoding, Language language, String text)
   {
      super(FrameType.TERMS_OF_USE);

      setEncoding(encoding);
      setLanguage(language);
      setText    (text);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * <br/><br/>
    * @param inputStream    input stream pointing to a terms of use frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyTermsOfUse(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.TERMS_OF_USE, frameBodySize);
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
      try
      {
         setLanguage(Language.getLanguage(new String(buffer, 1, 3, Encoding.ISO_8859_1.getCharacterSet())));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to english so we can continue parsing the tag
         setLanguage(Language.ENG);
      }
      text = new String(buffer, 4, buffer.length - 4, encoding.getCharacterSet()).trim();
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the text.
    * @return the character encoding of the text.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the text.
    * @param encoding    the character set used to encode the text.  Only those supported by the {@link com.beaglebuddy.id3.enums.v23.Encoding} enum are allowed.
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
    * gets the {@link com.beaglebuddy.id3.enums.Language language} the terms of user are written in.
    * @return the {@link com.beaglebuddy.id3.enums.Language language} the terms of user are written in.
    * @see #setLanguage(Language)
    */
   public Language getLanguage()
   {
      return language;
   }

   /**
    * sets the {@link com.beaglebuddy.id3.enums.Language language} the terms of user are written in.
    * @param language   {@link com.beaglebuddy.id3.enums.Language language} the terms of user are written in.
    * @see #getLanguage()
    */
   public void setLanguage(Language language)
   {
      if (language == null)
         throw new IllegalArgumentException("The language field in the " + frameType.getId() + " frame may not be null.");

      this.dirty    = true;
      this.language = language;
   }

   /**
    * gets the terms of use or ownership of the .mp3 file.
    * @return the terms of use or ownership of the .mp3 file.
    * @see #setText(String)
    */
   public String getText()
   {
      return text;
   }

   /**
    * sets the terms of use or ownership of the .mp3 file.
    * @param text   the terms of use or ownership of the .mp3 file.
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
         byte[] languageBytes = language.getCodeBytes();
         byte[] textBytes     = stringToBytes(encoding, text);
         int    index         = 0;

         buffer = new byte[1 + languageBytes.length + textBytes.length];

         buffer[index] = (byte)encoding.ordinal();
         index = 1;
         System.arraycopy(languageBytes, 0, buffer, index, languageBytes.length);
         index += languageBytes.length;
         System.arraycopy(textBytes    , 0, buffer, index, textBytes.length);

         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>terms of use</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: terms of use\n");
      buffer.append("   bytes.....: " + this.buffer.length   + " bytes\n");
      buffer.append("               " + hex(this.buffer, 15) + "\n");
      buffer.append("   encoding..: " + encoding             + "\n");
      buffer.append("   language..: " + language             + "\n");
      buffer.append("   text......: " + text                 + "\n");

      return buffer.toString();
   }
}
