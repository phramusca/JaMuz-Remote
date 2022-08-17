package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.enums.Language;
import com.beaglebuddy.id3.enums.TimeStampFormat;
import com.beaglebuddy.id3.pojo.SynchronizedLyric;



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
 * A <i>synchronized lyrics/text</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#SYNCHRONIZED_LYRIC_TEXT SYLT} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to include the words or lyrics in
 * the .mp3 file in sync with the audio. The most common use for this frame is to create Kareoke songs. It might also be used to describe events e.g. occurring on a stage or on the screen in
 * sync with the audio.
 * </p>
 * <p class="beaglebuddy">
 * A few considerations regarding whitespace characters. Whitespace separating words should mark the beginning of a new word, thus occurring in front of the first syllable of a new word.
 * This is also valid for newline characters. A syllable followed by a comma should not be broken apart with a sync (both the syllable and the comma should be before the sync).
 * </p>
 * <p class="beaglebuddy">
 * The <i>synchronized lyrics/text</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Synchronized Lyrics/Text Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}                </td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>description</i> and the <i>text</i>
 *                                                                                                                                                                   fields.                                                                                               </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.Language language}                </td><td class="beaglebuddy">{@link #setLanguage(Language) language} the <i>synchronized lyrics</i> are written in.                </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.TimeStampFormat time stamp format}</td><td class="beaglebuddy">{@link #setTimeStampFormat(TimeStampFormat) units} of the <i>time stamp</i> field
 *                                                                                                                                                                   in a synchronized lyric.                                                                              </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">{@link ContentType contentType}                                    </td><td class="beaglebuddy">{@link #setContentType(ContentType) predefined list} describing the type of lyric.                    </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">description                                                        </td><td class="beaglebuddy">description of the lyrics/text.                                                                       </td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">synchronizedLyrics                                                 </td><td class="beaglebuddy">list of synchronized lyrics, where each synchonized lyric consists of a syllable (or whatever size
 *                                                                                                                                                                   of text is considered to be convenient) and a time stamp denoting where in the .mp3 file it belongs.
 *                                                                                                                                                                   All synchronized lyrics should be sorted in chronological order.                                      </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>synchronized lyrics/text</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same <i>language</i> and
 * <i>description</i> fields.
 * </p>
 * <p>
 * example:
 * <code>
 *    <pre class="beaglebuddy">
 * import java.io.IOException;
 * import java.util.List;
 *
 * import com.beaglebuddy.mp3.MP3;
 * import com.beaglebuddy.id3.enums.v23.FrameType;
 * import com.beaglebuddy.id3.enums.SynchronizedLyric;
 * import com.beaglebuddy.id3.v23.ID3v23Frame;
 * import com.beaglebuddy.id3.v23.ID3v23Tag;
 * import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodySynchronizedLyricsText;
 *
 *
 * public class Kareoke
 * {
 *
 *    public static void main(String[] args)
 *    {
 *       try
 *       {
 *          // add Bon Jovi's "Livin on a Prayer" lyrics to the .mp3 so that they are displayed like a kareoke song by the mp3 player software
 *          MP3               mp3       = new MP3("c:\\music\\bon jovi\\livin on a prayer.mp3");
 *          ID3v23Tag         iD3v23Tag = mp3.getID3v23Tag();
 *          List&lt;ID3v23Frame&gt; frames    = iD3v23Tag.getFrames();
 *
 *          // if there was any invalid information (ie, frames) in the .mp3 file, then display the errors to the user
 *          if (mp3.hasErrors())
 *          {
 *             mp3.displayErrors(System.out);      // display the errors that were found
 *             mp3.save();                         // discard the invalid information (frames) and
 *          }                                      // save only the valid frames back to the .mp3 file
 *
 *          // break the lyrics into syllables
 *          SynchronizedLyric[] synchronizedLyrics = {new SynchronizedLyric("Tom-"   , 20350),
 *                                                    new SynchronizedLyric("my"     , 20400),
 *                                                    new SynchronizedLyric("used"   , 20625),
 *                                                    new SynchronizedLyric("to"     , 20700),
 *                                                    new SynchronizedLyric("work"   , 20800),
 *                                                    new SynchronizedLyric("on"     , 20900),
 *                                                    new SynchronizedLyric("the"    , 21000),
 *                                                    new SynchronizedLyric("docks"  , 21100),
 *                                                    new SynchronizedLyric("Un-"    , 22800),
 *                                                    new SynchronizedLyric("ions"   , 22900),
 *                                                    new SynchronizedLyric("been"   , 23000),
 *                                                    new SynchronizedLyric("on"     , 23100),
 *                                                    new SynchronizedLyric("strike" , 23200),
 *                                                    new SynchronizedLyric("He's"   , 23800),
 *                                                    new SynchronizedLyric("down"   , 23900),
 *                                                    new SynchronizedLyric("on"     , 24000),
 *                                                    new SynchronizedLyric("his"    , 24100),
 *                                                    new SynchronizedLyric("luck"   , 24200),
 *                                                    new SynchronizedLyric("it's"   , 25000),
 *                                                    new SynchronizedLyric("tough"  , 25300),
 *                                                    new SynchronizedLyric("so"     , 26900),
 *                                                    new SynchronizedLyric("tough"  , 27200)};
 *
 *          // add the synchronized lyrics frame to the ID3v2.3 tag
 *          ID3v23Frame frame = new ID3v23Frame(FrameType.SYNCHRONIZED_LYRIC_TEXT, new ID3v23FrameBodySynchronizedLyricsText(synchronizedLyrics));
 *          frames.add(frame);
 *          // save the ID3v2.3 tag to the .mp3 file
 *          mp3.save();
 *          System.out.println(mp3);
 *       }
 *       catch (IOException ex)
 *       {
 *          // an error occurred reading/saving the .mp3 file.
 *          // you may try to read it again to see if the error still occurs.
 *          ex.printStackTrace();
 *       }
 *    }
 * }
 *    </pre>
 * </code>
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see ID3v23FrameBodyUnsynchronizedLyrics
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodySynchronizedLyricsText extends ID3v23FrameBody
{
   /**
    * ID3v2.3 types of synchronized lyrics/text.
    */
   public enum ContentType
   {                                                                  /** other                                        */
      OTHER      ("other"                                         ),  /** lyrics                                       */
      LYRICS     ("lyrics"                                        ),  /** text transcription                           */
      TEXT       ("text transcription"                            ),  /** movement/part name (e.g. Adagio)             */
      MOVEMENT   ("movement/part name (e.g. \"Adagio\")"          ),  /** events (e.g. "Don Quijote enters the stage") */
      EVENT      ("events (e.g. \"Don Quijote enters the stage\")"),  /** chord (e.g. "Bb F Fsus")                     */
      CHORD      ("chord (e.g. \"Bb F Fsus\")"                    ),  /** trivia/'pop up' information                  */
      INFORMATION("trivia/'pop up' information"                   );

      // data members
      private String description;


      /**
       * constructor.
       * @param description   description of the synchronized lyric type.
       */
      private ContentType(String description)
      {
         this.description = description;
      }

      /**
       * gets the description of the synchronized lyric type.
       * @return the description of the synchronized lyric type.
       */
      public String getDescription()
      {
         return description;
      }

      /**
       * convert an integral value to its corresponding content type enum.
       * @return the ContentType enum corresponding to the integral value.
       * @param contentType  integral value to be converted to an ContentType enum.
       * @throws IllegalArgumentException   if the integral value does not correspond to a valid ContentType.
       */
      public static ContentType getContentType(byte contentType) throws IllegalArgumentException
      {
         for (ContentType c : ContentType.values())
            if (contentType == c.ordinal())
               return c;
         throw new IllegalArgumentException("Invalid synchronized lyric content type " + contentType + ".  It must be between " + OTHER.ordinal() + " and " + INFORMATION.ordinal() + ".");
      }

      /**
       * gets a string representation of the synchronized lyric type enum.
       * @return a string representation of the synchronized lyric type enum.
       */
      public String toString()
      {
         return "" + ordinal() + " - " + description;
      }
   }

   // data members
   private Encoding                encoding;             // charset used to encode the description and the text portion of the synchronized lyrics.
   private Language                language;             // ISO-639-2 language code
   private TimeStampFormat         timeStampFormat;      // units of the time stamps used in the synchronized lyrics
   private ContentType             contentType;          // type of synchronized lyrics/text
   private String                  description;          // description of the synchrozied lyrics
   private List<SynchronizedLyric> synchronizedLyrics;





   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>english language</li>
    *    <li>time stamp format in milliseconds</li>
    *    <li>lyrics content type</li>
    *    <li>"synchronized lyrics" description</li>
    *    <li>no synchronized lyrics</li>
    * </ul>
    */
   public ID3v23FrameBodySynchronizedLyricsText()
   {
      this(Encoding.ISO_8859_1, Language.ENG, TimeStampFormat.MS, ContentType.LYRICS, "synchronized lyrics",  new Vector<SynchronizedLyric>());
   }

   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>english language</li>
    *    <li>time stamp format in milliseconds</li>
    *    <li>lyrics content type</li>
    *    <li>"synchronized lyrics" description</li>
    * </ul>
    * @param synchronizedLyrics   array of synchronized lyrics in english.
    */
   public ID3v23FrameBodySynchronizedLyricsText(SynchronizedLyric[] synchronizedLyrics)
   {
      this(Encoding.ISO_8859_1, Language.ENG, TimeStampFormat.MS, ContentType.LYRICS, "synchronized lyrics",  synchronizedLyrics);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param encoding            character set used to encode the description and the lyric. see {@link #setEncoding(Encoding)}
    * @param language            ISO-639-2 language code indicating the language the lyric were written in.  see {@link #setLanguage(Language)}
    * @param timeStampFormat     units of the time stamp.  see {@link #setTimeStampFormat(TimeStampFormat)}
    * @param contentType         type of lyics/text.  see {@link #setContentType(ContentType)}
    * @param description         description of the lyrics/text.
    * @param synchronizedLyrics  array of synchronized lyrics/text.
    * @throws IllegalArgumentException  if the time stamps in the synchronized lyrics are not in ascending chronological order.
    */
   public ID3v23FrameBodySynchronizedLyricsText(Encoding encoding, Language language, TimeStampFormat timeStampFormat, ContentType contentType, String description, SynchronizedLyric[] synchronizedLyrics)
   {
      super(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      List<SynchronizedLyric> listSynchronizedLyrics = new Vector<SynchronizedLyric>();
      if (synchronizedLyrics != null && synchronizedLyrics.length != 0)
      {
         for(SynchronizedLyric synchronizedLyric : synchronizedLyrics)
               listSynchronizedLyrics.add(synchronizedLyric);
      }
      setEncoding          (encoding);
      setLanguage          (language);
      setTimeStampFormat   (timeStampFormat);
      setContentType       (contentType);
      setDescription       (description);
      setSynchronizedLyrics(listSynchronizedLyrics);
      dirty = true;         // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param encoding            character set used to encode the description and the lyric. see {@link #setEncoding(Encoding)}
    * @param language            ISO-639-2 language code indicating the language the lyric were written in.  see {@link #setLanguage(Language)}
    * @param timeStampFormat     units of the time stamp.  see {@link #setTimeStampFormat(TimeStampFormat)}
    * @param contentType         type of lyics/text.  see {@link #setContentType(ContentType)}
    * @param description         description of the lyrics/text.
    * @param synchronizedLyrics  list of synchronized lyrics/text.
    * @throws IllegalArgumentException  if the time stamps in the synchronized lyrics are not in ascending chronological order.
    */
   public ID3v23FrameBodySynchronizedLyricsText(Encoding encoding, Language language, TimeStampFormat timeStampFormat, ContentType contentType, String description, List<SynchronizedLyric> synchronizedLyrics)
   {
      super(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      setEncoding          (encoding);
      setLanguage          (language);
      setTimeStampFormat   (timeStampFormat);
      setContentType       (contentType);
      setDescription       (description);
      setSynchronizedLyrics(synchronizedLyrics);
      dirty = true;         // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a sychronized lyrics/text frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodySynchronizedLyricsText(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.SYNCHRONIZED_LYRIC_TEXT, frameBodySize);
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
      try
      {
         setTimeStampFormat(TimeStampFormat.valueOf(buffer[4]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to milliseconds so we can continue parsing the tag
         setTimeStampFormat(TimeStampFormat.MS);
      }
      try
      {
         setContentType(ContentType.getContentType(buffer[5]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to other so we can continue parsing the tag
         setContentType(ContentType.OTHER);
      }
      nullTerminatorIndex  = getNextNullTerminator(6, encoding);
      description          = new String(buffer, 6, nullTerminatorIndex-6, encoding.getCharacterSet()).trim();
      nullTerminatorIndex += encoding.getNumBytesInNullTerminator();
      Vector<SynchronizedLyric> lyrics = new Vector<SynchronizedLyric>();
      String                    text;
      int                       timeStamp;
      while (nullTerminatorIndex < buffer.length)
      {
         nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
         text                    = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex-nullTerminatorIndex, encoding.getCharacterSet()).trim();
         nullTerminatorIndex     = nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
         timeStamp               = ((buffer[nullTerminatorIndex] & 0xFF ) << 24) + ((buffer[nullTerminatorIndex + 1] & 0xFF) << 16) + ((buffer[nullTerminatorIndex + 2] & 0xFF) << 8) + (buffer[nullTerminatorIndex + 3] & 0xFF);
         nullTerminatorIndex    += 4;
         lyrics.add(new SynchronizedLyric(text, timeStamp));
      }
      setSynchronizedLyrics(lyrics);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the description and the synchronized lyrics.
    * @return the character encoding of the description and the synchronized lyrics.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the description and the synchronized lyrics.
    * @param encoding    the character set used to encode the description and the synchronized lyrics.  Only ISO 8859-1 and UTF-16 are allowed.
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
    * gets the {@link com.beaglebuddy.id3.enums.Language language} the synchronized lyrics are written in.
    * @return the {@link com.beaglebuddy.id3.enums.Language language} the synchronized lyrics are written in.
    * @see #setLanguage(Language)
    */
   public Language getLanguage()
   {
      return language;
   }

   /**
    * sets the {@link com.beaglebuddy.id3.enums.Language language} the synchronized lyrics are written in.
    * @param language   {@link com.beaglebuddy.id3.enums.Language language} the synchronized lyrics are written in.
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
    * gets the type of lyric.
    * @return the type of lyric.
    * @see #setContentType(ContentType)
    */
   public ContentType getContentType()
   {
      return contentType;
   }

   /**
    * sets the type of synchronized lyrics/text to one of the predefined types listed below.
    * <p>
    * <table class="beaglebuddy">
    *    <thead>
    *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Content Type</th><th class="beaglebuddy">Description</th></tr>
    *    </thead>
    *    <tbody>
    *       <tr><td class="beaglebuddy">0</td><td class="beaglebuddy">Other             </td><td class="beaglebuddy">other                                      </td></tr>
    *       <tr><td class="beaglebuddy">1</td><td class="beaglebuddy">Lyrics            </td><td class="beaglebuddy">song lyrics                                </td></tr>
    *       <tr><td class="beaglebuddy">2</td><td class="beaglebuddy">Text Transcription</td><td class="beaglebuddy">text transcription                         </td></tr>
    *       <tr><td class="beaglebuddy">3</td><td class="beaglebuddy">Movement          </td><td class="beaglebuddy">movement/part name (e.g. "Adagio")         </td></tr>
    *       <tr><td class="beaglebuddy">4</td><td class="beaglebuddy">Events            </td><td class="beaglebuddy">events (e.g. "Don Quijote enters the stage)</td></tr>
    *       <tr><td class="beaglebuddy">5</td><td class="beaglebuddy">Chord             </td><td class="beaglebuddy">chord  (e.g. "Bb F Fsus")                  </td></tr>
    *       <tr><td class="beaglebuddy">6</td><td class="beaglebuddy">Trivia            </td><td class="beaglebuddy">trivia/'pop up' information                </td></tr>
    *    </tbody>
    * </table>
    * </p>
    * @param contentType    one of the predefined values indicating the type of lyric.
    * @see #getContentType()
    */
   public void setContentType(ContentType contentType)
   {
      if (contentType == null)
         throw new IllegalArgumentException("The content type field in the " + frameType.getId() + " frame may not be empty.");

      this.contentType = contentType;
      this.dirty       = true;
   }

   /**
    * gets the description of the synchronized lyrics.
    * @return the description of the synchronized lyrics.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the synchronized lyrics.
    * @param description   the description of the synchronized lyrics.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description;
   }

   /**
    * gets the synchronized lyrics/text.
    * @return the synchronized lyrics/text.
    * @see #getSynchronizedLyrics()
    */
   public List<SynchronizedLyric> getSynchronizedLyrics()
   {
      return synchronizedLyrics;
   }

   /**
    * sets the synchronized lyrics/text.
    * @param synchronizedLyrics  the synchronized lyrics/text.
    * @see #getSynchronizedLyrics()
    */
   public void setSynchronizedLyrics(List<SynchronizedLyric> synchronizedLyrics)
   {
      int previousTimeStamp = -1;
      for(SynchronizedLyric synchronizedLyric : synchronizedLyrics)
      {
         if (synchronizedLyric.getTimeStamp() > previousTimeStamp)
            previousTimeStamp = synchronizedLyric.getTimeStamp();
         else
            throw new IllegalArgumentException("The time stamps in the synchronized lyrics in the " + frameType.getId() + " frame must be in ascending chronological order.");
      }
      this.synchronizedLyrics = synchronizedLyrics;
      this.dirty              = true;
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
         int numSynchronizedLyricBytes = 0;
         for(SynchronizedLyric synchronizedLyric : synchronizedLyrics)
            numSynchronizedLyricBytes += stringToBytes(encoding, synchronizedLyric.getText()).length + 4;

         byte[] languageBytes          = language.getCodeBytes();
         byte[] descriptionBytes       = stringToBytes(encoding, description);
         byte[] synchronizedLyricBytes = new byte[numSynchronizedLyricBytes];
         byte[] textBytes              = null;
         byte[] timeStampBytes         = null;
         int    index                  = 0;

         buffer = new byte[1 + languageBytes.length + 1 + 1 + descriptionBytes.length + synchronizedLyricBytes.length];

         buffer[index] = (byte)encoding.ordinal();
         index = 1;
         System.arraycopy(languageBytes        , 0, buffer, index, languageBytes.length);
         index += languageBytes.length;
         buffer[index] = (byte)timeStampFormat.getValue();
         index++;
         buffer[index] = (byte)contentType.ordinal();
         index++;
         System.arraycopy(descriptionBytes     , 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         for(SynchronizedLyric synchronizedLyric : synchronizedLyrics)
         {
            numSynchronizedLyricBytes += stringToBytes(encoding, synchronizedLyric.getText()).length + 4;
            textBytes = stringToBytes(encoding, synchronizedLyric.getText());
            System.arraycopy(textBytes         , 0, buffer, index, textBytes.length);
            index += textBytes.length;
            timeStampBytes = intToBytes(synchronizedLyric.getTimeStamp());
            System.arraycopy(timeStampBytes    , 0, buffer, index, timeStampBytes.length);
            index += timeStampBytes.length;
         }
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>synchronized lyrics/text</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: sychronized lyrics/text\n");
      buffer.append("   bytes..............: " + this.buffer.length   + " bytes\n");
      buffer.append("                        " + hex(this.buffer, 24) + "\n");
      buffer.append("   encoding...........: " + encoding             + "\n");
      buffer.append("   language...........: " + language             + "\n");
      buffer.append("   time stamp format..: " + timeStampFormat      + "\n");
      buffer.append("   content type.......: " + contentType          + "\n");
      buffer.append("   description........: " + description          + "\n");
      buffer.append("   synchronized lyrics: ");
      for(SynchronizedLyric synchronizedLyric : synchronizedLyrics)
         buffer.append(pad(24) + synchronizedLyric + "\n");

      return buffer.toString();
   }
}
