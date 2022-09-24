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
 * This class provides methods for reading the optional <a href="http://id3.org/Lyrics3" target="_blank">Lyrics3v1</a> tag , which is found at the end of an .mp3 file
 * after the mpeg audio data and before the ID3v1.x tag as shown below.  It is an obsolete tag that really should just be removed from your .mp3 files.  See the
 * <a href="http://www.beaglebuddy.com/content/pages/more_sample_code/CleanMP3Files.java" target="_blank">CleanMP3Files.java</a> file provided in the
 * <a href="http://www.beaglebuddy.com/content/pages/more_sample_code/file_list.html"     target="_blank">sample code</a> to see how this is done.
 * </p>
 * <p>
 * <table border="0">
 *   <tbody>
 *      <tr>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/mp3_format_ID3v2.3.gif" height="550" width="330" alt="mp3 format containing an ID3v2.3 tag" usemap="#id3v23_map"/>
 *          </td>
 *          <td> &nbsp; &nbsp; &nbsp; </td>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/lyrics3v1_format.jpg" height="140" width="215" alt="Lyrics3v1 Tag format"/>
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
 * </p>
 * <pre class="beaglebuddy">
 * <code>
 * import java.io.IOException;
 * import com.beaglebuddy.mp3.MP3;
 * import com.beaglebuddy.lyrics3.v1.Lyrics3v1Tag;
 *
 * public class Lyrics3v1Example
 * {
 *    public static void main(String[] args)
 *    {
 *       try
 *       {
 *          MP3 mp3 = new MP3("c:/mp3/Wild Cat.mp3");
 *
 *          if (mp3.hasLyrics3v1Tag())                   // if the mp3 file has a Lyrics3v1 tag
 *          {                                            // then display the lyrics in it
 *             Lyrics3v1Tag lyrics3v1Tag = mp3.getLyrics3v1Tag();
 *             System.out.println("mp3 contains a Lyrics3v1 tag");
 *
 *             System.out.println(lyrics3v1Tag.getLyrics());
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
 * @see Lyrics3v2Tag
 * @see <a href="http://id3.org/Lyrics3"   target="_blank">Lyrics3v1 Spec</a>
 * @see <a href="http://id3.org/Lyrics3v2" target="_blank">Lyrics3v2 Spec</a>
 */
public class Lyrics3v1Tag
{
   // class mnemonics
   private static final String ID_BEGIN        = "LYRICSBEGIN";
   private static final String ID_END          = "LYRICSEND";
   private static final String CHARACTER_SET   = "ISO-8859-1";
   private static final int    MAX_LYRICS_SIZE = 5100;
   private static final int    LENGTH_ID_BEGIN = ID_BEGIN.length();
   private static final int    LENGTH_ID_END   = ID_END  .length();

   // data members
   private int    filePosition;                      // position within the .mp3 file where the Lyrics3v1 tag occurs
   private int    size;                              // size of the Lyrics3v1 tag including the end id
   private String beginId;                           // unique id indicating that a Lyrics3v1 tag is present in the .mp3 file
   private String lyrics;
   private String endId;                             // ending id indicating that a Lyrics3v1 tag is present in the .mp3 file



   /**
    * constructor used to read in a Lyrics3v1 tag from an .mp3 file.
    * @param file   random access file to read in the Lyrics3v1 tag from an .mp3 file.
    * @throws IOException      if there is an error while reading the Lyrics3v1 tag.
    * @throws ParseException   if a Lyrics3v1 tag can not be found or if an invalid value is detected while parsing the Lyrics3v1 tag.
    */
   public Lyrics3v1Tag(RandomAccessFile file) throws IOException, ParseException
   {
      // the Lyrics3v1 is found right before the ID3v1 tag at the end of the .mp3 file
      // we're looking for the end of the Lyrics3v1 tag, which contains the size of the tag
      filePosition = (int)(file.length() - ID3v1Tag.TAG_SIZE - LENGTH_ID_END);
      file.seek(filePosition);

      byte[] bytes = read(file, LENGTH_ID_END);
             endId = new String(bytes, CHARACTER_SET);

      if (!endId.equals(ID_END))
      {
         // in a perfect world, the Lyrics3v1 tag is found right before the ID3v1 tag
         // but sometimes people remove ID3v1 tag without realizing that a Lyrics3v1 is also present in their .mp3's
         // so let's check at the end of the file as well
         filePosition += ID3v1Tag.TAG_SIZE;
         file.seek(filePosition);
         bytes = read(file, LENGTH_ID_END);
         endId = new String(bytes, CHARACTER_SET);
         if (!endId.equals(ID_END))
            throw new ParseException("Invalid id, " + endId + ", found in the Lyrics3v1 tag.", bytes);
      }
      file.seek(filePosition);                    // seek to end id
      findBeginning(file);       // find the beginning id
   }

   /**
    * find the beginning of the Lyrics3v1 tag.  This method is used in locating the Lyrics3v1 tag within the .mp3 file.
    * Once the begging id is found, the lyrics are parsed from the text between the beginning and ending id's.
    * @param file   random access file pointing to the end of the Lyrics3v1 tag in an .mp3 file.
    * @throws IOException      if there is an error while reading the bytes from the .mp3 file.
    * @throws ParseException   if the end of the .mp3 file is reached or if the end id in the Lyrics3v1 tag can not be parsed.
    */
   private void findBeginning(RandomAccessFile file) throws IOException, ParseException
   {
      byte[]  bytes = read(file, MAX_LYRICS_SIZE);
      byte    L     = (byte)76;        // 'L'
      byte    Y     = (byte)89;        // 'Y'
      boolean found = false;
      int     index = 0;

      // start from the end of the Lyrics3v1 tag and find 'L's
      for(index=bytes.length - LENGTH_ID_BEGIN - 1; index >= 0 && !found; --index)
      {
         if (bytes[index] == L && bytes[index + 1] == Y)
         {
            beginId = new String(bytes, index, LENGTH_ID_BEGIN, CHARACTER_SET);
            found   = beginId.equals(ID_BEGIN);
         }
      }
      if (!found)
         throw new ParseException("Beginning id not found in the Lyrics3v1 tag.");

      lyrics        = new String(bytes, index + LENGTH_ID_BEGIN, MAX_LYRICS_SIZE - index - LENGTH_ID_BEGIN, CHARACTER_SET);
      size          = LENGTH_ID_BEGIN + LENGTH_ID_END + lyrics.length();
      filePosition -= (lyrics.length() + LENGTH_ID_BEGIN);
   }

   /**
    * read the specified number of bytes from the .mp3 file.
    * @param file   random access file pointing to a Lyrics3v1 tag in an .mp3 file.
    * @param numBytes      the number of bytes to read in from the .mp3 file.
    * @return the bytes read in from the .mp3 file.
    * @throws IOException      if there is an error while reading the bytes from the .mp3 file.
    * @throws ParseException   if the end of the .mp3 file is reached.
    */
   private static byte[] read(RandomAccessFile file, int numBytes) throws IOException, ParseException
   {
      // read the specified number of bytes from the Lyrics3v1 tag
      byte[] bytes        = new byte[numBytes];
      int    numBytesRead = file.read(bytes);

      if (numBytesRead != bytes.length)
      {
         if (numBytesRead == -1)
            throw new ParseException("EOF", bytes);

         bytes = new byte[1];
         if (file.read(bytes) == -1)
            throw new ParseException("EOF", bytes);
         throw new IOException("Unable to read Lyrics3v1 tag.");
      }
      return bytes;
   }

   /**
    * get the size (in bytes) of the entire Lyrics3v1 tag, including the end id.
    * @return the size (in bytes) of the entire Lyrics3v1 tag, including the end id.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * get the position (in bytes) within the .mp3 file where the Lyrics3v1 tag starts.
    * @return the position (in bytes) within the .mp3 file where the Lyrics3v1 tag starts.
    */
   public int getFilePosition()
   {
      return filePosition;
   }

   /**
    * gets the lyrics to the song.
    * @return the lyrics to the song.
    */
   public String getLyrics()
   {
      return lyrics;
   }

   /**
    * gets a string representation of the Lyrics3v1 tag.
    * @return a string representation of the Lyrics3v1 tag.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("lyrics3 v1 tag\n");
      buffer.append("   begin id.....: " + beginId      + "\n");
      buffer.append("   file position: " + filePosition + "\n");
      buffer.append("   size.........: " + size         + " bytes\n");
      buffer.append("   lyrics.......: " + lyrics       + "\n");
      buffer.append("   end id.......: " + beginId            );

      return buffer.toString();
   }
}
