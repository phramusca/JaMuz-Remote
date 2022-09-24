package com.beaglebuddy.ape;

import java.io.UnsupportedEncodingException;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.util.Utility;




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
 * This class provides methods for reading the APE footer in an {@link APETag APE tag}.  The APE tag footer is found in both
 * <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv1</a> and
 * <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">APEv2</a> tags.
 * It is 32 bytes long.
 * </p>
 * <p>
 * <img src="../../../resources/ape_format.jpg" height="280" width="315" alt="APE Tag format" usemap="#ape_map"/>
 * <map name="ape_map">
 *    <area shape="rect" coords="  47,   9, 150,  25" href="APETag.html"      alt="APE Tag"/>
 *    <area shape="rect" coords=" 245, 134, 307, 150" href="APEItem.html"     alt="APE Tag Item"/>
 *    <area shape="rect" coords="  10,  40, 200,  74" href="APEHeader.html"   alt="APE Tag Header"/>
 *    <area shape="rect" coords="  10,  75, 200, 225" href="APEItem.html"     alt="APE Tag Item"/>
 *    <area shape="rect" coords="  10, 226, 200, 260" href="APEFooter.html"   alt="APE Tag Footer"/>
 * </map>
 * </p>
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv1 Specification</a>
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">APEv2 Specification</a>
 */
public class APEFooter
{
   // class mnemonics
   private static final String ID                      = "APETAGEX";
   private static final int    SIZE_ID                 = 8;            //  8 bytes long
   private static final int    SIZE_VERSION            = 4;            //  4 bytes long
   private static final int    SIZE_TAG_SIZE           = 4;            //  4 bytes long
   private static final int    SIZE_NUM_ITEMS          = 4;            //  4 bytes long
                                                                       /** size (in bytes) of an APE header/footer */
   public  static final int    SIZE                    = 32;           // 32 bytes long

   // data members
   private String   id;
   private int      size;
   private int      version;
   private int      tagSize;
   private int      numItems;
   private APEFlags flags;


   /**
    * constructor.
    * @param bytes   bytes from an APE tag.
    * @throws ParseException  if the bytes do not contain a valid APE footer.
    */
   public APEFooter(byte[] bytes) throws ParseException
   {
      this(bytes, "footer");
   }

   /**
    * constructor.
    * @param bytes   bytes from an APE tag.
    * @param type    whether this is an APE header or a footer.
    * @throws ParseException  if the bytes do not contain a valid APE footer.
    */
   public APEFooter(byte[] bytes, String type) throws ParseException
   {
      if (bytes.length < SIZE)
         throw new ParseException("Invalid size, " + bytes.length + ", of APE " + type + ".");

      // parse the id
      try
      {
         id = new String(bytes, 0, ID.length(), "ISO-8859-1");
      }
      catch (UnsupportedEncodingException ex)
      {
         id = new String(bytes, 0, ID.length());
      }
      if (!id.equals(ID))
         throw new ParseException("Invalid id, " + id + ", in APE " + type + ".");

      // parse the version
      version = Utility.littleEndianBytesToInt(bytes, SIZE_ID);
           if (version == 1000) version = 1;
      else if (version == 2000) version = 2;
      else throw new ParseException("Invalid version, " + version + ", in APE " + type + ".");

      // parse the tag size
      tagSize = Utility.littleEndianBytesToInt(bytes, SIZE_ID + SIZE_VERSION);

      // parse the number of items in the tag
      numItems = Utility.littleEndianBytesToInt(bytes, SIZE_ID + SIZE_VERSION + SIZE_TAG_SIZE);

      // parse the flags
      flags = new APEFlags(bytes, SIZE_ID + SIZE_VERSION + SIZE_TAG_SIZE + SIZE_NUM_ITEMS);

      if (type.equals("footer") && !flags.isFooter())
         throw new ParseException("Invalid APE footer flag type.");
      if (type.equals("header") && !flags.isHeader())
         throw new ParseException("Invalid APE header flag type.");

      size = SIZE;
   }


   /**
    * gets the size of the APE footer.
    * @return the size of the APE footer.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * gets the version of the APE tag.
    * @return the version of the APE tag.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * gets the size of the APE tag, including the footer, but not the header.
    * @return the size of the APE tag, including the footer, but not the header.
    */
   public int getTagSize()
   {
      return tagSize;
   }

   /**
    * gets the number of items in the APE tag.
    * @return the number of items in the APE tag.
    */
   public int getNumItems()
   {
      return numItems;
   }


   /**
    * gets the flags associated with the APE tag.
    * @return the flags associated with the APE tag.
    */
   public APEFlags getFlags()
   {
      return flags;
   }

   /**
    * gets a string representation of the APE footer.
    * @return a string representation of the APE footer.
    */
   @Override
   public String toString()
   {
      return toString("footer");
   }

   /**
    * gets a string representation of the APE footer/header.
    * @param type  whether this is an APE header or footer.
    * @return a string representation of the APE footer.
    */
   protected String toString(String type)
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append(       type                               + "\n");
      buffer.append("      size.....: " + size     + " bytes" + "\n");
      buffer.append("      id.......: " + id                  + "\n");
      buffer.append("      version..: " + "APEv"   + version  + "\n");
      buffer.append("      tag size.: " + tagSize  + " bytes" + "\n");
      buffer.append("      num items: " + numItems            + "\n");
      buffer.append("      flags....: " + flags                     );

      return buffer.toString();
   }
}