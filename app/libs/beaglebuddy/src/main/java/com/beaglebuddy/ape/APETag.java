package com.beaglebuddy.ape;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Vector;

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
 * This class provides methods for reading the optional <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv1</a> and
 * <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">APEv2</a> tags.  The APE tag was meant to provide the same functionality as the
 * ID3v2.x tag, but was not widely adopted.  The ID3v2.x tag, on the other hand, is a formal specification and is supported by all .mp3 players.
 * The <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv1</a>
 * tag can only be found at the end of an .mp3 file, after the {@link com.beaglebuddy.mpeg.MPEGFrame mpeg audio}, and if an {@link com.beaglebuddy.id3.v1.ID3v1Tag  ID3v1 tag}
 * is present, before the ID3v1 tag.  <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">Version 2</a> of the APE Specification added an
 * optional header to the tag, thus allowing an APE tag to be located at the beginning of an .mp3 file where the {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v23 tag} would
 * normally be. See the format of an .mp3 file shown below.
 * </p>
 * <p class="beaglebuddy">
 * An APE tag consists of a collection of key/value pairs which are stored in {@link APEItem items}.  While the key part of an {@link APEItem item} is always a <i>String</i>,
 * the value may be a <i>String</i> or it may be raw <i>binary</i> data, such as an image file.  The sample code shown below demonstrates how to read the values stored in an
 * APE tag.  The APE specification(s) have defined a standardized <a href="http://wiki.hydrogenaud.io/index.php?title=APE_key" target="_blank">list of keys</a>, but users
 * are not restricted to this list.
 * </p>
 * <p class="beaglebuddy">
 * The APE tag is an obsolete tag that really should just be removed from your .mp3 files.  See the
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
 *             <img src="../../../resources/ape_format.jpg" height="280" width="315" alt="APE Tag format" usemap="#ape_map"/>
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 * <map name="id3v23_map">
 *    <area shape="rect" coords=" 230, 145, 300, 165" href="../id3/v23/ID3v23Tag.html"               alt="ID3v2.3 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="../id3/v23/ID3v23TagHeader.html"         alt="ID3v2.3 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="../id3/v23/ID3v23TagExtendedHeader.html" alt="ID3v2.3 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="../id3/v23/ID3v23Frame.html"             alt="ID3v2.3 Frame""/>
 *    <area shape="rect" coords="   6, 287, 198, 374" href="../mpeg/MPEGFrame.html"                  alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 375, 198, 425" href="../lyrics3/Lyrics3v2Tag.html"            alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 426, 198, 479" href="APETag.html"                             alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 480, 198, 530" href="../id3/v1/ID3v1Tag.html"                 alt="ID3V1 Tag"/>
 * </map>
 * <map name="ape_map">
 *    <area shape="rect" coords=" 245, 134, 307, 150" href="APEItem.html"     alt="APE Tag Item"/>
 *    <area shape="rect" coords="  10,  40, 200,  74" href="APEHeader.html"   alt="APE Tag Header"/>
 *    <area shape="rect" coords="  10,  75, 200, 225" href="APEItem.html"     alt="APE Tag Item"/>
 *    <area shape="rect" coords="  10, 226, 200, 260" href="APEFooter.html"   alt="APE Tag Footer"/>
 * </map>
 * </p>
 * <pre class="beaglebuddy">
 * <code>
 * import java.io.IOException;
 * import com.beaglebuddy.mp3.MP3;
 * import com.beaglebuddy.ape.APETag;
 * import com.beaglebuddy.ape.APEItem;
 *
 * public class APEExample
 * {
 *    public static void main(String[] args)
 *    {
 *       try
 *       {
 *          MP3 mp3 = new MP3("c:/mp3/Wild Cat.mp3");
 *
 *          if (mp3.hasAPETag())                   // if the mp3 file has an APE tag
 *          {                                      // then display the data in it
 *             APETag apeTag = mp3.getAPETag();
 *             System.out.println("mp3 contains an " + apeTag.getVersionString() + " tag");
 *             System.out.println(apeTag);
 *             for(APEItem item : apeTag.getItems())
 *             {
 *                if (item.isValueText())
 *                   System.out.println(item.getKey() + " - " + item.getTextValue());
 *                else
 *                   System.out.println(item.getKey() + " - " + "binary data: " + item.getBinaryValue().length + " bytes.");
 *             }
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
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv1 Specification</a>
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">APEv2 Specification</a>
 */
public class APETag
{
   // data members
   private int             filePosition;      // position within the .mp3 file where the APE tag occurs
   private int             size;              // size    of the APE tag
   private int             version;           // version of the APE tag
   private APEHeader       header;            // APE header, if present
   private Vector<APEItem> items;             // list of APE items
   private APEFooter       footer;            // APE footer, if present



   /**
    * constructor used to read in a APEv2 tag from an .mp3 file.
    * @param file  random access file to read in the APE tag from an .mp3 file.
    * @throws IOException      if there is an error while reading the APE tag.
    * @throws ParseException   if a APE tag can not be found or if an invalid value is detected while parsing the APE tag.
    */
   public APETag(RandomAccessFile file) throws IOException, ParseException
   {
      // the APEv2 tag contains a header and can thus be found at the beginning of the .mp3 file
      try
      {
         byte[] bytes = new byte[APEHeader.SIZE];
         if (file.read(bytes) != bytes.length)
            throw new IOException("Unable to read the APEv2 tag header.");
         header = new APEHeader(bytes);
         filePosition = 0;
      }
      catch (ParseException ex)
      {  // both the APEv1 and APEv2 tags have footers and can be found at the end of the .mp3 file
         findEnd(file);
      }
      int numItems = 0;

      if (header != null)
      {
         version  = header.getVersion();
         size     = header.getTagSize() + header.getSize();
         numItems = header.getNumItems();
      }
      else
      {
         version  = footer.getVersion();
         size     = footer.getTagSize() + (version == 2 && footer.getFlags().isTagContainsHeader() ? APEHeader.SIZE : 0);
         numItems = footer.getNumItems();
      }

      // an APE header or footer was found, and we have seeked to the list of items
      byte[] bytes = new byte[header != null ? header.getTagSize() : footer.getTagSize()];

      if (file.read(bytes) != bytes.length)
         throw new IOException("Unable to read the APE tag items.");

      items = new Vector<APEItem>();
      int index = 0;
      for(int i=0; i<numItems; ++i)
      {
         APEItem item = new APEItem(bytes, index);
         items.add(item);
         index += item.getSize();
      }
      // see if we need to read in the footer
      if (index < size && footer == null && (size - index >= APEFooter.SIZE))
      {
         byte[] fbytes = new byte[APEFooter.SIZE];
         System.arraycopy(bytes, index, fbytes, 0, fbytes.length);
         footer = new APEFooter(fbytes);
      }
   }

   /**
    * searches for the APE footer at the end of the file.
    * @param file   random access file for the .mp3 file.
    * @throws IOException      if there is an error while reading the bytes from the .mp3 file.
    * @throws ParseException   if the end of the .mp3 file is reached or if the APE footer can not be parsed.
    */
   private void findEnd(RandomAccessFile file) throws IOException, ParseException
   {
      // no APEv2 tag was found at the beginning of the .mp3 ile, so lets take a look at the end of the file
      byte[] bytes = new byte[APEFooter.SIZE];
      try
      {
         file.seek(file.length() - APEFooter.SIZE);
         if (file.read(bytes) != bytes.length)
            throw new IOException("Unable to read the APE tag footer.");
         footer = new APEFooter(bytes);
         // an APE footer was found, so seek to the beginning of the items
         file.seek(file.length() - footer.getTagSize());
      }
      catch (ParseException e)
      {
         // no APE footer was found at the end of the .mp3 file, so lets see if there is one before an ID3v1 tag
         file.seek(file.length() - ID3v1Tag.TAG_SIZE - APEFooter.SIZE);
         if (file.read(bytes) != bytes.length)
            throw new IOException("Unable to read the APE tag footer.");
         footer = new APEFooter(bytes);
         // an APE footer was found, so seek to the beginning of the items
         file.seek(file.length() - ID3v1Tag.TAG_SIZE - footer.getTagSize());
      }
      filePosition = (int)file.getFilePointer();

      // the APE footer was found and the file pointer was moved to the beginning of the items
      // see if the APE tag has a header as well
      if (footer.getFlags().isTagContainsHeader())
      {
         file.seek(file.getFilePointer() - APEHeader.SIZE);
         filePosition = (int)file.getFilePointer();
         if (file.read(bytes) != bytes.length)
            throw new IOException("Unable to read the APE tag header.");
         header = new APEHeader(bytes);
      }
   }

   /**
    * get the size (in bytes) of the APE tag.
    * @return the size (in bytes) of the APE tag.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * get the position (in bytes) within the .mp3 file where the APE tag starts.
    * @return the position (in bytes) within the .mp3 file where the APE tag starts.
    */
   public int getFilePosition()
   {
      return filePosition;
   }

   /**
    * get the version of the APE tag.
    * @return the version of the APE tag.
    */
   public int getVersion()
   {
      return header == null ? footer.getVersion() : header.getVersion();
   }

   /**
    * get the version of the APE tag as a String.
    * @return the version of the APE tagas a String.
    */
   public String getVersionString()
   {
      return "APEv" + getVersion();
   }

   /**
    * get the optional header in the APE tag.  The header is only found in APEv2 tags.
    * @return the header in the APE tag, or null if it is not present,.
    */
   public APEHeader getHeader()
   {
      return header;
   }

   /**
    * get the list of items in the APE tag.
    * @return the list of items in the APE tag.
    */
   public List<APEItem> getItems()
   {
      return items;
   }

   /**
    * get the optional footer in the APE tag.  The footer is found in both APEv1 and APEv2 tags.
    * @return the footer in the APE tag, or null if it is not present,.
    */
   public APEFooter getFooter()
   {
      return footer;
   }

   /**
    * gets a string representation of the APE tag.
    * @return a string representation of the APE tag.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("APEv" + getVersion()+ " tag\n");
      buffer.append("   file position: " + filePosition  + "\n");
      buffer.append("   size.........: " + size          + " bytes\n");
      buffer.append("   header.......: " + header        + "\n");
      buffer.append("   items........: " + items.size()  + "\n");
      for(APEItem item : items) {
         buffer.append("      " + item                      + "\n");
      }
      buffer.append("   footer.......: " + footer              );

      return buffer.toString();
   }
}
