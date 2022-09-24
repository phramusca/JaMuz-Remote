package com.beaglebuddy.ape;




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
 * This class provides methods for reading the APE flags which are found in an {@link APEHeader APE header}, {@link APEFooter APE footer}, and in {@link APEItem APE item}.
 * The APE flags, while present in <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv1</a>, are all zero's and are not used.
 * In <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv2</a>, the flag bits were assigned meanings.
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
public class APEFlags
{
    /** valid APE value types */
   public enum Type
   {                       /** item contains text information encoded in UTF-8.    */
      UTF_8        ,       /** item contains binary information.                   */
      BINARY       ,       /** item is a locator of externally stored information. */
      EXTERNAL_LINK,       /** reserved for future use.                            */
      RESERVED     ;

      /** @return a string representation of the APE Type */
      public String toString()  {return "" + ordinal() + " - " + super.toString().toLowerCase();}

      /**
       * convert an integral value to its corresponding enum.
       * @param type   the integral value that is to be converted to an APE Type enum.
       * @return the APE Type enum whose ordinal value corresponds to the given integral value.
       * @throws IllegalArgumentException   if there is no APE Type enum whose ordinal value corresponds to the given integral value.
       */
      public static Type valueOf(int type)
      {
         for (Type t : Type.values())
            if (type == t.ordinal())
               return t;
         throw new IllegalArgumentException("Invalid APE type " + type + ".");
      }
   }

   // class mnemonics
   private static final int  SIZE = 4;                                // flags field is 4 bytes long
   private static final byte MASK_TAG_CONTAINS_HEADER = (byte)0x80;
   private static final byte MASK_TAG_CONTAINS_FOOTER = (byte)0x40;
   private static final byte MASK_IS_HEADER           = (byte)0x20;
   private static final byte MASK_TYPE                = (byte)0x06;
   private static final byte MASK_READ_ONLY           = (byte)0x01;

   // data members
   private boolean tagContainsHeader;
   private boolean tagContainsFooter;
   private boolean header;
   private Type    type;
   private boolean readOnly;


   
   /**
    * constructor.
    * @param bytes   bytes from an APE tag.
    * @param index   offset into the byte stream where the flag bytes are located.
    */
   public APEFlags(byte[] bytes, int index)
   {      tagContainsHeader = (bytes[index + 3] & MASK_TAG_CONTAINS_HEADER) != 0;
      tagContainsFooter = (bytes[index + 3] & MASK_TAG_CONTAINS_FOOTER) != 0;
      header            = (bytes[index + 3] & MASK_IS_HEADER          ) != 0;
      readOnly          = (bytes[index    ] & MASK_READ_ONLY          ) != 0;
      type              = Type.valueOf(bytes[index + 3] & MASK_TYPE);
   }

   /**
    * gets whether the APE tag contains a header.
    * @return whether the APE tag contains a header.
    */
   public boolean isTagContainsHeader()
   {
      return tagContainsHeader;
   }

   /**
    * gets whether the APE tag contains a footer.
    * @return whether the APE tag contains a footer.
    */
   public boolean isTagContainsFooter()
   {
      return tagContainsFooter;
   }

   /**
    * gets whether this is the APE tag header.
    * @return whether this is the APE tag header.
    */
   public boolean isHeader()
   {
      return header;
   }

   /**
    * gets whether this is the APE tag footer.
    * @return whether this is the APE tag footer.
    */
   public boolean isFooter()
   {
      return !header;
   }

   /**
    * gets the type of data.
    * @return the type of data.
    */
   public Type getType()
   {
      return type;
   }

   /**
    * gets whether this tag or item is read only.
    * @return whether this tag or item is read only.
    */
   public boolean isReadOnly()
   {
      return readOnly;
   }

   /**
    * gets a string representation of the APE flags.
    * @return a string representation of the APE flags.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append(         "flag"                                      + "\n");
      buffer.append("         size...............: " + SIZE              + " bytes\n");
      buffer.append("         tag contains header: " + tagContainsHeader + "\n");
      buffer.append("         tag contains footer: " + tagContainsFooter + "\n");
      buffer.append("         is header..........: " + header            + "\n");
      buffer.append("         data type..........: " + type              + "\n");
      buffer.append("         is read only.......: " + readOnly                );

      return buffer.toString();
   }
}