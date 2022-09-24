package com.beaglebuddy.lyrics3;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.id3.v1.ID3v1Tag;




/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * This class provides methods for reading the optional <a href="http://id3.org/Lyrics3v2" target="_blank">Lyrics3v2</a> tag, which is found at the end of an .mp3 file
 * after the {@link com.beaglebuddy.mpeg.MPEGFrame mpeg audio}, data and before the {@link com.beaglebuddy.id3.v1.ID3v1Tag ID3v1 tag} as shown below.  It is an obsolete
 * tag that really should just be removed from your .mp3 files.  See the
 * <a href="http://www.beaglebuddy.com/content/pages/more_sample_code/CleanMP3Files.java" target="_blank">CleanMP3Files.java</a> file provided in the
 * <a href="http://www.beaglebuddy.com/content/pages/more_sample_code/file_list.html"     target="_blank">sample code</a> to see how this is done.
 * </p>
 * <span class="beaglebuddy_warn_bold">note: </span>
 * <span class="beaglebuddy_warn"     >The documentation at </span><a href="http://id3.org/Lyrics3v2" target=_blank">http://id3.org/Lyrics3v2</a>
 * <span class="beaglebuddy_warn">contains the following errors:
 *    <ul>
 *       <li>the size part of a field is 5 characters long, not 6</li>
 *       <li>the indications field is only 2 characters long, not 3</li>
 *       <li>CRC is missing from the list of defined fields</li>
 *    </ul>
 * </span>
 * <br/>
 * <p>
 * <table border="0">
 *   <tbody>
 *      <tr>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/mp3_format_ID3v2.3.gif" height="550" width="330" alt="mp3 format containing an ID3v2.3 tag" usemap="#id3v23_map"/>
 *          </td>
 *          <td> &nbsp; &nbsp; &nbsp; </td>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/lyrics3v2_format.jpg" height="320" width="320" alt="Lyrics3v2 Tag format" usemap="#lyrics3v2_map"/>
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 * <map name="lyrics3v2_map">
 *    <area shape="rect" coords="  8,  69, 190,  92" href="Lyrics3v2Tag.html#isLyricsPresent()"   alt="Lyrics3v2.isLyricsPresent()"/>
 *    <area shape="rect" coords="  8,  93, 190, 114" href="Lyrics3v2Tag.html#getAuthorName()"     alt="Lyrics3v2.getAuthorName()"/>
 *    <area shape="rect" coords="  8, 115, 190, 137" href="Lyrics3v2Tag.html#getCRC()"            alt="Lyrics3v2.getCRC()"/>
 *    <area shape="rect" coords="  8, 138, 190, 161" href="Lyrics3v2Tag.html#getAlbumName()"      alt="Lyrics3v2.getAlbumName()"/>
 *    <area shape="rect" coords="  8, 162, 190, 184" href="Lyrics3v2Tag.html#getArtistName()"     alt="Lyrics3v2.getArtistName()"/>
 *    <area shape="rect" coords="  8, 185, 190, 207" href="Lyrics3v2Tag.html#getTrackTitle()"     alt="Lyrics3v2.getTrackTitle()"/>
 *    <area shape="rect" coords="  8, 208, 190, 230" href="Lyrics3v2Tag.html#getImageLink()"      alt="Lyrics3v2.getImageLink()"/>
 *    <area shape="rect" coords="  8, 231, 190, 253" href="Lyrics3v2Tag.html#getAdditionalInfo()" alt="Lyrics3v2.getAddtionalInfo()"/>
 *    <area shape="rect" coords="  8, 254, 190, 276" href="Lyrics3v2Tag.html#getLyrics()"         alt="Lyrics3v2.getLyrics()"/>
 * </map>
 * <map name="id3v23_map">
 *    <area shape="rect" coords=" 230, 145, 300, 165" href="../../id3/v23/ID3v23Tag.html"                 alt="ID3v2.3 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="../../id3/v23/ID3v23TagHeader.html"           alt="ID3v2.3 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="../../id3/v23/ID3v23TagExtendedHeader.html"   alt="ID3v2.3 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="../../id3/v23/ID3v23Frame.html"               alt="ID3v2.3 Frame""/>
 *    <area shape="rect" coords="   6, 287, 198, 374" href="../../mpeg/MPEGFrame.html"                    alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 375, 198, 425" href="Lyrics3v2Tag.html"                            alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 426, 198, 479" href="../../ape/APETag.html"                        alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 480, 198, 530" href="../../id3/v1/ID3v1Tag.html"                   alt="ID3V1 Tag"/>
 * </map>
 * </p>
 * <pre class="beaglebuddy">
 * <code>
 * import java.io.IOException;
 * import com.beaglebuddy.mp3.MP3;
 * import com.beaglebuddy.lyrics3.Lyrics3v2Tag;
 *
 * public class Lyrics3v2Example
 * {
 *    public static void main(String[] args)
 *    {
 *       try
 *       {
 *          MP3 mp3 = new MP3("c:/mp3/Wild Cat.mp3");
 *
 *          if (mp3.hasLyrics3v2Tag())             // if the mp3 file has a Lyrics3v2 tag
 *          {                                      // then display the data in it
 *             Lyrics3v2Tag lyrics3v2Tag = mp3.getLyrics3v2Tag();
 *             System.out.println("mp3 contains a Lyrics3v2 tag");
 *             System.out.println(lyrics3v2Tag);
 *
 *             System.out.println("artist..: " + lyrics3v2Tag.getArtistName());
 *             System.out.println("album...: " + lyrics3v2Tag.getAlbumName());
 *             System.out.println("author..: " + lyrics3v2Tag.getAuthorName());
 *             System.out.println("track...: " + lyrics3v2Tag.getTrackTitle());
 *             System.out.println("add info: " + lyrics3v2Tag.getAdditionalInfo());
 *             System.out.println("image...: " + lyrics3v2Tag.getImageLink());
 *             System.out.println("lyrics..: " + lyrics3v2Tag.getLyrics());
 *          }
 *       }
 *       catch (IOException ex)
 *       {
 *          System.out.println("An error occurred while reading the mp3 file.");
 *       }
 *    }
 * }
 * </code>
 * </pre>
 * @see Lyrics3v1Tag
 * @see <a href="http://id3.org/Lyrics3"   target="_blank">Lyrics3v1 Spec</a>
 * @see <a href="http://id3.org/Lyrics3v2" target="_blank">Lyrics3v2 Spec</a>
 */
public class Lyrics3v2Tag
{
   // class mnemonics
   private static final String ID_BEGIN                = "LYRICSBEGIN";
   private static final String ID_END                  = "LYRICS200";
   private static final String ID_INDICATIONS          = "IND";
   private static final String ID_LYRICS               = "LYR";
   private static final String ID_ADDITIONAL_INFO      = "INF";
   private static final String ID_AUTHOR_NAME          = "AUT";
   private static final String ID_EXTENDED_ALBUM_NAME  = "EAL";
   private static final String ID_EXTENDED_ARTIST_NAME = "EAR";
   private static final String ID_EXTENDED_TRACK_TITLE = "ETT";
   private static final String ID_IMAGE_LINK           = "IMG";
   private static final String ID_CRC                  = "CRC";
   private static final String CHARACTER_SET           = "ISO-8859-1";
// private static final char   CHAR_ZERO               = '0';
   private static final char   CHAR_ONE                = '1';

   private static final int    FIELD_ID_SIZE           = 3;    // each Lyrics3v2 fields start with a 3 character id
   private static final int    FIELD_SIZE_SIZE         = 5;    // the 3 character id is then followed by a 5 character numerical size field
   private static final int    END_FIELD_SIZE_SIZE     = 6;    // the end id field has a 6 character
   private static final int    END_FIELD_SIZE          = END_FIELD_SIZE_SIZE + ID_END.length();

   // data members
   private int     filePosition;                      // position within the .mp3 file where the Lyrics3v2 tag occurs
   private int     size;                              // size of the Lyrics3v2 tag including the end id
   private String  beginId;                           // unique id indicating that a Lyrics3v2 tag is present in the .mp3 file
   private boolean lyricsPresent;                     // whether lyrics are present
   private boolean timestampPresent;                  // whether the lyrics contain timestamps
   private boolean randomTrackSelectionAllowed;       // whether random track selection is allowed
   private String  lyrics;
   private String  additionalInfo;
   private String  authorName;
   private String  albumName;
   private String  artistName;
   private String  trackTitle;
   private String  imageLink;
   private String  crc;
   private int     endSize;                           // size of the Lyrics3v2 tag not including the 6 characters used to store the size or the end id
                                                      // that is, end size = Lyrics3v2 - 15
   private String endId;                              // ending id indicating that a Lyrics3v2 tag is present in the .mp3 file



   /**
    * constructor used to read in a Lyrics3v2 tag from an .mp3 file.
    * @param file   random access file to read in the Lyrics3v2 tag from an .mp3 file.
    * @throws IOException      if there is an error while reading the Lyrics3v2 tag.
    * @throws ParseException   if a Lyrics3v2 tag can not be found or if an invalid value is detected while parsing the Lyrics3v2 tag.
    */
   public Lyrics3v2Tag(RandomAccessFile file) throws IOException, ParseException
   {
      // the Lyrics3v2Tag is found right before the ID3v1 tag at the end of the .mp3 file
      // we're looking for the end of the Lyrics3v2 tag, which contains the size of the tag
      filePosition = (int)(file.length() - ID3v1Tag.TAG_SIZE - Lyrics3v2Tag.END_FIELD_SIZE);

      // look for the end id
      file.seek(filePosition);
      try
      {
         endSize = findEnd(file);
      }
      catch (ParseException ex)
      {
         // in a perfect world, the Lyrics3v2 tag is found right before the ID3v1 tag
         // but sometimes people remove ID3v1 tag without realizing that a Lyrics3v2 is also present in their .mp3's
         // so let's check at the end of the file as well
         filePosition += ID3v1Tag.TAG_SIZE;
         file.seek(filePosition);
         endSize = findEnd(file);
      }
      size = endSize + END_FIELD_SIZE;

      // go to the beginning of the Lyrics3v2 tag and parse the beginning id
      filePosition -= endSize;
      file.seek(filePosition);

      // read and parse the bytes for the beginning id
      byte[] bytes = read(file, ID_BEGIN.length());

      beginId = new String(bytes);
      if (!beginId.equals(ID_BEGIN))
         throw new ParseException("Invalid id, " + beginId + ", found in the Lyrics3v2 tag.", bytes);

      // loop through the fields in the Lyrics3v2 tag and parse them
      String fieldId      = null;
      int    numBytesRead = 0;

      do
      {
         fieldId = new String(read(file, FIELD_ID_SIZE));

              if (fieldId.equals(ID_INDICATIONS))                           parseIndications(file, fieldId);
         else if (fieldId.equals(ID_LYRICS))               lyrics         = parseString     (file, fieldId);
         else if (fieldId.equals(ID_ADDITIONAL_INFO))      additionalInfo = parseString     (file, fieldId);
         else if (fieldId.equals(ID_AUTHOR_NAME))          authorName     = parseString     (file, fieldId);
         else if (fieldId.equals(ID_EXTENDED_ALBUM_NAME))  albumName      = parseString     (file, fieldId);
         else if (fieldId.equals(ID_EXTENDED_ARTIST_NAME)) artistName     = parseString     (file, fieldId);
         else if (fieldId.equals(ID_EXTENDED_TRACK_TITLE)) trackTitle     = parseString     (file, fieldId);
         else if (fieldId.equals(ID_IMAGE_LINK))           imageLink      = parseString     (file, fieldId);
         else if (fieldId.equals(ID_CRC))                  crc            = parseString     (file, fieldId);
         else throw new ParseException("Invalid field, " + fieldId + ", found in the Lyrics3v2 tag.");
         numBytesRead = (int)(file.getFilePointer() - filePosition);
      }
      while (numBytesRead < endSize);
   }

   /**
    * read in a field's size bytes and corresponding text from the Lyrics3v2 tag.
    * @param file   input stream to read in a field from the Lyrics3v2 tag in an .mp3 file.
    * @param fieldId       the id of the Lyrics3v2 field being parsed.
    * @return the text value of the Lyrics3v2 field read in from the .mp3 file.
    * @throws IOException      if there is an error while reading the bytes from the .mp3 file.
    * @throws ParseException   if the end of the .mp3 file is reached or if the field in the Lyrics3v2 tag can not be parsed.
    */
   private static String parseString(RandomAccessFile file, String fieldId) throws IOException, ParseException
   {
      byte[] bytes = read(file, FIELD_SIZE_SIZE);
      String ssize = new String(bytes, CHARACTER_SET);
      int    size  = 0;

      try
      {
         size = Integer.parseInt(ssize);
      }
      catch (NumberFormatException ex)
      {
         throw new ParseException("Unable to parse the size, " + ssize + ", from the " + fieldId + " field in the Lyrics3v2 tag.");
      }
      bytes = read(file, size);

      return new String(bytes, CHARACTER_SET);
   }

   /**
    * read in a field's size bytes and the corresponding text from the Lyrics3v2 tag.
    * @param file     input stream used to read in a field from the Lyrics3v2 tag in an .mp3 file.
    * @param fieldId  the id of the Lyrics3v2 field being parsed.
    * @throws IOException      if there is an error while reading the bytes from the .mp3 file.
    * @throws ParseException   if the end of the .mp3 file is reached or if the field in the Lyrics3v2 tag can not be parsed.
    */
   private void parseIndications(RandomAccessFile file, String fieldId) throws IOException, ParseException
   {
      String indications = parseString(file, fieldId);

      this.lyricsPresent               = indications.charAt(0) == CHAR_ONE;
      this.timestampPresent            = indications.charAt(1) == CHAR_ONE;
      this.randomTrackSelectionAllowed = false; // indications.charAt(2) == CHAR_ZERO;   documentation at http://id3.org/Lyrics3v2 is wrong
   }

   /**
    * read the specified number of bytes from the .mp3 file.
    * @param file       random access file to read in the Lyrics3v2 tag from an .mp3 file.
    * @param numBytes   the number of bytes to read in from the .mp3 file.
    * @return the bytes read in from the .mp3 file.
    * @throws IOException      if there is an error while reading the bytes from the .mp3 file.
    * @throws ParseException   if the end of the .mp3 file is reached.
    */
   private static byte[] read(RandomAccessFile file, int numBytes) throws IOException, ParseException
   {
      // read the specified number of bytes from the Lyrics3v2 tag
      byte[] bytes        = new byte[numBytes];
      int    numBytesRead = file.read(bytes);

      if (numBytesRead != bytes.length)
      {
         if (numBytesRead == -1)
            throw new ParseException("EOF", bytes);

         bytes = new byte[1];
         if (file.read(bytes) == -1)
            throw new ParseException("EOF", bytes);
         throw new IOException("Unable to read Lyrics3v2 tag.");
      }
      return bytes;
   }

   /**
    * read in the end field's size bytes and the end id from the Lyrics3v2 tag.  This method is used in locating the Lyrics3v2 tag within the .mp3 file.
    * @param file   random access file pointing to the expected location of the Lyrics3v2 tag in an .mp3 file.
    * @return the size of the Lyrics3v2 tag.
    * @throws IOException      if there is an error while reading the bytes from the .mp3 file.
    * @throws ParseException   if the end of the .mp3 file is reached or if the end id in the Lyrics3v2 tag can not be parsed.
    */
   private int findEnd(RandomAccessFile file) throws IOException, ParseException
   {
      byte[] bytes      = read(file, END_FIELD_SIZE);
      byte[] sizeBytes  = new byte[END_FIELD_SIZE_SIZE];
      byte[] endIdBytes = new byte[ID_END.length()];
      int    size       = 0;
      String ssize      = null;

      System.arraycopy(bytes, 0                  , sizeBytes , 0, END_FIELD_SIZE_SIZE);
      System.arraycopy(bytes, END_FIELD_SIZE_SIZE, endIdBytes, 0, ID_END.length());

      endId = new String(endIdBytes, CHARACTER_SET);
      if (!endId.equals(ID_END))
         throw new ParseException("Invalid end id, " + endId + ", found in the Lyrics3v2 tag.", bytes);

      try
      {
         ssize = new String(sizeBytes, CHARACTER_SET);
         size  = Integer.parseInt(ssize);
      }
      catch (NumberFormatException ex)
      {
         throw new ParseException("Unable to parse the tag size, " + ssize + ", from the end of the Lyrics3v2 tag.");
      }
      return size;
   }

   /**
    * get whether lyrics are present in the Lyrics3v2 tag.
    * @return whether lyrics are present in the Lyrics3v2 tag.
    */
   public boolean isLyricsPresent()
   {
      return lyricsPresent;
   }

   /**
    * get whether the lyrics contain timestamps in the Lyrics3v2 tag.
    * @return whether the lyrics contain timestamps in the Lyrics3v2 tag.
    */
   public boolean isTimestampPresent()
   {
      return timestampPresent;
   }

   /**
    * get whether random track selection is allowed.
    * @return whether random track selection is allowed.
    */
   public boolean isRandomTrackSelectionAllowed()
   {
      return randomTrackSelectionAllowed;
   }

   /**
    * get the lyrics in the Lyrics3v2 tag.
    * @return the song lyrics in the Lyrics3v2 tag, or null if the tag does not contain any.
    */
   public String getLyrics()
   {
      return lyrics;
   }

   /**
    * get the additional information in the Lyrics3v2 tag.
    * @return the additional information in the Lyrics3v2 tag, or null if the tag does not contain any.
    */
   public String getAdditionalInfo()
   {
      return additionalInfo;
   }

   /**
    * get the music/lyrics author's name in the Lyrics3v2 tag.
    * @return the music/lyrics author's name in the Lyrics3v2 tag, or null if no author's name has been specified in the tag.
    */
   public String getAuthorName()
   {
      return authorName;
   }

   /**
    * get the album name in the Lyrics3v2 tag.
    * @return the album name in the Lyrics3v2 tag, or null if no album name has been specified in the tag.
    */
   public String getAlbumName()
   {
      return albumName;
   }

   /**
    * get the artist name in the Lyrics3v2 tag.
    * @return the artist name in the Lyrics3v2 tag, or null if no artist's name has been specified in the tag.
    */
   public String getArtistName()
   {
      return artistName;
   }

   /**
    * get the track title in the Lyrics3v2 tag.
    * @return the track title in the Lyrics3v2 tag, or null if no track title has been specified in the tag.
    */
   public String getTrackTitle()
   {
      return trackTitle;
   }

   /**
    * get the link to an image file in .bmp or .jpg format.
    * @return the link to an image file in .bmp or .jpg format, or null if no image link has been specified in the tag.
    */
   public String getImageLink()
   {
      return imageLink;
   }

   /**
    * get the CRC of the Lyrics3v2 tag.
    * @return the CRC of the Lyrics3v2 tag, or null if the tag does not contain one.
    */
   public String getCRC()
   {
      return crc;
   }

   /**
    * get the size (in bytes) of the entire Lyrics3v2 tag, including the end id.
    * @return the size (in bytes) of the entire Lyrics3v2 tag, including the end id.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * get the position (in bytes) within the .mp3 file where the Lyrics3v2 tag starts.
    * @return the position (in bytes) within the .mp3 file where the Lyrics3v2 tag starts.
    */
   public int getFilePosition()
   {
      return filePosition;
   }

   /**
    * gets a string representation of the Lyrics3v2 tag.
    * @return a string representation of the Lyrics3v2 tag.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("lyrics3v2 tag\n");
      buffer.append("   begin id......................: " + beginId                     + "\n");
      buffer.append("   file position.................: " + filePosition                + "\n");
      buffer.append("   size..........................: " + size                        + " bytes\n");
      buffer.append("   lyrics present................: " + lyricsPresent               + "\n");
      buffer.append("   timstamps present.............: " + timestampPresent            + "\n");
      buffer.append("   random track selection allowed: " + randomTrackSelectionAllowed + "\n");
      buffer.append("   author name...................: " + authorName                  + "\n");
      buffer.append("   album name....................: " + albumName                   + "\n");
      buffer.append("   artist name...................: " + artistName                  + "\n");
      buffer.append("   track title...................: " + trackTitle                  + "\n");
      buffer.append("   image link....................: " + imageLink                   + "\n");
      buffer.append("   additional information........: " + additionalInfo              + "\n");
      buffer.append("   crc...........................: " + crc                         + "\n");
      buffer.append("   end size......................: " + size                        + "\n");
      buffer.append("   end id........................: " + endId                       + "\n");
      buffer.append("   lyrics........................: " + lyrics                            );

      return buffer.toString();
   }
}
