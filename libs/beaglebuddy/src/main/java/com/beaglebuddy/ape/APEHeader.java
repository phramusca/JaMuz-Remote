package com.beaglebuddy.ape;

import com.beaglebuddy.exception.ParseException;




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
 * This class provides methods for reading the APE header in an {@link APETag APE tag}.  The APE tag header is only found in <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">APEv2</a> tags,
 * and allows an APEv2 tag to be located at the beginning of an .mp3 file instead of the end.  It is 32 bytes long.
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
public class APEHeader extends APEFooter
{
   /**
    * constructor.
    * @param bytes   bytes from an APE tag.
    * @throws ParseException  if the bytes do not contain a valid APE header.
    */
   public APEHeader(byte[] bytes) throws ParseException
   {      super(bytes, "header");
   }

   /**
    * gets a string representation of the APE header.
    * @return a string representation of the APE header.
    */
    @Override
   public String toString()
   {
      return toString("header");
   }
}